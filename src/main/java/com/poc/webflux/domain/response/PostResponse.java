package com.poc.webflux.domain.response;

import lombok.Data;

@Data
public class PostResponse {

    private Integer userId;

    private Integer id;

    private String title;

    private String body;
}
