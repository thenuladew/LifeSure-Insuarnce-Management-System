package com.example.lifesureinsuarncemanagementsystem.service;

import com.example.lifesureinsuarncemanagementsystem.model.Feedback;
import com.example.lifesureinsuarncemanagementsystem.service.processors.FeedbackProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class FeedbackFactory {

    private final Map<String, FeedbackProcessor> processors;

    @Autowired
    public FeedbackFactory(Map<String, FeedbackProcessor> processors) {
        this.processors = processors;
    }

    public FeedbackProcessor getProcessor(Feedback.FeedbackCategory category) {
        String beanName = category.name();
        // Find the specific processor for the category.
        // If not found, get the DEFAULT processor.
        return Optional.ofNullable(processors.get(beanName))
                .orElse(processors.get("DEFAULT"));
    }
}