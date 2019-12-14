package com.poc.webflux.controller;

import com.poc.webflux.domain.response.AggregateResponse;
import com.poc.webflux.service.AsyncAggregateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/async/")
public class AsyncAggregateController {

    private final AsyncAggregateService service;

    public AsyncAggregateController(final AsyncAggregateService service) {
        this.service = service;
    }

    @GetMapping("/independent")
    public AggregateResponse callIndependentRequestsAndAggregateResponses() throws InterruptedException, ExecutionException {
        return service.callIndependentRequestsAndAggregateResponses();
    }

    @GetMapping("/dependent")
    public AggregateResponse callDependentRequestsAndAggregateResponses() throws InterruptedException, ExecutionException {
        return service.callDependentRequestsAndAggregateResponses();
    }
}
