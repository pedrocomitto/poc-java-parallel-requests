package com.pedrocomitto.poc.parallel.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostAndUserResponse {

    private PostResponse postResponse;

    private UserResponse userResponse;
}
