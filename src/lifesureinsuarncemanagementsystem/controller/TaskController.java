package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dto.TaskRequest;
import com.example.lifesureinsuarncemanagementsystem.entity.Agent;
import com.example.lifesureinsuarncemanagementsystem.entity.Customer;
import com.example.lifesureinsuarncemanagementsystem.model.Policy;
import com.example.lifesureinsuarncemanagementsystem.entity.Task;
import com.example.lifesureinsuarncemanagementsystem.repository.AgentRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.CustomerRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.PolicyRepository;
import com.example.lifesureinsuarncemanagementsystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private final AgentRepository agentRepository;
    private final CustomerRepository customerRepository;
    private final PolicyRepository policyRepository;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest request) {
        try {
            Agent agent = agentRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new RuntimeException("Agent not found"));
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            Policy policy = policyRepository.findById(request.getPolicyId())
                    .orElseThrow(() -> new RuntimeException("Policy not found"));

            Task task = new Task();
            task.setTitle(request.getTitle());
            task.setDescription(request.getDescription());
            task.setAgent(agent);
            task.setCustomer(customer);
            task.setPolicy(policy);
            task.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
            task.setDueDate(request.getDueDate());

            Task savedTask = taskService.createTask(task);
            return ResponseEntity.ok(savedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Task>> getTasksByAgentId(@PathVariable Long agentId) {
        return ResponseEntity.ok(taskService.getTasksByAgentId(agentId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        try {
            Task taskDetails = new Task();
            taskDetails.setTitle(request.getTitle());
            taskDetails.setDescription(request.getDescription());
            taskDetails.setStatus(request.getStatus());
            taskDetails.setDueDate(request.getDueDate());

            if (request.getCustomerId() != null) {
                Customer customer = customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
                taskDetails.setCustomer(customer);
            }
            if (request.getPolicyId() != null) {
                Policy policy = policyRepository.findById(request.getPolicyId())
                        .orElseThrow(() -> new RuntimeException("Policy not found"));
                taskDetails.setPolicy(policy);
            }

            Task updatedTask = taskService.updateTask(id, taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
