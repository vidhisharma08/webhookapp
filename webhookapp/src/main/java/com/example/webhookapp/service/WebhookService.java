package com.example.webhookapp.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.webhookapp.model.WebhookResponse;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();
    
    private final String REG_NO = "REG12347";  
    private final String NAME = "Vidhi Sharma";  
    private final String EMAIL = "vidhi@example.com";  

    public void startProcess() {
        WebhookResponse response = generateWebhook();  
        if (response != null) {
            System.out.println("Webhook: " + response.getWebhook());
            System.out.println("AccessToken: " + response.getAccessToken());

            String finalQuery = determineAndSolveSQL(REG_NO);
            System.out.println("Final SQL Query: " + finalQuery);
            
            submitSolution(response.getWebhook(), response.getAccessToken(), finalQuery);
        } else {
            System.err.println("Failed to generate webhook.");
        }
    }

    private WebhookResponse generateWebhook() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = Map.of(
                "name", NAME,
                "regNo", REG_NO,
                "email", EMAIL
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    WebhookResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String determineAndSolveSQL(String regNo) {
        int lastDigit = Character.getNumericValue(regNo.charAt(regNo.length() - 1));
        
        if (lastDigit % 2 == 1) {
            return solveQuestion1();  
        } else {
            return solveQuestion2();  
        }
    }

    private String solveQuestion1() {
        return "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
               "FLOOR(DATEDIFF(CURRENT_DATE, e.DOB) / 365.25) AS AGE, d.DEPARTMENT_NAME " +
               "FROM PAYMENTS p " +
               "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
               "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
               "WHERE DAY(p.PAYMENT_TIME) != 1 " +
               "ORDER BY p.AMOUNT DESC LIMIT 1;";
    }

    private String solveQuestion2() {
        return "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
               "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
               "FROM EMPLOYEE e1 " +
               "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
               "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT " +
               "AND e2.DOB > e1.DOB " +
               "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
               "ORDER BY e1.EMP_ID DESC;";
    }

    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);  

        Map<String, String> body = Map.of("finalQuery", sqlQuery);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl, 
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            System.out.println("Submission Response: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
