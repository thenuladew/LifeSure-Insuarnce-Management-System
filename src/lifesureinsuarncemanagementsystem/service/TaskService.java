package com.example.lifesureinsuarncemanagementsystem.service;

import com.example.lifesureinsuarncemanagementsystem.entity.Agent;
import com.example.lifesureinsuarncemanagementsystem.entity.Customer;
import com.example.lifesureinsuarncemanagementsystem.model.Policy;
import com.example.lifesureinsuarncemanagementsystem.entity.Task;
import com.example.lifesureinsuarncemanagementsystem.repository.AgentRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.CustomerRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.PolicyRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AgentRepository agentRepository;
    private final CustomerRepository customerRepository;
    private final PolicyRepository policyRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByAgentId(Long agentId) {
        return taskRepository.findByAgentId(agentId);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());

        if (taskDetails.getCustomer() != null) {
            task.setCustomer(taskDetails.getCustomer());
        }
        if (taskDetails.getPolicy() != null) {
            task.setPolicy(taskDetails.getPolicy());
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }
}
