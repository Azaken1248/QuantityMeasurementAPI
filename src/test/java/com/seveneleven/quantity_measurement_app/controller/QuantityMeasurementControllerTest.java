package com.seveneleven.quantity_measurement_app.controller;

import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.seveneleven.quantity_measurement_app.model.QuantityDTO;
import com.seveneleven.quantity_measurement_app.model.QuantityInputDTO;
import com.seveneleven.quantity_measurement_app.model.QuantityMeasurementDTO;
import com.seveneleven.quantity_measurement_app.service.IQuantityMeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * QuantityMeasurementControllerTest.java – Updated to resolve JPA Bean errors.
 * * We exclude DataSource and Hibernate auto-configurations because @WebMvcTest 
 * normally struggles when @EnableJpaRepositories is present on the main Application class.
 */
@WebMvcTest(controllers = QuantityMeasurementController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class, 
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false) 
public class QuantityMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IQuantityMeasurementService service;

    @Autowired
    private ObjectMapper objectMapper;

    private QuantityInputDTO quantity1;
    private QuantityMeasurementDTO measurementResult;

    @BeforeEach
    public void setUp() {
        quantity1 = new QuantityInputDTO();
        quantity1.setThisQuantityDTO(new QuantityDTO(1.0, "FEET", "LengthUnit"));
        quantity1.setThatQuantityDTO(new QuantityDTO(12.0, "INCHES", "LengthUnit"));

        measurementResult = new QuantityMeasurementDTO();
        measurementResult.setThisValue(quantity1.getThisQuantityDTO().getValue());
        measurementResult.setThisUnit(quantity1.getThisQuantityDTO().getUnit());
        measurementResult.setThisMeasurementType(quantity1.getThisQuantityDTO().getMeasurementType());
        
        measurementResult.setThatValue(quantity1.getThatQuantityDTO().getValue());
        measurementResult.setThatUnit(quantity1.getThatQuantityDTO().getUnit());
        measurementResult.setThatMeasurementType(quantity1.getThatQuantityDTO().getMeasurementType());
    }

    @Test
    public void testCompareQuantities_Success() throws Exception {
        measurementResult.setOperation("Compare");
        measurementResult.setResultString("true");
        measurementResult.setResultValue(0.0);
        measurementResult.setResultUnit(null);
        measurementResult.setResultMeasurementType(null);
        measurementResult.setError(false);

        Mockito.when(service.compare(
                Mockito.any(QuantityDTO.class),
                Mockito.any(QuantityDTO.class)
        )).thenReturn(measurementResult);

        mockMvc.perform(
                post("/api/v1/quantities/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quantity1))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultString").value("true"));
    }

    @Test
    public void testAddQuantities_Success() throws Exception {
        measurementResult.setOperation("add");
        measurementResult.setResultValue(2.0);
        measurementResult.setResultUnit("FEET");
        measurementResult.setResultMeasurementType("LengthUnit");
        measurementResult.setError(false);

        Mockito.when(service.add(
                Mockito.any(QuantityDTO.class),
                Mockito.any(QuantityDTO.class)
        )).thenReturn(measurementResult);

        mockMvc.perform(
                post("/api/v1/quantities/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quantity1))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultValue").value(2.0));
    }

    @Test
    public void testGetOperationHistory_Success() throws Exception {
        Mockito.when(service.getOperationHistory("COMPARE"))
                .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(
                get("/api/v1/quantities/history/operation/COMPARE")
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testGetOperationCount_Success() throws Exception {
        Mockito.when(service.getOperationCount("COMPARE"))
                .thenReturn(0L);

        mockMvc.perform(
                get("/api/v1/quantities/count/COMPARE")
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("0"));
    }
}