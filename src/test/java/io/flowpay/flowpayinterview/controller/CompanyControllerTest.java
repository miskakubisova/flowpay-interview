package io.flowpay.flowpayinterview.controller;

import io.flowpay.flowpayinterview.config.ApiUrls;
import io.flowpay.flowpayinterview.exception.GlobalExceptionHandler;
import io.flowpay.flowpayinterview.model.dto.CompanyDTO;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.model.entity.Company;
import io.flowpay.flowpayinterview.model.entity.Representative;
import io.flowpay.flowpayinterview.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyControllerTest {

    private static final String COMPANY_NAME = "Acme Corporation";
    private static final String UPDATED_COMPANY_NAME = "Acme Corp";

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(companyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void createCompany() throws Exception {
        CompanyDTO companyDTO = new CompanyDTO(1L, COMPANY_NAME, null);
        when(companyService.createCompany(any(CompanyDTO.class))).thenReturn(companyDTO);

        mockMvc.perform(post(ApiUrls.URL_COMPANIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Acme Corporation\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(COMPANY_NAME));

        verify(companyService, times(1)).createCompany(any(CompanyDTO.class));
    }

    @Test
    public void getCompanyById() throws Exception {
        CompanyDTO companyDTO = new CompanyDTO(1L, COMPANY_NAME, null);
        when(companyService.getCompanyById(1L)).thenReturn(companyDTO);

        mockMvc.perform(get(ApiUrls.URL_COMPANIES + ApiUrls.URL_COMPANIES_BY_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(COMPANY_NAME));

        verify(companyService).getCompanyById(1L);
    }

    @Test
    public void updateCompany() throws Exception {
        CompanyDTO updatedCompanyDTO = new CompanyDTO(1L, UPDATED_COMPANY_NAME, null);
        when(companyService.updateCompany(eq(1L), any(CompanyDTO.class))).thenReturn(updatedCompanyDTO);

        mockMvc.perform(put(ApiUrls.URL_COMPANIES + ApiUrls.URL_COMPANIES_BY_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + UPDATED_COMPANY_NAME + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(UPDATED_COMPANY_NAME));

        verify(companyService).updateCompany(eq(1L), any(CompanyDTO.class));
    }

    @Test
    public void deleteCompany() throws Exception {
        doNothing().when(companyService).deleteCompany(1L);

        mockMvc.perform(delete(ApiUrls.URL_COMPANIES + ApiUrls.URL_COMPANIES_BY_ID, 1L))
                .andExpect(status().isNoContent());

        verify(companyService).deleteCompany(1L);
    }

    @Test
    public void getNonExistingCompanyById() throws Exception {
        when(companyService.getCompanyById(anyLong())).thenThrow(new EntityNotFoundException("Company not found"));

        mockMvc.perform(get(ApiUrls.URL_COMPANIES + ApiUrls.URL_COMPANIES_BY_ID, 99L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(jsonPath("$.message").value("Company not found"));

        verify(companyService).getCompanyById(anyLong());
    }

    @Test
    public void updateNonExistingCompany() throws Exception {
        when(companyService.updateCompany(eq(99L), any(CompanyDTO.class)))
                .thenThrow(new EntityNotFoundException("Company not found"));

        mockMvc.perform(put(ApiUrls.URL_COMPANIES + ApiUrls.URL_COMPANIES_BY_ID, 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Non Existing Corp\"}"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(jsonPath("$.message").value("Company not found"));

        verify(companyService).updateCompany(eq(99L), any(CompanyDTO.class));
    }

    @Test
    void assignRepresentativeToCompany() throws Exception {
        mockMvc.perform(post(ApiUrls.URL_COMPANIES + ApiUrls.URL_ASSIGN_REPRESENTATIVE_TO_COMPANY, 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(companyService).assignRepresentativeToCompany(1L, 1L);
    }

    @Test
    void unassignRepresentativeFromCompany() throws Exception {
        mockMvc.perform(post(ApiUrls.URL_COMPANIES + ApiUrls.URL_UNASSIGN_REPRESENTATIVE_FROM_COMPANY, 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(companyService).unassignRepresentativeFromCompany(1L, 1L);
    }

    @Test
    void getAllRepresentativesForCompany() throws Exception {
        Set<RepresentativeDTO> representatives = new HashSet<>();
        representatives.add(new RepresentativeDTO(1L, "John", "Doe"));

        when(companyService.getAllRepresentativesForCompany(anyLong())).thenReturn(representatives);

        mockMvc.perform(get(ApiUrls.URL_COMPANIES + ApiUrls.URL_COMPANY_REPRESENTATIVES_ALL, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));

        verify(companyService).getAllRepresentativesForCompany(1L);
    }

    @Test
    void transferRepresentative() throws Exception {
        mockMvc.perform(post(ApiUrls.URL_COMPANIES + ApiUrls.URL_TRANSFER_REPRESENTATIVE_BETWEEN_COMPANIES)
                        .param("currentCompanyId", "1")
                        .param("newCompanyId", "2")
                        .param("representativeId", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(companyService).transferRepresentative(1L, 2L, 2L);
    }
}
