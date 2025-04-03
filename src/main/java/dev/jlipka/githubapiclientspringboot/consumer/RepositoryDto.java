package dev.jlipka.githubapiclientspringboot.consumer;

import dev.jlipka.githubapiclientspringboot.consumer.model.Branch;

import java.util.List;

public record RepositoryDto(String name, String owner, List<Branch> branches) {
}
