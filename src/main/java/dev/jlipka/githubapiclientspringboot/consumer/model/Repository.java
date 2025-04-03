package dev.jlipka.githubapiclientspringboot.consumer.model;

import java.util.List;

public record Repository(
        String name,
        String owner,
        boolean isFork,
        List<Branch> branches
) {
}
