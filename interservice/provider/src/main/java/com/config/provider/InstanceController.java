package com.config.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class InstanceController {

    @Value("${server.port}")
    private String port;

    private final String instanceID = UUID.randomUUID().toString();

    @GetMapping("/instance-info")
    public String getInstanceInfo() {
        System.out.println("Request recieveed on the running on the port" + port);
        return "Instance running on teh port " + port + "Instance ID" + instanceID;
    }
}
