package com.micro_service_api_gateway.api_gateway.Gateway;

import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/circuit-breaker")
public class CircuitBreakerController {

    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    @RequestMapping(value = "/fallback/conversion", method = RequestMethod.GET)
    @Retry(name = "heart-beat",fallbackMethod = "hardCordedMethod")
    public String sampleApi() {

        logger.info("Sample Api");
        ResponseEntity<String> response = new RestTemplate().getForEntity("http://localhost:5050", String.class);

        return response.getBody();
    }

    // The Throwable parameter is used in the fallback method because when a downstream service call fails
    // (due to timeout, network error, or other exception) inside the circuit breaker,
    // the circuit breaker passes that exception to the fallback method.
    // This allows the fallback method to know the reason for failure and handle it appropriately.

    public String hardCordedMethod(Throwable ex){

        return "Error Message"+ex.getMessage();
    }


}
