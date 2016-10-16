package io.magentys.rest;

import io.magentys.rest.model.RestResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import javax.xml.ws.Response;

public class RestAssuredTool {

    private final RequestSpecification requestSpecification;

    public RestAssuredTool() {
        this(RestAssured.baseURI);
    }

    public RestAssuredTool(final String baseURI) {
        this.requestSpecification = new RequestSpecBuilder().setBaseUri(baseURI).build();
    }

    public RequestSpecification requestSpecification() {
        return requestSpecification;
    }

    public <T> RestResponse<T> toRestResponse(final Response response, final T t) {
        return null;
    }
}