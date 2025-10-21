package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dto.LoginRequest;
import com.example.lifesureinsuarncemanagementsystem.dto.RegisterRequest;
import com.example.lifesureinsuarncemanagementsystem.entity.Agent;
import com.example.lifesureinsuarncemanagementsystem.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Agent agent = new Agent();
            agent.setName(request.getName());
            agent.setEmail(request.getEmail());
            agent.setPassword(request.getPassword());
            agent.setPhone(request.getPhone());
            agent.setAddress(request.getAddress());

            Agent savedAgent = agentService.registerAgent(agent);
            return ResponseEntity.ok(savedAgent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Optional<Agent> agent = agentService.loginAgent(request.getEmail(), request.getPassword());
            if (agent.isPresent()) {
                return ResponseEntity.ok(agent.get());
            }
            return ResponseEntity.badRequest().body("Invalid email or password");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAgent(@PathVariable Long id) {
        Optional<Agent> agent = agentService.getAgentById(id);
        if (agent.isPresent()) {
            return ResponseEntity.ok(agent.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Agent>> getAllAgents() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Agent>> getPendingAgents() {
        return ResponseEntity.ok(agentService.getPendingAgents());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Agent>> getApprovedAgents() {
        return ResponseEntity.ok(agentService.getApprovedAgents());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveAgent(@PathVariable Long id) {
        try {
            Agent agent = agentService.approveAgent(id);
            return ResponseEntity.ok(agent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectAgent(@PathVariable Long id) {
        try {
            Agent agent = agentService.rejectAgent(id);
            return ResponseEntity.ok(agent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAgent(@PathVariable Long id) {
        try {
            agentService.deleteAgent(id);
            return ResponseEntity.ok("Agent deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
