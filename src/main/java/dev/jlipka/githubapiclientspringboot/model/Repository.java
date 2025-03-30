package dev.jlipka.githubapiclientspringboot.model;

import java.util.List;

public record Repository(
        String name,
        String owner,
        List<Branch> branches
) {
}
