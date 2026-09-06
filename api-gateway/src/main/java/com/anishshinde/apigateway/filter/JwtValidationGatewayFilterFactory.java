package com.anishshinde.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component // for Spring Cloud Gateway to discover and use this class
public class JwtValidationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<Object> {

    // to make HTTP requests from api-gateway to auth-service
    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                             @Value("${auth.service.url}") String authServiceUrl) {
        // Read auth service base URL from configuration
        // for e.g. locally: auth-service:4005
        // or another env e.g.: ecs.aws.someString:5000
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    /*
     * GatewayFilter runs for requests passing through the gateway route.
     *
     * exchange = current HTTP request + response
     * chain    = remaining filters / routing process
     *
     * chain.filter(exchange) = pass current request to next gateway filter
     * and, after all filters have run, to the target downstream service.
     *
     * If chain.filter(exchange) is not called, processing stops in this filter
     * and the request is not forwarded to the downstream service.
     */
    @Override
    public GatewayFilter apply(Object config) {
        return(exchange, chain) ->  {
            // Read Authorization header from incoming client request
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if(token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete(); // HTTP 401
            }

            // GET http://auth-service:4005/validate
            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity()
                    // pass original request onward through gateway to target microservice
                    .then(chain.filter(exchange));

        };
    }

}
