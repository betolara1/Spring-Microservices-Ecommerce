package com.betolara1.user.DTO.response;

import com.betolara1.user.model.User;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String role;
    private String name;
    private String email;
    private String phone;
    private String address;

    public UserDTO(){}

    public UserDTO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getAddress();
    }

}