package com.example.lifesureinsuarncemanagementsystem.service.processors;

import com.example.lifesureinsuarncemanagementsystem.model.Feedback;
import org.springframework.stereotype.Component;

@Component("DEFAULT") // This is the fallback processor
public class DefaultFeedbackProcessor implements FeedbackProcessor {
    @Override
    public void process(Feedback feedback) {
        // For other categories, we don't have special logic yet.
        System.out.println("LOGIC EXECUTED: Standard processing for category '" + feedback.getCategory() + "' for feedback ID: " + feedback.getId());
    }
}