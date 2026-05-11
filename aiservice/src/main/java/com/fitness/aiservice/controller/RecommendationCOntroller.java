package com.fitness.aiservice.controller;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.service.ServiceRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation")
public class RecommendationCOntroller {
    private final ServiceRecommendation serviceRecommendation;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendation(@PathVariable String userId) {
        return ResponseEntity.ok(serviceRecommendation.getUserRecommendation(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendation(@PathVariable String activityId) {
        return ResponseEntity.ok(serviceRecommendation.getActivityRecommendation(activityId));
    }


}
