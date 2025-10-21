package com.example.lifesureinsuarncemanagementsystem.service.processors;

import com.example.lifesureinsuarncemanagementsystem.model.Feedback;
import org.springframework.stereotype.Component;

@Component("CLAIM_PROCESS") // Bean name must match the Enum name
public class ClaimFeedbackProcessor implements FeedbackProcessor {
    @Override
    public void process(Feedback feedback) {
        // This is where you would put the logic to email the legal department
        System.out.println("LOGIC EXECUTED: Sending email to Legal Department for feedback ID:(IT IS WORKING!!! CLAIM PROCESSOR CALLED SUCCESSFULLY) " + feedback.getId());
    }
}