package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

  private final ActivityAIService aiService;
  private final RecommendationRespository recommendationRespository;


    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity) {
        log.info("Received Activity: {}", activity.getId());
       // log.info("Generated Recommendation: {}", aiService.generateRecommendation(activity));
        Recommendation recommendation = aiService.generateRecommendation(activity);
        recommendationRespository.save(recommendation);


    }
}


