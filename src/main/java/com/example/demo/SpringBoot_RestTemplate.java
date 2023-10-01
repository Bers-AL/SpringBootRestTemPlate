package com.example.demo;

import com.example.demo.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringBoot_RestTemplate {

    private static String baseUrl = "http://94.198.50.185:7081/api/users";
    private static final RestTemplate restTemplate = new RestTemplate();


    public static void main(String[] args) {
        SpringApplication.run(SpringBoot_RestTemplate.class, args);

        StringBuilder result = new StringBuilder();
        String sessionId = getAllUsers();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionId);
        User user = new User(3L, "James", "Brown", (byte) 30);

        //step 1: create user
        result.append(createUser(user, headers));

        //step 2: update user
        user.setName("Thomas");
        user.setLastName("Shelby");
        result.append(updateUser(user, headers));

        //step 3: delete user
        result.append(deleteUser(3L, headers));

        System.out.println(result);
        System.out.println("Символов в строке: " + result.length());
    }

    private static String getAllUsers() {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, null, String.class);
        return response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    }

    private static String createUser(User user, HttpHeaders headers) {
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        return restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class).getBody();
    }

    private static String updateUser(User user, HttpHeaders headers) {
        HttpEntity<User> request = new HttpEntity<>(user, headers);

        return restTemplate.exchange(baseUrl, HttpMethod.PUT, request, String.class).getBody();
    }

    private static String deleteUser(Long id, HttpHeaders headers) {
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(baseUrl + "/" + id, HttpMethod.DELETE, request, String.class).getBody();
    }
}
