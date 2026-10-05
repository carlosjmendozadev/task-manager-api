package com.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/actuator/health")
    public Map<String, Object> health(){
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());

        try{
            if(jdbcTemplate != null){
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                response.put("database", "UP");
            }
        } catch (Exception e){
            response.put("database", "DOWN");
            response.put("error", e.getMessage());
            return response;
        }

        return response;
    }
}
