package com.example.activityservice.controller;


import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import com.example.activityservice.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request, @RequestHeader("X-User-ID") String userId)

    {
        if(userId != null)
        {
            request.setUserId(userId);
        }
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping()
    public ResponseEntity<List<ActivityResponse>> getUserActivity(@RequestHeader("X-User-ID") String userId)
    {
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable String id)
    {
        return ResponseEntity.ok(activityService.getUserActivitiesById(id));
    }

}
