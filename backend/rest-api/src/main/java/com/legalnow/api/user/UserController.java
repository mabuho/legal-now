package com.legalnow.api.user;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.user.dto.PublicUserResponse;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/{id}")
    public PublicUserResponse getById(@PathVariable UUID id) {
        return users.findById(id)
            .map(PublicUserResponse::from)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
