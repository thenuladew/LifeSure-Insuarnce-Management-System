package com.example.lifesureinsuarncemanagementsystem.repository;

import com.example.lifesureinsuarncemanagementsystem.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAgentId(Long agentId);
    List<Task> findByCustomerId(Long customerId);
    List<Task> findByStatus(String status);
}
