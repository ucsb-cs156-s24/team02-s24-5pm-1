package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBRecommendationRequests;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UCSBRecommendationRequestsRepository extends CrudRepository<UCSBRecommendationRequests, Long> {
    
}