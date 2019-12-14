package com.poc.webflux.controller;

import com.poc.webflux.service.WebfluxAggregateService;
import com.poc.webflux.domain.response.AggregateResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webflux")
public class WebfluxAggregateController {

    private final WebfluxAggregateService service;

    public WebfluxAggregateController(final WebfluxAggregateService webfluxAggregateService) {
        this.service = webfluxAggregateService;
    }

    @GetMapping("/independent")
    public Mono<AggregateResponse> callIndependentRequestsAndAggregateResponses() throws InterruptedException {
        return service.callIndependentRequestsAndAggregateResponses();
    }

    @GetMapping("/dependent")
    public Mono<AggregateResponse> callDependentRequestsAndAggregateResponses() throws InterruptedException {
        return service.callDependentRequestsAndAggregateResponses();
    }
}
