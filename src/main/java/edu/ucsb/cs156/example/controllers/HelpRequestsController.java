package edu.ucsb.cs156.example.controllers;

import java.time.LocalDateTime;

import javax.validation.Valid;

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

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.ucsb.cs156.example.entities.UCSBHelpRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBHelpRequestsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Help Request")
@RequestMapping("/api/helprequest")
@RestController
@Slf4j
public class HelpRequestsController extends ApiController {
    @Autowired
    UCSBHelpRequestsRepository ucsbHelpRequestsRepository;

    @Operation(summary= "List all ucsb help requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBHelpRequest> allUCSBDates() {
        Iterable<UCSBHelpRequest> dates = ucsbHelpRequestsRepository.findAll();
        return dates;
    }

    @Operation(summary= "Create a new help request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBHelpRequest postUCSBDate(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="teamId") @RequestParam String teamId,
            @Parameter(name="tableOrBreakoutRoom") @RequestParam String tableOrBreakoutRoom,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="solved") @RequestParam boolean solved,
            @Parameter(name="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("localDateTime={}", localDateTime);

        // UCSBDate ucsbDate = new UCSBDate();
        // ucsbDate.setQuarterYYYYQ(quarterYYYYQ);
        // ucsbDate.setName(name);
        // ucsbDate.setLocalDateTime(localDateTime);

        
        UCSBHelpRequest ucsbHelpRequest = new UCSBHelpRequest();
        ucsbHelpRequest.setRequestTime(localDateTime);
        ucsbHelpRequest.setSolved(solved);
        ucsbHelpRequest.setRequesterEmail(requesterEmail);
        ucsbHelpRequest.setExplanation(explanation);
        ucsbHelpRequest.setTeamId(teamId);
        ucsbHelpRequest.setTableOrBreakoutRoom(tableOrBreakoutRoom);


        UCSBHelpRequest savedUcsbDate = ucsbHelpRequestsRepository.save(ucsbHelpRequest);
        
        return savedUcsbDate;
    }

    @Operation(summary= "Get a single help request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBHelpRequest getById(
            @Parameter(name="id") @RequestParam Long id) {

        UCSBHelpRequest ucsbHelpRequest = ucsbHelpRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBHelpRequest.class, id));

        return ucsbHelpRequest;
    }

    @Operation(summary= "Delete a UCSBHelpRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBDate(
            @Parameter(name="id") @RequestParam Long id) {

        UCSBHelpRequest ucsbHelpRequest = ucsbHelpRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBHelpRequest.class, id));

        ucsbHelpRequestsRepository.delete(ucsbHelpRequest);
        return genericMessage("UCSBHelpRequest with id %s deleted".formatted(id));
    }

    @Operation(summary= "Update a single help request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBHelpRequest updateUCSBDate(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid UCSBHelpRequest incoming) {

        UCSBHelpRequest ucsbHelpRequest = ucsbHelpRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBHelpRequest.class, id));
        ucsbHelpRequest.setExplanation(incoming.getExplanation());
        ucsbHelpRequest.setRequesterEmail(incoming.getRequesterEmail());
        ucsbHelpRequest.setSolved(incoming.getSolved());
        ucsbHelpRequest.setTableOrBreakoutRoom(incoming.getTableOrBreakoutRoom());
        ucsbHelpRequest.setTeamId(incoming.getTeamId());
        ucsbHelpRequest.setRequestTime(incoming.getRequestTime());

        ucsbHelpRequestsRepository.save(ucsbHelpRequest);

        return ucsbHelpRequest;
    }
}
