package com.pedrocomitto.poc.parallel.controller;

import com.pedrocomitto.poc.parallel.domain.response.AggregateResponse;
import com.pedrocomitto.poc.parallel.domain.response.PostAndUserResponse;
import com.pedrocomitto.poc.parallel.service.WebfluxAggregateService;
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

    @GetMapping("/dependent/functional")
    public Mono<PostAndUserResponse> callDependentRequestsAndAggregateResponsesInAElegantFunctionalWay() throws InterruptedException {
        return service.callDependentRequestsAndAggregateResponsesInAElegantFunctionalWay();
    }
}
