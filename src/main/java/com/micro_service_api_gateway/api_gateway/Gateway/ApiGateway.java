package com.micro_service_api_gateway.api_gateway.Gateway;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ApiGateway {

    // Here we are configuring a custom route using RouteLocator and RouteLocatorBuilder.
    // The line `.filters(f -> f.stripPrefix(1))` helps strip the prefix from the path to avoid sending the full path to the backend,
    // which can cause 404 errors (e.g., currency-conversion-feign/).
    // The same behavior can be achieved using a rewrite filter with a regex to capture the segment after the pattern match and forward it to
    // the service using Eureka load balancing.


    @Bean
    public RouteLocator gatewayRouteLocator(RouteLocatorBuilder builder) {

        Function<PredicateSpec, Buildable<Route>> routeFunction =
                p -> p.path("/get").
                        filters(f -> f.addRequestHeader("EpicShelter", "Hari")
                                .addRequestParameter("Earlybird", "early")).
                        uri("http://httpbin.org:80");

        return builder.routes().route(
                        p -> p.path("/get").
                                filters(f -> f.addRequestParameter("Epicshelter", "Hari")
                                        .addRequestParameter("EarlyBird", "early")).
                                uri("http://httpbin.org:80")
                ).route(p -> p.path("/currency-exchange/**")
                        .filters(f -> f.stripPrefix(1)).
                        uri("lb://currency-exchange")).
                route(p -> p.path("/currency-conversion/**")
                        .filters(f -> f.stripPrefix(1)).
                        uri("lb://currency-conversion")).
                route(p -> p.path("/currency-conversion-feign/**")
                        .filters(f -> f.stripPrefix(1)).
                        uri("lb://currency-conversion")).
                route(p -> p.path("/conversion/**")
                        .filters(f -> f.
                                rewritePath("/conversion/(?<segment>.*)", "/${segment}")).
                        uri("lb://currency-conversion"))
                .route(p -> p.path("/currency/exchange/**").
                        uri("lb://currency-exchange")).
                route(p -> p.path("/currency/conversion-feign/**").
                        uri("lb://currency-conversion"))
                .route(p->p.path("/currency/conversion/**")
                        .filters(f->f.circuitBreaker(config ->
                                config.setName("heart-beat").
                                        setFallbackUri("forward:/circuit-breaker/fallback/conversion"))).
                        uri("lb://currency-conversion")).
                build();
    }

}
