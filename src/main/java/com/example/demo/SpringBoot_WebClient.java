package com.example.demo;

import com.example.demo.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class SpringBoot_WebClient {

    private static final WebClient webClient = WebClient.create();
    private static final String baseUrl = "http://94.198.50.185:7081/api/users";

    public static void main(String[] args) {

        SpringApplication.run(SpringBoot_WebClient.class, args);

        StringBuilder result = new StringBuilder();

        //step 1: get session id
        String sessionId = getAllUsers();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionId);

        User user = new User(3L, "James", "Brown", (byte) 30);

        //step 2: create user
        result.append(createUser(user, headers));

        //step 3: update user
        user.setName("Thomas");
        user.setLastName("Shelby");
        result.append(updateUser(user, headers));

        //step 4: delete user
        result.append(deleteUser(3L, headers));

        System.out.println(result);
        System.out.println("Символов в строке: " + result.length());
    }

    private static String getAllUsers() {
        return webClient.get()
                .uri(baseUrl)
                .retrieve()
                .toEntity(String.class)
                .map(responseEntity -> responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
                .block();
    }

    private static String createUser(User user, HttpHeaders headers) {
        return webClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Cookie", headers.getFirst(HttpHeaders.COOKIE))
                .body(BodyInserters.fromValue(user))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private static String updateUser(User user, HttpHeaders headers) {
        return webClient.put()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Cookie", headers.getFirst(HttpHeaders.COOKIE))
                .body(BodyInserters.fromValue(user))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private static String deleteUser(Long id, HttpHeaders headers) {
        return webClient.delete()
                .uri(baseUrl + "/" + id)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
