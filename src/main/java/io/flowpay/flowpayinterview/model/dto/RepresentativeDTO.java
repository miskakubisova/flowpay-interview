package io.flowpay.flowpayinterview.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Data Transfer Object representing a representative.
 * This class is used to transfer representative data between processes,
 * encapsulating details such as first and last name.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class RepresentativeDTO {

    /**
     * Unique identifier of the representative.
     */
    private Long id;

    /**
     * First name of the representative.
     * Must not be blank and must not exceed 255 characters in length.
     */
    @NotBlank(message = "First name must not be blank")
    @Size(max = 255, message = "First name must not exceed 255 characters")
    private String firstName;

    /**
     * Last name of the representative.
     * Must not be blank and must not exceed 255 characters in length.
     */
    @NotBlank(message = "Last name must not be blank")
    @Size(max = 255, message = "Last name must not exceed 255 characters")
    private String lastName;
}
