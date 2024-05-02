package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBRecommendationRequests;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBRecommendationRequestsRepository;

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

@Tag(name = "UCSBRecommendationRequests")
@RequestMapping("/api/ucsbrecommendationrequests")
@RestController
@Slf4j
public class UCSBRecommendationRequestsController extends ApiController {

    @Autowired
    UCSBRecommendationRequestsRepository ucsbRecommendationRequestsRepository;

    @Operation(summary= "List all ucsb recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRecommendationRequests> allUCSBRecommendationRequests() {
        Iterable<UCSBRecommendationRequests> RecommendationRequests = ucsbRecommendationRequestsRepository.findAll();
        return RecommendationRequests;
    }

    @Operation(summary= "Create a recommendation requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBRecommendationRequests postUCSBRecommendationRequests(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="professorEmail") @RequestParam String professorEmail,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="dateRequested") @RequestParam("dateRequested")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
            @Parameter(name="dateNeeded") @RequestParam("dateNeeded")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
            @Parameter(name="done") @RequestParam boolean done)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("localDateTime={}", dateRequested);

        UCSBRecommendationRequests ucsbRecommendationRequests = new UCSBRecommendationRequests();
        ucsbRecommendationRequests.setRequesterEmail(requesterEmail);
        ucsbRecommendationRequests.setProfessorEmail(professorEmail);
        ucsbRecommendationRequests.setExplanation(explanation);
        ucsbRecommendationRequests.setDateRequested(dateRequested);
        ucsbRecommendationRequests.setDateNeeded(dateNeeded);
        ucsbRecommendationRequests.setDone(done);
        UCSBRecommendationRequests savedUCSBRecommendationRequests = ucsbRecommendationRequestsRepository.save(ucsbRecommendationRequests);

        return savedUCSBRecommendationRequests;
    }

    @Operation(summary= "Get a single recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBRecommendationRequests getById(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBRecommendationRequests ucsbRecommendationRequests = ucsbRecommendationRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBRecommendationRequests.class, id));

        return ucsbRecommendationRequests;
    }

    @Operation(summary= "Delete a UCSBRecommendationRequests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBRecommendationRequests(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBRecommendationRequests ucsbRecommendationRequests = ucsbRecommendationRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBRecommendationRequests.class, id));

        ucsbRecommendationRequestsRepository.delete(ucsbRecommendationRequests);
        return genericMessage("UCSBRecommendationRequests with id %s deleted".formatted(id));
    }

    @Operation(summary= "Update a single recommendation requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBRecommendationRequests updateUCSBRecommendationRequests(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid UCSBRecommendationRequests incoming) {

        UCSBRecommendationRequests ucsbRecommendationRequests = ucsbRecommendationRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBRecommendationRequests.class, id));

            ucsbRecommendationRequests.setRequesterEmail(incoming.getRequesterEmail());
            ucsbRecommendationRequests.setProfessorEmail(incoming.getProfessorEmail());
            ucsbRecommendationRequests.setExplanation(incoming.getExplanation());
            ucsbRecommendationRequests.setDateRequested(incoming.getDateRequested());
            ucsbRecommendationRequests.setDateNeeded(incoming.getDateNeeded());
            ucsbRecommendationRequests.setDone(incoming.getDone());

        ucsbRecommendationRequestsRepository.save(ucsbRecommendationRequests);

        return ucsbRecommendationRequests;
    }
}
