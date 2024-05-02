package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

@Tag(name = "UCSBOrganizations")
@RequestMapping("/api/ucsbOrganizations")
@RestController
@Slf4j
public class UCSBOrganizationsController extends ApiController {

    @Autowired
    UCSBOrganizationsRepository ucsbOrganizationsRepository;

    @Operation(summary= "List all ucsb organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganizations> allOrgs() {
        Iterable<UCSBOrganizations> orgs = ucsbOrganizationsRepository.findAll();
        return orgs;
    }

    @Operation(summary= "Create a new orgs")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganizations postOrgs(
        @Parameter(name="orgCode") @RequestParam String orgCode,
        @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
        @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
        @Parameter(name="inactive") @RequestParam boolean inactive
        )
        {

        UCSBOrganizations orgs = new UCSBOrganizations();
        orgs.setOrgCode(orgCode);
        orgs.setOrgTranslationShort(orgTranslationShort);
        orgs.setOrgTranslation(orgTranslation);
        orgs.setInactive(inactive);

        UCSBOrganizations savedOrgs = ucsbOrganizationsRepository.save(orgs);

        return savedOrgs;
    }

    @Operation(summary= "Get a single orgs")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganizations getById(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
        UCSBOrganizations orgs = ucsbOrganizationsRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));

        return orgs;
    }

    @Operation(summary= "Delete a UCSBOrganizations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteOrgs(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
        UCSBOrganizations orgs = ucsbOrganizationsRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));

        ucsbOrganizationsRepository.delete(orgs);
        return genericMessage("UCSBOrganizations with id %s deleted".formatted(orgCode));
    }

    @Operation(summary= "Update a single orgs")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganizations updateOrgs(
            @Parameter(name="orgCode") @RequestParam String orgCode,
            @RequestBody @Valid UCSBOrganizations incoming) {

        UCSBOrganizations orgs = ucsbOrganizationsRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));


        orgs.setOrgTranslationShort(incoming.getOrgTranslationShort());  
        orgs.setOrgTranslation(incoming.getOrgTranslation());
        orgs.setInactive(incoming.getInactive());

        ucsbOrganizationsRepository.save(orgs);

        return orgs;
    }
}
