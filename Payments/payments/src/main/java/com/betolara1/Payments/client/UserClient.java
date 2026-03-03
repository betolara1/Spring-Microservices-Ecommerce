package com.betolara1.Payments.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-client", url = "http://user-ms:8080")
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
