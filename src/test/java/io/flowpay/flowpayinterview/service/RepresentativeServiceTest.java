package io.flowpay.flowpayinterview.service;

import io.flowpay.flowpayinterview.mapper.CommonMapper;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.model.entity.Company;
import io.flowpay.flowpayinterview.model.entity.Representative;
import io.flowpay.flowpayinterview.repository.CompanyRepository;
import io.flowpay.flowpayinterview.repository.RepresentativeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RepresentativeServiceTest {

    @Mock
    private RepresentativeRepository representativeRepository;

    @Mock
    private CompanyRepository companyRepository;

    private CommonMapper commonMapper;

    private RepresentativeService representativeService;

    private Representative representative;

    @BeforeEach
    void setUp() {
        representative = new Representative();
        representative.setId(1L);
        representative.setFirstName("John");
        representative.setLastName("Doe");
        commonMapper = Mappers.getMapper(CommonMapper.class);
        representativeService = new RepresentativeService(representativeRepository, companyRepository, commonMapper);
    }

    @Test
    void getRepresentativeById() {
        when(representativeRepository.findById(anyLong())).thenReturn(Optional.of(representative));
        RepresentativeDTO found = representativeService.getRepresentativeById(1L);
        assertThat(found)
                .isNotNull()
                .extracting(RepresentativeDTO::getFirstName, RepresentativeDTO::getLastName)
                .containsExactlyInAnyOrder("John", "Doe");
    }

    @Test
    void getRepresentativeFirstNameAndLastName() {
        when(representativeRepository.findAllByFirstNameAndLastName("John", "Doe")).thenReturn(Set.of(representative));
        Set<RepresentativeDTO> representatives = representativeService.getRepresentativesByFirstNameAndLastName("John", "Doe");
        assertThat(representatives)
                .isNotEmpty()
                .extracting(RepresentativeDTO::getFirstName, RepresentativeDTO::getLastName)
                .containsExactlyInAnyOrder(Tuple.tuple("John", "Doe"));
    }

    @Test
    void createRepresentative() {
        when(representativeRepository.save(any(Representative.class))).thenReturn(representative);
        RepresentativeDTO saved = representativeService.createRepresentative(commonMapper.representativeToDto(representative));
        assertNotNull(saved);
        verify(representativeRepository).save(representative);
    }

    @Test
    void updateRepresentative() {
        RepresentativeDTO updatedRepresentative = RepresentativeDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .build();

        when(representativeRepository.findById(representative.getId())).thenReturn(Optional.of(representative));
        when(representativeRepository.save(any(Representative.class))).thenReturn(commonMapper.representativeDtoToEntity(updatedRepresentative));

        RepresentativeDTO updated = representativeService.updateRepresentative(representative.getId(), updatedRepresentative);
        assertThat(updated)
                .isNotNull()
                .extracting(RepresentativeDTO::getFirstName, RepresentativeDTO::getLastName)
                .containsExactlyInAnyOrder("Jane", "Doe");
    }

    @Test
    void deleteRepresentative() {
        doNothing().when(representativeRepository).deleteById(representative.getId());
        representativeService.deleteRepresentative(representative.getId());
        verify(representativeRepository).deleteById(representative.getId());
    }

    @Test
    void deleteRepresentativeAssignedToCompany() {
        doNothing().when(representativeRepository).deleteById(representative.getId());
        doNothing().when(companyRepository).disassociateRepresentativeFromAllCompanies(representative.getId());
        representativeService.deleteRepresentative(representative.getId());
        verify(representativeRepository).deleteById(representative.getId());
        verify(companyRepository).disassociateRepresentativeFromAllCompanies(representative.getId());
    }

    @Test
    void getNonExistingRepresentativeById() {
        when(representativeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            representativeService.getRepresentativeById(1L);
        });

        verify(representativeRepository).findById(1L);
    }

    @Test
    void updateNonExistingRepresentative() {
        when(representativeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            representativeService.updateRepresentative(1L, commonMapper.representativeToDto(representative));
        });

        verify(representativeRepository, never()).save(any(Representative.class));
    }

    @Test
    void deleteNonExistingRepresentative() {
        doThrow(new EntityNotFoundException("Representative not found with id " + representative.getId()))
                .when(representativeRepository).deleteById(anyLong());

        assertThrows(EntityNotFoundException.class, () -> {
            representativeService.deleteRepresentative(representative.getId());
        });

        verify(representativeRepository).deleteById(representative.getId());
    }
}
