package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.MenuItemReviews;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuItemReviewsRepository extends CrudRepository<MenuItemReviews, Long> {

}
