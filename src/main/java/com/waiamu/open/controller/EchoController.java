/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.waiamu.open.controller;


import java.io.IOException;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import com.waiamu.open.dto.JsonPayload;

@Controller
public class EchoController {

	private static final Logger LOG = LoggerFactory.getLogger(EchoController.class); 

	@Autowired
	private HttpServletRequest request;


	@RequestMapping(value = "/**", consumes = MediaType.ALL_VALUE, produces = 
		MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.GET, RequestMethod.POST,
		RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
	public ResponseEntity<JsonPayload> echoBack(@RequestBody(required = false) byte[] rawBody) throws IOException, InterruptedException {
		long start = System.currentTimeMillis();

		final Map<String, String> headers  = Collections.list(request.getHeaderNames()).stream()
			.collect(Collectors.toMap(Function.identity(), request::getHeader));

		final JsonPayload response = buildPayload(request, headers);
		
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;

		response.set(JsonPayload.BODY, rawBody != null ? Base64.getEncoder().encodeToString(rawBody) + "\n time elapsed: " + timeElapsed: null);


		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	public JsonPayload buildPayload(HttpServletRequest request, Map<String, String> headers) throws IOException, InterruptedException {

		final JsonPayload response = new JsonPayload();
		response.set(JsonPayload.PROTOCOL, request.getProtocol());
		response.set(JsonPayload.METHOD, request.getMethod());
		response.set(JsonPayload.HEADERS, headers);
		response.set(JsonPayload.COOKIES, request.getCookies());
		response.set(JsonPayload.PARAMETERS, request.getParameterMap());
		response.set(JsonPayload.PATH, request.getServletPath());
		LOG.info("REQUEST: {}", request.getParameterMap());


		if (request.getMethod() == "POST") {
			 System.out.println("poooost" + request.getMethod());
			 
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> myResponse = restTemplate.getForEntity("http://echo-spring-backend.echo-backend/hello", String.class);
			System.out.println(myResponse.toString());
			response.set(JsonPayload.BODY, "backended");
		}
		
		return response;
	}


}
