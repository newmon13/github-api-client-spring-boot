package dev.jlipka.githubapiclientspringboot.consumer.error;

public record ErrorResponse(int status, String message) {
}
