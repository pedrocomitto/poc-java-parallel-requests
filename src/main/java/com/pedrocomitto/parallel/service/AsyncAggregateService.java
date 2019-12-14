package com.pedrocomitto.parallel.service;

import com.pedrocomitto.parallel.domain.response.AggregateResponse;
import com.pedrocomitto.parallel.domain.response.PostResponse;
import com.pedrocomitto.parallel.domain.response.UserResponse;
import com.pedrocomitto.parallel.http.JsonPlaceholderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
public class AsyncAggregateService {

    private final JsonPlaceholderClient client;

    private ExecutorService threadpool = Executors.newCachedThreadPool();

    public AsyncAggregateService(JsonPlaceholderClient client) {
        this.client = client;
    }

    public AggregateResponse callIndependentRequestsAndAggregateResponses() throws ExecutionException, InterruptedException {
        Future<PostResponse> postResponseFuture = threadpool.submit(() -> retrievePost(1));
        Future<UserResponse> userResponseFuture = threadpool.submit(() -> retrieveUser(1));

        PostResponse postResponse = postResponseFuture.get();
        UserResponse userResponse = userResponseFuture.get();

        return aggregatePostAndUser(postResponse, userResponse);
    }

    public AggregateResponse callDependentRequestsAndAggregateResponses() throws ExecutionException, InterruptedException {
        Future<PostResponse> postResponseFuture = threadpool.submit(() -> retrievePost(1));
        PostResponse postResponse = postResponseFuture.get();

        Future<UserResponse> userResponseFuture = threadpool.submit(() -> retrieveUser(postResponse.getUserId()));
        UserResponse userResponse = userResponseFuture.get();

        return aggregatePostAndUser(postResponse, userResponse);
    }

    private UserResponse retrieveUser(Integer userId) throws InterruptedException {
        log.info("M=retrieveUser");

        Thread.sleep(2000);

        UserResponse userResponse = client.retrieveUser(userId);

        log.info("retrieveUser success callback");
        return userResponse;
    }

    private PostResponse retrievePost(Integer id) throws InterruptedException {
        log.info("M=retrievePost");

        Thread.sleep(10000);

        PostResponse postResponse = client.retrievePost(id);

        log.info("retrievePost success callback");
        return postResponse;
    }

    private AggregateResponse aggregatePostAndUser(PostResponse postResponse, UserResponse userResponse) {
        log.info("merging post and user");
        return AggregateResponse.builder()
                .body(postResponse.getBody())
                .title(postResponse.getTitle())
                .email(userResponse.getEmail())
                .username(userResponse.getUsername()).build();
    }
}
