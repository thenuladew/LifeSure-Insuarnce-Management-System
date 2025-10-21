package com.example.lifesureinsuarncemanagementsystem.repository;

import com.example.lifesureinsuarncemanagementsystem.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    /**
     * Find policies by status
     */
    List<Policy> findByStatus(Policy.PolicyStatus status);
    
    /**
     * Find policies by type
     */
    List<Policy> findByPolicyType(String policyType);
    
    /**
     * Search policies by name, type, or description
     */
    @Query("SELECT p FROM Policy p WHERE " +
           "LOWER(p.policyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.policyType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Policy> searchPolicies(@Param("keyword") String keyword);
    
    /**
     * Find active policies only
     */
    @Query("SELECT p FROM Policy p WHERE p.status = 'ACTIVE'")
    List<Policy> findActivePolicies();
    
    /**
     * Find policies by price range
     */
    @Query("SELECT p FROM Policy p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Policy> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                  @Param("maxPrice") java.math.BigDecimal maxPrice);
}
