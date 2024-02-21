package io.flowpay.flowpayinterview.controller;

import io.flowpay.flowpayinterview.config.ApiUrls;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.service.RepresentativeService;
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
 * Controller for handling operations related to representatives.
 * Supports creating, retrieving, updating, and deleting representatives.
 */
@Validated
@RestController
@RequestMapping(ApiUrls.URL_REPRESENTATIVES)
public class RepresentativesController {

    private final RepresentativeService representativeService;

    public RepresentativesController(RepresentativeService representativeService) {
        this.representativeService = representativeService;
    }

    /**
     * Creates a new representative.
     *
     * @param representative DTO containing the details of the representative to be created.
     * @return ResponseEntity containing the created representative DTO.
     */
    @Operation(summary = "Create a new representative", description = "Creates a new representative with the provided details.")
    @PostMapping
    public ResponseEntity<RepresentativeDTO> createRepresentative(@Valid @RequestBody RepresentativeDTO representative) {
        RepresentativeDTO createdRepresentative = representativeService.createRepresentative(representative);
        return new ResponseEntity<>(createdRepresentative, HttpStatus.CREATED);
    }

    /**
     * Retrieves a representative by ID.
     *
     * @param id The ID of the representative to retrieve.
     * @return ResponseEntity containing the representative DTO.
     */
    @Operation(summary = "Get a representative by ID", description = "Retrieves a representative using its unique identifier.")
    @GetMapping(ApiUrls.URL_REPRESENTATIVES_BY_ID)
    public ResponseEntity<RepresentativeDTO> getRepresentativeById(@Parameter(description = "ID of the representative to retrieve", required = true)
                                                                   @PathVariable Long id) {
        RepresentativeDTO representative = representativeService.getRepresentativeById(id);
        return ResponseEntity.ok(representative);
    }

    /**
     * Retrieves representatives by first and last name.
     *
     * @param firstName The first name of the representative(s) to retrieve.
     * @param lastName The last name of the representative(s) to retrieve.
     * @return ResponseEntity containing a list of representative DTOs.
     */
    @Operation(summary = "Get representatives by first name and last name", description = "Retrieves representatives using their first name and last name.")
    @GetMapping(ApiUrls.URL_REPRESENTATIVES_BY_FIRST_AND_LAST_NAME)
    public ResponseEntity<Set<RepresentativeDTO>> getRepresentativesByFirstNameAndLastName(@Parameter(description = "First name of the representative(s) to retrieve", required = true)
                                                                                            @RequestParam String firstName,
                                                                                            @Parameter(description = "Last name of the representative(s) to retrieve", required = true)
                                                                                            @RequestParam String lastName) {
        Set<RepresentativeDTO> representatives = representativeService.getRepresentativesByFirstNameAndLastName(firstName, lastName);
        return ResponseEntity.ok(representatives);
    }

    /**
     * Retrieves all representatives.
     *
     * @return ResponseEntity containing a list of all representative DTOs.
     */
    @Operation(summary = "Get all representatives", description = "Retrieves all existing representatives.")
    @GetMapping(ApiUrls.URL_REPRESENTATIVES_ALL)
    public ResponseEntity<List<RepresentativeDTO>> getAllRepresentatives() {
        List<RepresentativeDTO> representatives = representativeService.getAllRepresentatives();
        return ResponseEntity.ok(representatives);
    }

    /**
     * Updates an existing representative.
     *
     * @param id The ID of the representative to update.
     * @param representative DTO containing the updated representative details.
     * @return ResponseEntity containing the updated representative DTO.
     */
    @Operation(summary = "Update a representative", description = "Updates the details of an existing representative.")
    @PutMapping(ApiUrls.URL_REPRESENTATIVES_BY_ID)
    public ResponseEntity<RepresentativeDTO> updateRepresentative(@Parameter(description = "ID of the representative to update", required = true)
                                                                  @PathVariable Long id,
                                                                  @Valid @RequestBody RepresentativeDTO representative) {
        RepresentativeDTO updatedRepresentative = representativeService.updateRepresentative(id, representative);
        return ResponseEntity.ok(updatedRepresentative);
    }

    /**
     * Deletes a representative by ID.
     *
     * @param id The ID of the representative to delete.
     * @return ResponseEntity with HTTP status indicating the outcome.
     */
    @Operation(summary = "Delete a representative", description = "Deletes a representative using its unique identifier.")
    @DeleteMapping(ApiUrls.URL_REPRESENTATIVES_BY_ID)
    public ResponseEntity<Void> deleteRepresentative(@Parameter(description = "ID of the representative to delete", required = true)
                                                     @PathVariable Long id) {
        representativeService.deleteRepresentative(id);
        return ResponseEntity.noContent().build();
    }
}
