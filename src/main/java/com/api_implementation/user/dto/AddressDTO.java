package com.api_implementation.user.dto;

import lombok.*;

@Data
public class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
