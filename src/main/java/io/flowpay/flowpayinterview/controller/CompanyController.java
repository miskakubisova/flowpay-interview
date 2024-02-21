package io.flowpay.flowpayinterview.controller;

import io.flowpay.flowpayinterview.config.ApiUrls;
import io.flowpay.flowpayinterview.model.dto.CompanyDTO;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

/**
 * RestController for handling company-related operations.
 * This controller provides endpoints for managing companies and their associated representatives
 * within the Flowpay Interview application.
 */
@Validated
@RestController
@RequestMapping(ApiUrls.URL_COMPANIES)
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Endpoint to create a new company.
     *
     * @param company The {@link CompanyDTO} containing the details of the company to be created.
     * @return A {@link ResponseEntity} containing the created {@link CompanyDTO} and HTTP status code.
     */
    @Operation(summary = "Create a new company", description = "Creates a new company with the provided details.")
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO company) {
        CompanyDTO createdCompany = companyService.createCompany(company);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve a company by its ID.
     *
     * @param id The ID of the company to retrieve.
     * @return A {@link ResponseEntity} containing the {@link CompanyDTO} and HTTP status code.
     */
    @Operation(summary = "Get a company by ID", description = "Retrieves a company using its unique identifier.")
    @GetMapping(ApiUrls.URL_COMPANIES_BY_ID)
    public ResponseEntity<CompanyDTO> getCompanyById(@Parameter(description = "ID of the company to retrieve", required = true)
                                                     @PathVariable Long id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    /**
     * Endpoint to retrieve companies by name.
     *
     * @param name The name of the companies to retrieve.
     * @return A {@link ResponseEntity} containing a list of {@link CompanyDTO} and HTTP status code.
     */
    @Operation(summary = "Get companies by name", description = "Retrieves companies matching the specified name.")
    @GetMapping(ApiUrls.URL_COMPANIES_BY_NAME)
    public ResponseEntity<List<CompanyDTO>> getCompaniesByName(@Parameter(description = "Name of the companies to retrieve", required = true)
                                                               @PathVariable String name) {
        List<CompanyDTO> companies = companyService.getCompanyByName(name);
        return ResponseEntity.ok(companies);
    }

    /**
     * Endpoint to retrieve companies without any assigned representatives.
     *
     * @return A {@link ResponseEntity} containing a list of {@link CompanyDTO} without representatives and HTTP status code.
     */
    @Operation(summary = "Get companies without representatives", description = "Retrieves companies that do not have any representatives assigned.")
    @GetMapping(ApiUrls.URL_COMPANIES_WITHOUT_REPRESENTATIVE)
    public ResponseEntity<List<CompanyDTO>> getCompaniesWithoutRepresentative() {
        List<CompanyDTO> companies = companyService.getCompaniesWithoutRepresentative();
        return ResponseEntity.ok(companies);
    }

    /**
     * Endpoint to retrieve all companies.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link CompanyDTO} and HTTP status code.
     */
    @Operation(summary = "Get all companies", description = "Retrieves all existing companies.")
    @GetMapping(ApiUrls.URL_COMPANIES_ALL)
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    /**
     * Endpoint to update a company by its ID.
     *
     * @param id The ID of the company to update.
     * @param company The {@link CompanyDTO} containing the updated company details.
     * @return A {@link ResponseEntity} containing the updated {@link CompanyDTO} and HTTP status code.
     */
    @Operation(summary = "Update a company", description = "Updates the details of an existing company.")
    @PutMapping(ApiUrls.URL_COMPANIES_BY_ID)
    public ResponseEntity<CompanyDTO> updateCompany(@Parameter(description = "ID of the company to update", required = true)
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody CompanyDTO company) {
        CompanyDTO updatedCompany = companyService.updateCompany(id, company);
        return ResponseEntity.ok(updatedCompany);
    }

    /**
     * Endpoint to delete a company by its ID.
     *
     * @param id The ID of the company to delete.
     * @return A {@link ResponseEntity} with HTTP status code indicating the outcome of the operation.
     */
    @Operation(summary = "Delete a company", description = "Deletes a company using its unique identifier.")
    @DeleteMapping(ApiUrls.URL_COMPANIES_BY_ID)
    public ResponseEntity<Void> deleteCompany(@Parameter(description = "ID of the company to delete", required = true)
                                              @PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assigns an existing representative to an existing company.
     *
     * @param companyId The ID of the company to which the representative will be assigned.
     * @param representativeId The ID of the representative to be assigned to the company.
     * @return A {@link ResponseEntity} with the updated {@link CompanyDTO} containing the assigned representative.
     */
    @Operation(summary = "Assign a representative to a company", description = "Assigns an existing representative to an existing company.")
    @PostMapping(ApiUrls.URL_ASSIGN_REPRESENTATIVE_TO_COMPANY)
    public ResponseEntity<CompanyDTO> assignRepresentativeToCompany(
            @Parameter(description = "ID of the company to which the representative will be assigned", required = true)
            @PathVariable Long companyId,
            @Parameter(description = "ID of the representative to assign to the company", required = true)
            @PathVariable Long representativeId) {
        CompanyDTO company = companyService.assignRepresentativeToCompany(companyId, representativeId);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    /**
     * Unassigns a representative from a company.
     *
     * @param companyId The ID of the company from which the representative will be unassigned.
     * @param representativeId The ID of the representative to be unassigned from the company.
     * @return A {@link ResponseEntity} with HTTP status code indicating the outcome of the operation.
     */
    @Operation(summary = "Unassign a representative from a company", description = "Removes a representative from a company.")
    @PostMapping(ApiUrls.URL_UNASSIGN_REPRESENTATIVE_FROM_COMPANY)
    public ResponseEntity<Void> unassignRepresentativeFromCompany(
            @Parameter(description = "ID of the company from which the representative will be unassigned", required = true)
            @PathVariable Long companyId,
            @Parameter(description = "ID of the representative to unassign from the company", required = true)
            @PathVariable Long representativeId) {
        companyService.unassignRepresentativeFromCompany(companyId, representativeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all representatives associated with a specific company.
     *
     * @param companyId The ID of the company for which to retrieve all representatives.
     * @return A {@link ResponseEntity} with a set of {@link RepresentativeDTO} for the specified company.
     */
    @Operation(summary = "Get all representatives for a company", description = "Retrieves all representatives associated with a specific company.")
    @GetMapping(ApiUrls.URL_COMPANY_REPRESENTATIVES_ALL)
    public ResponseEntity<Set<RepresentativeDTO>> getAllRepresentativesForCompany(
            @Parameter(description = "ID of the company for which to retrieve all representatives", required = true)
            @PathVariable Long companyId) {
        Set<RepresentativeDTO> representatives = companyService.getAllRepresentativesForCompany(companyId);
        return new ResponseEntity<>(representatives, HttpStatus.OK);
    }

    /**
     * Transfers a representative from their current company to another company.
     *
     * @param currentCompanyId The ID of the current company from which the representative will be transferred.
     * @param newCompanyId The ID of the new company to which the representative will be transferred.
     * @param representativeId The ID of the representative to transfer.
     * @return A {@link ResponseEntity} with HTTP status code indicating the outcome of the operation.
     */
    @Operation(summary = "Transfer a representative to another company", description = "Transfers a representative from their current company to another company.")
    @PostMapping(ApiUrls.URL_TRANSFER_REPRESENTATIVE_BETWEEN_COMPANIES)
    public ResponseEntity<Void> transferRepresentative(
            @Parameter(description = "ID of the current company from which the representative will be transferred", required = true)
            @RequestParam Long currentCompanyId,
            @Parameter(description = "ID of the new company to which the representative will be transferred", required = true)
            @RequestParam Long newCompanyId,
            @Parameter(description = "ID of the representative to transfer", required = true)
            @RequestParam Long representativeId) {
        companyService.transferRepresentative(currentCompanyId, newCompanyId, representativeId);
        return ResponseEntity.noContent().build();
    }
}

