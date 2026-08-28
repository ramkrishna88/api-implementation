package com.api_implementation.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.Locale;

@RestController
public class ApiGatewayController {

    private final RestClient restClient;
    private final String userServiceUrl;
    private final String productServiceUrl;
    private final String orderServiceUrl;
    private final String cartServiceUrl;

    public ApiGatewayController(
            @Value("${service.user.url}") String userServiceUrl,
            @Value("${service.product.url}") String productServiceUrl,
            @Value("${service.order.url}") String orderServiceUrl,
            @Value("${service.cart.url}") String cartServiceUrl) {
        this.restClient = RestClient.builder().build();
        this.userServiceUrl = userServiceUrl;
        this.productServiceUrl = productServiceUrl;
        this.orderServiceUrl = orderServiceUrl;
        this.cartServiceUrl = cartServiceUrl;
    }

    @RequestMapping(
            value = {"/api/{service}", "/api/{service}/{*path}"},
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
                    RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<byte[]> forward(
            @PathVariable String service,
            @PathVariable(required = false) String path,
            HttpServletRequest request,
            @RequestHeader HttpHeaders incomingHeaders,
            @RequestBody(required = false) byte[] body) {

        String serviceUrl = serviceUrlFor(service);
        if (serviceUrl == null) {
            return ResponseEntity.notFound().build();
        }

        String targetPath = "/api/" + service + (path == null ? "" : "/" + path);
        String query = request.getQueryString();
        URI targetUri = URI.create(serviceUrl + targetPath + (query == null ? "" : "?" + query));

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        RestClient.RequestBodySpec requestSpec = restClient.method(method).uri(targetUri);
        copyForwardableHeaders(incomingHeaders, requestSpec);

        try {
            RestClient.RequestHeadersSpec<?> outgoingRequest = body == null
                    ? requestSpec
                    : requestSpec.body(body);
            ResponseEntity<byte[]> response = outgoingRequest.retrieve().toEntity(byte[].class);
            return responseWithForwardableHeaders(
                    response.getStatusCode(), response.getHeaders(), response.getBody());
        } catch (RestClientResponseException exception) {
            return responseWithForwardableHeaders(
                    exception.getStatusCode(),
                    exception.getResponseHeaders(),
                    exception.getResponseBodyAsByteArray());
        } catch (RestClientException exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String serviceUrlFor(String service) {
        return switch (service) {
            case "users" -> userServiceUrl;
            case "products" -> productServiceUrl;
            case "orders" -> orderServiceUrl;
            case "cart" -> cartServiceUrl;
            default -> null;
        };
    }

    private void copyForwardableHeaders(HttpHeaders incomingHeaders, RestClient.RequestHeadersSpec<?> requestSpec) {
        incomingHeaders.forEach((name, values) -> {
            if (!name.equalsIgnoreCase(HttpHeaders.HOST)
                    && !isHopByHopHeader(name)) {
                requestSpec.header(name, values.toArray(String[]::new));
            }
        });
    }

    private ResponseEntity<byte[]> responseWithForwardableHeaders(
            HttpStatusCode status, HttpHeaders sourceHeaders, byte[] body) {
        HttpHeaders responseHeaders = new HttpHeaders();
        if (sourceHeaders != null) {
            sourceHeaders.forEach((name, values) -> {
                if (!isHopByHopHeader(name)) {
                    responseHeaders.put(name, values);
                }
            });
        }
        return ResponseEntity.status(status).headers(responseHeaders).body(body);
    }

    private boolean isHopByHopHeader(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
                    "te", "trailer", "transfer-encoding", "upgrade", "content-length" -> true;
            default -> false;
        };
    }
}
