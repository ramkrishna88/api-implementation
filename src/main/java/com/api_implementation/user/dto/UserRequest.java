package com.api_implementation.user.dto;

import lombok.Data;
import com.api_implementation.user.model.UserRole;

@Data
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;

    private AddressDTO address;
}
