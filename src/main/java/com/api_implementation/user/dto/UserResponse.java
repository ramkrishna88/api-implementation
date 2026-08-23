package com.api_implementation.user.dto;

import com.api_implementation.user.model.UserRole;
import lombok.Data;

@Data
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role = UserRole.CUSTOMER;

    private AddressDTO address;
}
