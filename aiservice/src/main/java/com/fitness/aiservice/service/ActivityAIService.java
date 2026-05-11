package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswers(prompt);
        log.info("AI Response is {}", aiResponse);

        return processAiResponse(activity, aiResponse);
    }

    private  Recommendation processAiResponse(Activity activity,String aiResponse){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(aiResponse);

            JsonNode activityNode = jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");


            String JsonContent =  activityNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            //log.info("Parsed Response is {}", JsonContent);


            JsonNode analsysisJson = objectMapper.readTree(JsonContent);

            JsonNode analysisNode =  analsysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();

            addAnalysisSection(fullAnalysis,analysisNode,"overall","overall: ");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","pace: ");
            addAnalysisSection(fullAnalysis,analysisNode,"Heart Rate","Heart Rate: ");
            addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","caloriesBurned: ");


            List<String> improvements = extractImprovements(analsysisJson.path("improvements"));
            List<String> suggestions = extractsuggestions(analsysisJson.path("suggestions"));

            List<String> safety = extractsafetys(analsysisJson.path("safety"));


            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        catch (Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractsafetys(JsonNode safety) {
        List<String> safetyList = new ArrayList<>();

        if(safety.isArray())
        {
            safety.forEach(safe -> {
                safetyList.add(safe.asText());

            });
        }
        return safety.isEmpty() ? Collections.singletonList("Follow the general safety guideLines") : safetyList;
    }

    private List<String> extractsuggestions(JsonNode suggestions) {
        List<String> suggestionList = new ArrayList<>();

        if(suggestions.isArray()){
            suggestions.forEach(suggestion -> {
                String workOut = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();

                suggestionList.add(String.format("%s: %s", workOut, description));
            });
        }
        return suggestionList.isEmpty() ? Collections.singletonList("No Specific suggestion provided") : suggestionList;
    }

    private List<String> extractImprovements(JsonNode improvements) {
        List<String> improvementList = new ArrayList<>();
        if (improvements.isArray()) {
            improvements.forEach(improvementL -> {
                String area = improvementL.path("area").asText();
                String detail =
                        improvementL.path("recommendation").asText();

                improvementList.add(String.format("%s: %s", area, detail));
            });

        }
        return improvementList.isEmpty() ? Collections.singletonList("No Specific improvements provided") : improvementList;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String previx) {
        if(!analysisNode.path(key).isMissingNode())
        {
            fullAnalysis.append(previx)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity){
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }

}

