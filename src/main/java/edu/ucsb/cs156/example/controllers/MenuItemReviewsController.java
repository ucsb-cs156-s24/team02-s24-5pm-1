package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReviews;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "MenuItemReviews")
@RequestMapping("/api/menuitemreviews")
@RestController
@Slf4j

public class MenuItemReviewsController extends ApiController {
    @Autowired
    MenuItemReviewsRepository menuItemReviewsRepository;

    @Operation(summary = "List all menu item reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<MenuItemReviews> allmenuItemReviews() {
        Iterable<MenuItemReviews> reviews = menuItemReviewsRepository.findAll();
        return reviews;
    }

    @Operation(summary = "Create a new date reviewed")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public MenuItemReviews postMenuItemReviews(
            @Parameter(name = "itemId") @RequestParam long itemId,
            @Parameter(name = "reviewerEmail") @RequestParam String reviewerEmail,
            @Parameter(name = "stars") @RequestParam int stars,
            @Parameter(name = "dateReviewed") @RequestParam("dateReviewed") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateReviewed,
            @Parameter(name = "comments") @RequestParam String comments)

            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("dataReviewed={}", dateReviewed);

        MenuItemReviews menuItemReviews = new MenuItemReviews();
        menuItemReviews.setItemId(itemId);
        menuItemReviews.setReviewerEmail(reviewerEmail);
        menuItemReviews.setStars(stars);
        menuItemReviews.setComments(comments);
        menuItemReviews.setDateReviewed(dateReviewed);

        MenuItemReviews savedMenuItemReviews = menuItemReviewsRepository.save(menuItemReviews);

        return savedMenuItemReviews;
    }

    @Operation(summary = "Get a single review")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public MenuItemReviews getById(
            @Parameter(name = "id") @RequestParam Long id) {
        MenuItemReviews menuItemReviews = menuItemReviewsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReviews.class, id));

        return menuItemReviews;
    }

    @Operation(summary = "Delete a menuItemReview")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteMenuItemReviews(
            @Parameter(name = "id") @RequestParam Long id) {
        MenuItemReviews menuItemReviews = menuItemReviewsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReviews.class, id));

        menuItemReviewsRepository.delete(menuItemReviews);
        return genericMessage("menuItemReviews with itemId %s deleted".formatted(id));
    }

    @Operation(summary = "Update a single menu item review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public MenuItemReviews updateMenuItemReviews(
            @Parameter(name = "id") @RequestParam Long id,
            @RequestBody @Valid MenuItemReviews incoming) {

        MenuItemReviews menuItemReviews = menuItemReviewsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReviews.class, id));

        menuItemReviews.setItemId(incoming.getItemId());
        menuItemReviews.setReviewerEmail(incoming.getReviewerEmail());
        menuItemReviews.setStars(incoming.getStars());
        menuItemReviews.setComments(incoming.getComments());
        menuItemReviews.setDateReviewed(incoming.getDateReviewed());

        menuItemReviewsRepository.save(menuItemReviews);

        return menuItemReviews;
    }
}
