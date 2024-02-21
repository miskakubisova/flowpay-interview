package io.flowpay.flowpayinterview.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Data Transfer Object representing a company.
 * This class is used to transfer company data between processes,
 * encapsulating the company's details, including its associated representatives.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class CompanyDTO {

    /**
     * Unique identifier of the company.
     */
    private Long id;

    /**
     * Name of the company.
     * Must not be blank and must not exceed 255 characters in length.
     */
    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    /**
     * Set of representatives associated with the company.
     * Each representative is represented by a {@link RepresentativeDTO}.
     */
    private Set<RepresentativeDTO> representatives;

}
