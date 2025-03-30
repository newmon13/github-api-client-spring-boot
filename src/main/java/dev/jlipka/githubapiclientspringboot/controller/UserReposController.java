package dev.jlipka.githubapiclientspringboot.controller;

import dev.jlipka.githubapiclientspringboot.model.Repository;
import dev.jlipka.githubapiclientspringboot.service.UserReposService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserReposController {
    private final UserReposService userReposService;

    @GetMapping("github/{username}")
    public ResponseEntity<List<Repository>> getUserRepositories(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(userReposService.getUserRepositories(username));
    }
}
