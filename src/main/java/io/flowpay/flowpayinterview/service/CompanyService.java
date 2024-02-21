package io.flowpay.flowpayinterview.service;

import io.flowpay.flowpayinterview.mapper.CommonMapper;
import io.flowpay.flowpayinterview.model.dto.CompanyDTO;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.model.entity.Company;
import io.flowpay.flowpayinterview.model.entity.Representative;
import io.flowpay.flowpayinterview.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides services for managing companies, including CRUD operations,
 * assigning and unassigning representatives, and retrieving companies and their representatives.
 */
@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RepresentativeService representativeService;
    private final CommonMapper commonMapper;

    public CompanyService(final CompanyRepository companyRepository,
                          final RepresentativeService representativeService,
                          final CommonMapper commonMapper) {
        this.companyRepository = companyRepository;
        this.representativeService = representativeService;
        this.commonMapper = commonMapper;
    }

    /**
     * Creates a new company based on the provided DTO.
     *
     * @param companyDTO The DTO containing company information.
     * @return The DTO representing the created company.
     */
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = commonMapper.companyDtoToEntity(companyDTO);
        return commonMapper.companyToDto(companyRepository.save(company));
    }

    /**
     * Retrieves a company by its ID, throwing an exception if not found.
     *
     * @param id The ID of the company.
     * @return The DTO representing the retrieved company.
     * @throws EntityNotFoundException If the company with the given ID does not exist.
     */
    public CompanyDTO getCompanyById(Long id) {
        return commonMapper.companyToDto(findCompanyById(id));
    }

    /**
     * Retrieves companies by their name.
     *
     * @param name The name of the companies to retrieve.
     * @return A list of DTOs representing the retrieved companies.
     */
    public List<CompanyDTO> getCompanyByName(String name) {
        return companyRepository.findAllByName(name).stream()
                .map(commonMapper::companyToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves companies without any representatives.
     *
     * @return A list of DTOs representing the companies without representatives.
     */
    public List<CompanyDTO> getCompaniesWithoutRepresentative() {
        return companyRepository.findAllByRepresentativesEmpty().stream()
                .map(commonMapper::companyToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all existing companies.
     *
     * @return A list of DTOs representing all existing companies.
     */
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(commonMapper::companyToDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates the details of an existing company.
     *
     * @param id               The ID of the company to update.
     * @param updatedCompanyDTO The updated details of the company.
     * @return The DTO representing the updated company.
     * @throws EntityNotFoundException If the company with the given ID does not exist.
     */
    public CompanyDTO updateCompany(Long id, CompanyDTO updatedCompanyDTO) {
        Company existingCompany = findCompanyById(id);
        commonMapper.updateCompanyFromDto(updatedCompanyDTO, existingCompany);
        return commonMapper.companyToDto(companyRepository.save(existingCompany));
    }

    /**
     * Deletes a company by its ID.
     *
     * @param id The ID of the company to delete.
     */
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    /**
     * Assigns a representative to a company, updating both entities.
     *
     * @param companyId         The ID of the company.
     * @param representativeId  The ID of the representative to assign.
     * @return The updated company DTO.
     * @throws EntityNotFoundException If either the company or representative does not exist.
     */
    public CompanyDTO assignRepresentativeToCompany(Long companyId, Long representativeId) {
        Company company = findCompanyById(companyId);
        RepresentativeDTO representativeDTO = representativeService.getRepresentativeById(representativeId);

        company.addRepresentative(commonMapper.representativeDtoToEntity(representativeDTO));
        return commonMapper.companyToDto(companyRepository.save(company));
    }

    /**
     * Unassigns a representative from a company.
     *
     * @param companyId        The ID of the company.
     * @param representativeId The ID of the representative to unassign.
     * @throws EntityNotFoundException If either the company or representative does not exist.
     */
    public void unassignRepresentativeFromCompany(Long companyId, Long representativeId) {
        Company company = findCompanyById(companyId);
        Representative representative = commonMapper.representativeDtoToEntity(representativeService.getRepresentativeById(representativeId));

        if (company.getRepresentatives().removeIf(r -> r.getId().equals(representative.getId()))) {
            companyRepository.save(company);
        }
    }

    /**
     * Retrieves all representatives of a specific company.
     *
     * @param companyId The ID of the company.
     * @return A set of DTOs for all representatives.
     * @throws EntityNotFoundException If the company is not found.
     */
    public Set<RepresentativeDTO> getAllRepresentativesForCompany(Long companyId) {
        Company company = findCompanyById(companyId);
        return company.getRepresentatives().stream()
                .map(commonMapper::representativeToDto)
                .collect(Collectors.toSet());
    }

    /**
     * Transfers a representative from one company to another.
     *
     * @param currentCompanyId The ID of the current company.
     * @param newCompanyId     The ID of the new company.
     * @param representativeId The ID of the representative to transfer.
     * @throws EntityNotFoundException If any entity is not found.
     * @throws IllegalStateException   If the representative is not part of the current company.
     */
    public void transferRepresentative(Long currentCompanyId, Long newCompanyId, Long representativeId) {
        Company currentCompany = findCompanyById(currentCompanyId);
        Company newCompany = findCompanyById(newCompanyId);
        Representative representative = commonMapper.representativeDtoToEntity(representativeService.getRepresentativeById(representativeId));

        if (!currentCompany.getRepresentatives().contains(representative)) {
            throw new IllegalStateException("Representative not part of current company");
        }

        currentCompany.removeRepresentative(representative);
        newCompany.addRepresentative(representative);
        companyRepository.saveAll(List.of(currentCompany, newCompany));
    }

    private Company findCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id " + id));
    }
}
