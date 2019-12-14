package com.poc.webflux.service;

import com.poc.webflux.domain.response.AggregateResponse;
import com.poc.webflux.domain.response.PostResponse;
import com.poc.webflux.domain.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class WebfluxAggregateService {

    private final WebClient jsonPlaceholderClient;

    public WebfluxAggregateService(final WebClient jsonPlaceholderClient) {
        this.jsonPlaceholderClient = jsonPlaceholderClient;
    }

    public Mono<AggregateResponse> callIndependentRequestsAndAggregateResponses() throws InterruptedException {
        return aggregatePostAndUser(retrievePost(1), retrieveUser(1));
    }

    public Mono<AggregateResponse> callDependentRequestsAndAggregateResponses() throws InterruptedException {

        Mono<PostResponse> postResponseMono = retrievePost(1);

        Mono<UserResponse> userResponseMono = postResponseMono.map(PostResponse::getUserId).flatMap(this::retrieveUser);

        return aggregatePostAndUser(postResponseMono, userResponseMono);
    }

    private Mono<PostResponse> retrievePost(Integer id) throws InterruptedException {
        log.info("M=retrievePost");
        return jsonPlaceholderClient.get()
                .uri("/posts/" + id)
                .retrieve()
                .bodyToMono(PostResponse.class)
                .delayElement(Duration.of(10, ChronoUnit.SECONDS))
                .doOnSuccess(i -> log.info("retrievePost success callback"));
    }

    private Mono<UserResponse> retrieveUser(final Integer id) {
        log.info("M=retrieveUser");
        return jsonPlaceholderClient.get()
                .uri("/users/" + id)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .delayElement(Duration.of(2, ChronoUnit.SECONDS))
                .doOnSuccess(i -> log.info("retrieveUser success callback"));
    }

    private Mono<AggregateResponse> aggregatePostAndUser(Mono<PostResponse> postResponseMono, Mono<UserResponse> userResponseMono) {
        return Mono.zip(postResponseMono, userResponseMono).map(merge -> AggregateResponse.builder()
                .body(merge.getT1().getBody())
                .title(merge.getT1().getTitle())
                .email(merge.getT2().getEmail())
                .username(merge.getT2().getUsername()).build())
                .doOnSuccess(i -> log.info("merged post and user"));
    }
}
