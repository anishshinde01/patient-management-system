package com.anishshinde.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class JwtValidationException {

    /*
     * Handles the 401 exception thrown when the auth-service rejects a token.
     *
     * Without this handler, the gateway treats the WebClient exception as an
     * internal error and returns 500 to the client even though the real problem is
     * an invalid/expired token.
     */
    @ExceptionHandler(WebClientResponseException.Unauthorized.class)
    public Mono<Void> handleUnauthorizedException(ServerWebExchange exchange) {
        // Return correct 401 Unauthorized status to the original client.
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // Complete response here; do not continue forwarding the request.
        return exchange.getResponse().setComplete();
    }

}
