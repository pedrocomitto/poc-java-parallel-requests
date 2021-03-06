package com.pedrocomitto.poc.parallel.service;

import com.pedrocomitto.poc.parallel.domain.response.AggregateResponse;
import com.pedrocomitto.poc.parallel.domain.response.PostAndUserResponse;
import com.pedrocomitto.poc.parallel.domain.response.PostResponse;
import com.pedrocomitto.poc.parallel.domain.response.UserResponse;
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

        Mono<PostResponse> postResponseMono = retrievePost(1).cache();

        Mono<UserResponse> userResponseMono = postResponseMono.map(PostResponse::getUserId).flatMap(this::retrieveUser).cache();

        return aggregatePostAndUser(postResponseMono, userResponseMono);
    }

    public Mono<PostAndUserResponse> callDependentRequestsAndAggregateResponsesInAElegantFunctionalWay() throws InterruptedException {
        return retrievePost(1)
                .flatMap(postResponse -> retrieveUser(postResponse.getId())
                        .map(userResponse -> new PostAndUserResponse(postResponse, userResponse)));
    }

    private Mono<PostResponse> retrievePost(final Integer id) throws InterruptedException {
        log.info("M=retrievePost");
        return jsonPlaceholderClient.get()
                .uri(uriBuilder -> uriBuilder.path("/posts/{id}").build(id))
                .attribute("id", id)
                .retrieve()
                .bodyToMono(PostResponse.class)
                .delayElement(Duration.of(10, ChronoUnit.SECONDS))
                .doOnSuccess(i -> log.info("retrievePost success callback"));
    }

    private Mono<UserResponse> retrieveUser(final Integer id) {
        log.info("M=retrieveUser");
        return jsonPlaceholderClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{id}").build(id))
                .retrieve()
                .bodyToMono(UserResponse.class)
                .delayElement(Duration.of(2, ChronoUnit.SECONDS))
                .doOnSuccess(i -> log.info("retrieveUser success callback"));
    }

    private Mono<AggregateResponse> aggregatePostAndUser(final Mono<PostResponse> postResponseMono, final Mono<UserResponse> userResponseMono) {
        return Mono.zip(postResponseMono, userResponseMono).map(merge -> AggregateResponse.builder()
                .body(merge.getT1().getBody())
                .title(merge.getT1().getTitle())
                .email(merge.getT2().getEmail())
                .username(merge.getT2().getUsername()).build())
                .doOnSuccess(i -> log.info("merged post and user"));
    }
}
