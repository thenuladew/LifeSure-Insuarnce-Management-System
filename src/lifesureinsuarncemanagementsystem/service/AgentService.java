package com.example.lifesureinsuarncemanagementsystem.service;

import com.example.lifesureinsuarncemanagementsystem.entity.Agent;
import com.example.lifesureinsuarncemanagementsystem.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;

    public Agent registerAgent(Agent agent) {
        if (agentRepository.existsByEmail(agent.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        agent.setIsApproved(false);
        agent.setIsActive(true);
        return agentRepository.save(agent);
    }

    public Optional<Agent> loginAgent(String email, String password) {
        Optional<Agent> agent = agentRepository.findByEmail(email);
        if (agent.isPresent() && agent.get().getPassword().equals(password)) {
            if (!agent.get().getIsApproved()) {
                throw new RuntimeException("Your account is pending approval by admin");
            }
            if (!agent.get().getIsActive()) {
                throw new RuntimeException("Your account is inactive");
            }
            return agent;
        }
        return Optional.empty();
    }

    public Optional<Agent> getAgentById(Long id) {
        return agentRepository.findById(id);
    }

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public List<Agent> getPendingAgents() {
        return agentRepository.findByIsApproved(false);
    }

    public List<Agent> getApprovedAgents() {
        return agentRepository.findByIsApproved(true);
    }

    public Agent approveAgent(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        agent.setIsApproved(true);
        return agentRepository.save(agent);
    }

    public Agent rejectAgent(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        agent.setIsApproved(false);
        agent.setIsActive(false);
        return agentRepository.save(agent);
    }

    public void deleteAgent(Long id) {
        agentRepository.deleteById(id);
    }
}
