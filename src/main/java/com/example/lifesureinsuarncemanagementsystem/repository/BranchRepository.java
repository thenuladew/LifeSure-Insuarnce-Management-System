package com.example.lifesureinsuarncemanagementsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lifesureinsuarncemanagementsystem.model.Branch;
import com.example.lifesureinsuarncemanagementsystem.model.BranchStatus;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findBybCode(String bCode);

    @Query("SELECT b FROM Branch b WHERE (:keyword IS NULL OR LOWER(b.bName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(b.bAddress) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(b.managerName) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND (:status IS NULL OR b.status = :status) ORDER BY b.bName ASC")
    List<Branch> searchBranches(@Param("keyword") String keyword, @Param("status") BranchStatus status);

    List<Branch> findByStatus(BranchStatus status);

}
