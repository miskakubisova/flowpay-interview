package io.flowpay.flowpayinterview.service;

import io.flowpay.flowpayinterview.mapper.CommonMapper;
import io.flowpay.flowpayinterview.model.dto.CompanyDTO;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.model.entity.Company;
import io.flowpay.flowpayinterview.model.entity.Representative;
import io.flowpay.flowpayinterview.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private RepresentativeService representativeService;
    private CommonMapper commonMapper;
    private CompanyService companyService;
    private Company company;
    private Representative representative;

    @BeforeEach
    void setUp() {
        company = new Company(1L, "Acme Corporation", new HashSet<>());
        representative = new Representative(1L, "John", "Doe");
        commonMapper = Mappers.getMapper(CommonMapper.class);
        companyService = new CompanyService(companyRepository, representativeService, commonMapper);
    }

    @Test
    public void createCompany() {
        Company company = new Company(1L, "Acme Corporation", Set.of());
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        CompanyDTO savedCompany = companyService.createCompany(commonMapper.companyToDto(company));
        assertNotNull(savedCompany);
        assertEquals("Acme Corporation", savedCompany.getName());
    }

    @Test
    public void getCompanyById() {
        Company company = new Company(1L, "Acme Corporation", Set.of());
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        CompanyDTO foundCompany = companyService.getCompanyById(1L);
        assertNotNull(foundCompany);
        assertEquals("Acme Corporation", foundCompany.getName());
    }

    @Test
    public void getCompanyByName() {
        final String companyName = "Acme Corporation";
        Company company = new Company(1L, companyName, Set.of());
        when(companyRepository.findAllByName(companyName)).thenReturn(List.of(company));
        List<CompanyDTO> companies = companyService.getCompanyByName(companyName);
        assertThat(companies)
                .isNotEmpty()
                .extracting(CompanyDTO::getName)
                .containsExactlyInAnyOrder(companyName);
    }

    @Test
    public void getCompaniesWithoutRepresentative() {
        final String companyName = "Acme Corporation";
        Company company = new Company(1L, companyName, Set.of());
        when(companyRepository.findAllByRepresentativesEmpty()).thenReturn(List.of(company));
        List<CompanyDTO> companies = companyService.getCompaniesWithoutRepresentative();
        assertThat(companies)
                .isNotEmpty()
                .extracting(CompanyDTO::getName)
                .containsExactlyInAnyOrder(companyName);
    }

    @Test
    public void updateCompany() {
        Company company = new Company(1L, "Acme Corporation", Set.of());
        Company updatedInfo = new Company(1L, "Acme Corp", Set.of());
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(updatedInfo);
        CompanyDTO updatedCompany = companyService.updateCompany(1L, commonMapper.companyToDto(updatedInfo));
        assertNotNull(updatedCompany);
        assertEquals("Acme Corp", updatedCompany.getName());
    }

    @Test
    public void deleteCompany() {
        doNothing().when(companyRepository).deleteById(anyLong());
        companyService.deleteCompany(1L);
        verify(companyRepository, times(1)).deleteById(1L);
    }

    @Test
    public void getNonExistingCompanyById() {
        when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.getCompanyById(1L),
                "Expected getCompanyById to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Company not found with id 1"));
    }

    @Test
    public void updateNonExistingCompany() {
        when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

        Company updatedInfo = new Company(1L, "Acme Corp", Set.of());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.updateCompany(1L, commonMapper.companyToDto(updatedInfo)),
                "Expected updateCompany to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Company not found with id 1"));
    }

    @Test
    public void deleteNonExistingCompany() {
        doThrow(new EntityNotFoundException("Company not found with id 1")).when(companyRepository).deleteById(1L);

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.deleteCompany(1L),
                "Expected deleteCompany to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Company not found with id 1"));
    }

    @Test
    void assignRepresentativeToCompany() {
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        when(representativeService.getRepresentativeById(anyLong())).thenReturn(commonMapper.representativeToDto(representative));
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyDTO updatedCompany = companyService.assignRepresentativeToCompany(company.getId(), representative.getId());

        verify(representativeService).getRepresentativeById(representative.getId());
        verify(companyRepository).save(company);
        assertTrue(updatedCompany.getRepresentatives().contains(commonMapper.representativeToDto(representative)));
    }

    @Test
    void unassignRepresentativeFromCompany() {
        company.addRepresentative(representative);
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        when(representativeService.getRepresentativeById(anyLong())).thenReturn(commonMapper.representativeToDto(representative));

        companyService.unassignRepresentativeFromCompany(company.getId(), representative.getId());

        verify(companyRepository).save(company);
        assertFalse(company.getRepresentatives().contains(representative));
    }

    @Test
    void getAllRepresentativesForCompany() {
        company.addRepresentative(representative);
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));

        Set<RepresentativeDTO> representatives = companyService.getAllRepresentativesForCompany(company.getId());

        assertTrue(representatives.contains(commonMapper.representativeToDto(representative)));
        assertEquals(1, representatives.size());
    }

    @Test
    void transferRepresentative() {
        Company currentCompany = new Company(1L, "Company A", new HashSet<>());
        currentCompany.addRepresentative(representative);
        Company newCompany = new Company(2L, "Company B", new HashSet<>());

        when(representativeService.getRepresentativeById(anyLong())).thenReturn(commonMapper.representativeToDto(representative));
        when(companyRepository.findById(currentCompany.getId())).thenReturn(Optional.of(currentCompany));
        when(companyRepository.findById(newCompany.getId())).thenReturn(Optional.of(newCompany));

        companyService.transferRepresentative(currentCompany.getId(), newCompany.getId(), representative.getId());

        verify(companyRepository).saveAll(List.of(company, newCompany));
        assertTrue(newCompany.getRepresentatives().contains(representative));
        assertFalse(company.getRepresentatives().contains(representative));
    }
}


