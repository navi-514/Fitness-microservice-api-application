package com.fitness.aiservice.repository;

import com.fitness.aiservice.config.MongoConfig;
import com.fitness.aiservice.model.Recommendation;
import org.jspecify.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRespository extends MongoRepository<Recommendation, String> {
    List<Recommendation> findByUserId(String userId);

     Optional<Recommendation> findByactivityId(String activityId);
}
