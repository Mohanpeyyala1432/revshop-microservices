package com.revshop.userservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;
    private String businessName;

    private String street;
    private String city;
    private String state;
    private String pincode;
}
