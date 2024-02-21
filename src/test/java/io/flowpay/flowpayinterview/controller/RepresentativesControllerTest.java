package io.flowpay.flowpayinterview.controller;

import io.flowpay.flowpayinterview.config.ApiUrls;
import io.flowpay.flowpayinterview.exception.GlobalExceptionHandler;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.model.entity.Representative;
import io.flowpay.flowpayinterview.service.RepresentativeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RepresentativesControllerTest {

    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final String UPDATED_FIRST_NAME = "Jane";
    @Mock
    private RepresentativeService representativeService;

    @InjectMocks
    private RepresentativesController representativesController;

    private MockMvc mockMvc;

    private RepresentativeDTO representative;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(representativesController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        representative = RepresentativeDTO.builder()
                .id(1L)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    @Test
    void getRepresentativeById() throws Exception {
        when(representativeService.getRepresentativeById(anyLong())).thenReturn(representative);

        mockMvc.perform(get(ApiUrls.URL_REPRESENTATIVES + ApiUrls.URL_REPRESENTATIVES_BY_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME));

        verify(representativeService).getRepresentativeById(1L);
    }

    @Test
    void getRepresentativeByFirstNameAndLastName() throws Exception {
        when(representativeService.getRepresentativesByFirstNameAndLastName(FIRST_NAME, LAST_NAME)).thenReturn(Set.of(representative));

        mockMvc.perform(get(ApiUrls.URL_REPRESENTATIVES + ApiUrls.URL_REPRESENTATIVES_BY_FIRST_AND_LAST_NAME)
                        .param("firstName", FIRST_NAME)
                        .param("lastName", LAST_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$[0].lastName").value(LAST_NAME));

        verify(representativeService).getRepresentativesByFirstNameAndLastName(FIRST_NAME, LAST_NAME);
    }

    @Test
    void createRepresentative() throws Exception {
        RepresentativeDTO mockRepresentativeDTO = new RepresentativeDTO(1L, "Jane", "Doe");
        when(representativeService.createRepresentative(any(RepresentativeDTO.class))).thenReturn(mockRepresentativeDTO);

        mockMvc.perform(post(ApiUrls.URL_REPRESENTATIVES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(representativeService).createRepresentative(any(RepresentativeDTO.class));
    }

    @Test
    void updateRepresentative() throws Exception {
        RepresentativeDTO updatedRepresentative = RepresentativeDTO.builder()
                .id(representative.getId())
                .firstName(UPDATED_FIRST_NAME)
                .lastName(LAST_NAME)
                .build();

        when(representativeService.updateRepresentative(eq(representative.getId()), any(RepresentativeDTO.class)))
                .thenReturn(updatedRepresentative);

        mockMvc.perform(put(ApiUrls.URL_REPRESENTATIVES + ApiUrls.URL_REPRESENTATIVES_BY_ID, representative.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(UPDATED_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME));

        verify(representativeService).updateRepresentative(eq(representative.getId()), any(RepresentativeDTO.class));
    }

    @Test
    void deleteRepresentative() throws Exception {
        doNothing().when(representativeService).deleteRepresentative(representative.getId());

        mockMvc.perform(delete(ApiUrls.URL_REPRESENTATIVES + ApiUrls.URL_REPRESENTATIVES_BY_ID, representative.getId()))
                .andExpect(status().isNoContent());

        verify(representativeService).deleteRepresentative(representative.getId());
    }

    @Test
    void updateNonExistingRepresentative() throws Exception {
        Representative updatedRepresentative = new Representative();
        updatedRepresentative.setFirstName(UPDATED_FIRST_NAME);
        updatedRepresentative.setLastName(LAST_NAME);

        when(representativeService.updateRepresentative(eq(99L), any(RepresentativeDTO.class)))
                .thenThrow(new EntityNotFoundException("Representative not found with id: 99"));

        mockMvc.perform(put(ApiUrls.URL_REPRESENTATIVES + ApiUrls.URL_REPRESENTATIVES_BY_ID, 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isNotFound());

        verify(representativeService, times(1)).updateRepresentative(eq(99L), any(RepresentativeDTO.class));
    }

    @Test
    void deleteNonExistingRepresentative() throws Exception {
        doThrow(new EntityNotFoundException("Representative not found with id: 99"))
                .when(representativeService).deleteRepresentative(99L);

        mockMvc.perform(delete(ApiUrls.URL_REPRESENTATIVES + ApiUrls.URL_REPRESENTATIVES_BY_ID, 99L))
                .andExpect(status().isNotFound());

        verify(representativeService, times(1)).deleteRepresentative(99L);
    }
}
