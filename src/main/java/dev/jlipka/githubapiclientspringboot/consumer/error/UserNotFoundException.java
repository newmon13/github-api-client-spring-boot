package dev.jlipka.githubapiclientspringboot.consumer.error;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
