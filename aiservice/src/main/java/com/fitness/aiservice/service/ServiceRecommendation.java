package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRespository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ServiceRecommendation {

    private  final RecommendationRespository recommendationRespository;

    public  List<Recommendation> getUserRecommendation(String userId) {
        return recommendationRespository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String activityId) {
        return recommendationRespository.findByactivityId(activityId)
                .orElseThrow(() -> new RuntimeException("No activity found "+activityId));
    }
}
