package com.betolara1.user.dto.request;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
}
