package io.flowpay.flowpayinterview.service;

import io.flowpay.flowpayinterview.mapper.CommonMapper;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.model.entity.Company;
import io.flowpay.flowpayinterview.model.entity.Representative;
import io.flowpay.flowpayinterview.repository.CompanyRepository;
import io.flowpay.flowpayinterview.repository.RepresentativeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing representatives. It provides functionality for creating, updating,
 * retrieving, and deleting representatives, as well as managing their associations with companies.
 * This service plays a crucial role in handling business logic related to representatives.
 */
@Service
@Transactional
public class RepresentativeService {

    private final RepresentativeRepository representativeRepository;
    private final CompanyRepository companyRepository;
    private final CommonMapper commonMapper;

    public RepresentativeService(final RepresentativeRepository representativeRepository,
                                 final CompanyRepository companyRepository,
                                 final CommonMapper commonMapper) {
        this.representativeRepository = representativeRepository;
        this.companyRepository = companyRepository;
        this.commonMapper = commonMapper;
    }

    /**
     * Creates and persists a new representative based on the provided DTO.
     *
     * @param representativeDTO DTO containing the data for the new representative.
     * @return DTO representation of the created representative, including its new ID.
     */
    public RepresentativeDTO createRepresentative(RepresentativeDTO representativeDTO) {
        Representative representative = commonMapper.representativeDtoToEntity(representativeDTO);
        return commonMapper.representativeToDto(representativeRepository.save(representative));
    }

    /**
     * Retrieves a representative by its unique ID.
     *
     * @param id The ID of the representative to retrieve.
     * @return DTO representing the retrieved representative.
     * @throws EntityNotFoundException If no representative is found with the given ID.
     */
    @Transactional(readOnly = true)
    public RepresentativeDTO getRepresentativeById(Long id) {
        return commonMapper.representativeToDto(findRepresentativeById(id));
    }

    /**
     * Finds representatives by their first and last names.
     *
     * @param firstName The first name of the representatives to find.
     * @param lastName  The last name of the representatives to find.
     * @return A set of DTOs representing the found representatives.
     */
    @Transactional(readOnly = true)
    public Set<RepresentativeDTO> getRepresentativesByFirstNameAndLastName(String firstName, String lastName) {
        return representativeRepository.findAllByFirstNameAndLastName(firstName, lastName).stream()
                .map(commonMapper::representativeToDto)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves all representatives.
     *
     * @return A list of DTOs representing all registered representatives.
     */
    @Transactional(readOnly = true)
    public List<RepresentativeDTO> getAllRepresentatives() {
        return representativeRepository.findAll().stream()
                .map(commonMapper::representativeToDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing representative with data from the provided DTO.
     *
     * @param id                     The ID of the representative to update.
     * @param updatedRepresentativeDTO DTO with updated representative data.
     * @return DTO representing the updated representative.
     * @throws EntityNotFoundException If no representative is found with the given ID.
     */
    public RepresentativeDTO updateRepresentative(Long id, RepresentativeDTO updatedRepresentativeDTO) {
        Representative representative = findRepresentativeById(id);
        commonMapper.updateRepresentativeFromDto(updatedRepresentativeDTO, representative);
        return commonMapper.representativeToDto(representativeRepository.save(representative));
    }

    /**
     * Deletes a representative by its ID.
     *
     * @param representativeId The ID of the representative to delete.
     * @throws EntityNotFoundException If no representative is found with the given ID.
     */
    public void deleteRepresentative(Long representativeId) {
        companyRepository.disassociateRepresentativeFromAllCompanies(representativeId);
        representativeRepository.deleteById(representativeId);
    }

    /**
     * Helper method to find a representative entity by its ID, throwing an exception if not found.
     *
     * @param id The ID of the representative to find.
     * @return The found representative entity.
     * @throws EntityNotFoundException If no representative is found with the given ID.
     */
    private Representative findRepresentativeById(Long id) {
        return representativeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Representative not found with id: " + id));
    }
}
