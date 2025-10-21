package com.example.lifesureinsuarncemanagementsystem.service.processors;

import com.example.lifesureinsuarncemanagementsystem.model.Feedback;
import org.springframework.stereotype.Component;

@Component("WEBSITE_EXPERIENCE") // Bean name must match the Enum name
public class WebsiteExperienceProcessor implements com.example.lifesureinsuarncemanagementsystem.service.processors.FeedbackProcessor {
    @Override
    public void process(Feedback feedback) {
        // This is where you would put the logic to create an IT support ticket
        System.out.println("LOGIC EXECUTED: Creating IT support ticket for feedback ID: " + feedback.getId());
    }
}