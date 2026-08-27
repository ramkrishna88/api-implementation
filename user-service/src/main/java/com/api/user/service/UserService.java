package com.api.user.service;

import com.api.user.dto.AddressDTO;
import com.api.user.dto.UserRequest;
import com.api.user.dto.UserResponse;
import com.api.user.model.Address;
import com.api.user.model.User;
import com.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream().map(this::mapToUserResponse).toList();
    }

    public UserResponse addUser(UserRequest request) {
        User user = new User();
        updateUserFromRequest(user, request);
        return mapToUserResponse(userRepository.save(user));
    }

    public Optional<UserResponse> fetchUser(String id) {
        return userRepository.findById(id).map(this::mapToUserResponse);
    }

    public Optional<UserResponse> updateUser(String id, UserRequest request) {
        return userRepository.findById(id).map(existing -> {
            updateUserFromRequest(existing, request);
            return mapToUserResponse(userRepository.save(existing));
        });
    }

    private void updateUserFromRequest(User user, UserRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getAddress() != null) {
            Address address = user.getAddress();
            if (address == null) {
                address = new Address();
                user.setAddress(address);
            }
            address.setStreet(request.getAddress().getStreet());
            address.setCity(request.getAddress().getCity());
            address.setState(request.getAddress().getState());
            address.setCountry(request.getAddress().getCountry());
            address.setZipCode(request.getAddress().getZipCode());
        }
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setAddress(mapToAddressResponse(user.getAddress()));
        return response;
    }

    private AddressDTO mapToAddressResponse(Address address) {
        if (address == null) {
            return null;
        }
        AddressDTO response = new AddressDTO();
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setZipCode(address.getZipCode());
        return response;
    }
}
