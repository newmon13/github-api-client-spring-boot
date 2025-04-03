package dev.jlipka.githubapiclientspringboot.consumer;

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

    @GetMapping("github/users/{username}")
    public ResponseEntity<List<RepositoryDto>> getUserNonForkRepositories(@PathVariable String username) {
        return ResponseEntity.ok(userReposService.getUserRepositories(username, false));
    }
}