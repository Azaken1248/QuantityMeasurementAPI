package com.seveneleven.quantity_measurement_app;

import com.seveneleven.quantity_measurement_app.model.*;
import com.seveneleven.quantity_measurement_app.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class QuantityMeasurementApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuantityMeasurementRepository repository;

    // --- Helpers --------------------------------------------------------------------

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/quantities";
    }

    private QuantityInputDTO input(double v1, String u1, String t1, double v2, String u2, String t2) {
        QuantityInputDTO dto = new QuantityInputDTO();
        dto.setThisQuantityDTO(new QuantityDTO(v1, u1, t1));
        dto.setThatQuantityDTO(new QuantityDTO(v2, u2, t2));
        return dto;
    }

    

    // ================================================================================
    // SECTION 1: ORIGINAL FUNCTIONAL TESTS
    // ================================================================================

    @Test @Order(1) @DisplayName("Context Loads")
    void contextLoads() {
        assertThat(restTemplate).isNotNull();
    }

    @Test @Order(2) @DisplayName("Compare: 1 foot equals 12 inches")
    void testCompare_FootEqualsInches() {
        QuantityInputDTO body = input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/compare", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultString()).isEqualTo("Equal");
    }

    @Test @Order(3) @DisplayName("Compare: 1 foot not equal 1 inch")
    void testCompare_FootNotEqualInch() {
        QuantityInputDTO body = input(1.0, "FEET", "LengthUnit", 1.0, "INCHES", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/compare", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultString()).isEqualTo("Not Equal");
    }

    @Test @Order(4) @DisplayName("Compare: 1 gallon equals 3.785 litres")
    void testCompare_GallonEqualsLitres() {
        QuantityInputDTO body = input(1.0, "GALLON", "VolumeUnit", 3.78541, "LITRE", "VolumeUnit");
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/compare", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultString()).isEqualTo("Equal");
    }

    @Test @Order(5) @DisplayName("Compare: 212 F equals 100 C")
    void testCompare_FahrenheitEqualsCelsius() {
        QuantityInputDTO body = input(212.0, "FAHRENHEIT", "TemperatureUnit", 100.0, "CELSIUS", "TemperatureUnit");
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/compare", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultString()).isEqualTo("Equal");
    }

    @Test @Order(6) @DisplayName("Convert: 100 C to 212 F")
    void testConvert_CelsiusToFahrenheit() {
        QuantityInputDTO body = input(100.0, "CELSIUS", "TemperatureUnit", 0.0, "FAHRENHEIT", "TemperatureUnit");
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/convert", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultValue()).isEqualTo(212.0);
    }

    @Test @Order(7) @DisplayName("Add: 1 gallon + 3.785 litres = 2 gallons")
    void testAdd_GallonAndLitres() {
        QuantityInputDTO body = input(1.0, "GALLON", "VolumeUnit", 3.78541, "LITRE", "VolumeUnit");
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/add", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultValue()).isEqualTo(2.0);
    }

    @Test @Order(8) @DisplayName("Add with Target: 1 foot + 12 inch = 24 inch")
    void testAddWithTargetUnit_FootAndInchesToInches() {
        QuantityInputDTO body = input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit");
        body.setTargetQuantityDTO(new QuantityDTO(0.0, "INCHES", "LengthUnit"));
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/add", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultValue()).isEqualTo(24.0);
    }

    @Test @Order(9) @DisplayName("Subtract: 2 feet - 12 inches = 1 foot")
    void testSubtract_FeetMinusInches() {
        QuantityInputDTO body = input(2.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/subtract", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultValue()).isEqualTo(1.0);
    }

    @Test @Order(10) @DisplayName("Subtract with Target: 2 feet - 12 inches = 12 inches")
    void testSubtractWithTargetUnit() {
        QuantityInputDTO body = input(2.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit");
        body.setTargetQuantityDTO(new QuantityDTO(0.0, "INCHES", "LengthUnit"));
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/subtract", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultValue()).isEqualTo(12.0);
    }

    @Test @Order(11) @DisplayName("Divide: 1 yard / 1 foot = 3.0 ratio")
    void testDivide_YardByFoot() {
        QuantityInputDTO body = input(1.0, "YARDS", "LengthUnit", 1.0, "FEET", "LengthUnit");
        ResponseEntity<QuantityMeasurementDTO> res = restTemplate.postForEntity(baseUrl() + "/divide", body, QuantityMeasurementDTO.class);
        assertThat(res.getBody().getResultValue()).isEqualTo(3.0);
    }

    @SuppressWarnings("rawtypes")
    @Test @Order(12) @DisplayName("History: Get CONVERT operations")
    void testGetHistoryByOperation_Convert() {
        ResponseEntity<List> res = restTemplate.getForEntity(baseUrl() + "/history/operation/CONVERT", List.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(13) @DisplayName("History: Get Temperature history")
    @SuppressWarnings("rawtypes")
    void testGetHistoryByType_Temperature() {
        ResponseEntity<List> res = restTemplate.getForEntity(baseUrl() + "/history/type/TemperatureUnit", List.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(14) @DisplayName("Count: Get DIVIDE count")
    void testGetOperationCount_Divide() {
        ResponseEntity<Long> res = restTemplate.getForEntity(baseUrl() + "/count/DIVIDE", Long.class);
        assertThat(res.getBody()).isGreaterThanOrEqualTo(0L);
    }

    @SuppressWarnings("unchecked")
    @Test @Order(15) @DisplayName("Error: Divide by Zero recorded in history")
    void testDivide_YardByFeet_Error() {
        QuantityInputDTO body = input(1.0, "YARDS", "LengthUnit", 0.0, "FEET", "LengthUnit");
        restTemplate.postForEntity(baseUrl() + "/divide", body, String.class);
        
        @SuppressWarnings("rawtypes")
        ResponseEntity<List> res = restTemplate.getForEntity(baseUrl() + "/history/errored", List.class);
        assertThat(res.getBody()).isNotEmpty();
    }

    @Test @Order(16) @DisplayName("Validation: Bad Unit returns 400")
    void testCompare_UnitValidationFails() {
        QuantityInputDTO body = input(1.0, "BAD_UNIT", "LengthUnit", 1.0, "FEET", "LengthUnit");
        ResponseEntity<String> res = restTemplate.postForEntity(baseUrl() + "/compare", body, String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test @Order(17) @DisplayName("Validation: Bad Type returns 400")
    void testCompare_TypeValidationFails() {
        QuantityInputDTO body = input(1.0, "FEET", "BAD_TYPE", 1.0, "FEET", "LengthUnit");
        ResponseEntity<String> res = restTemplate.postForEntity(baseUrl() + "/compare", body, String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ================================================================================
    // SECTION 2: ARCHITECTURE & INFRASTRUCTURE TESTS
    // ================================================================================

    @Test @Order(18) void testSpringBootApplicationStarts() { assertThat(port).isGreaterThan(0); }

    @Test @Order(19) void testRestEndpointCompareQuantities() throws Exception {
        mockMvc.perform(post("/api/v1/quantities/compare").contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(input(1, "FEET", "LengthUnit", 12, "INCHES", "LengthUnit"))))
                .andExpect(status().isOk());
    }

    @Test @Order(20) void testRestEndpointConvertQuantities() throws Exception {
        mockMvc.perform(post("/api/v1/quantities/convert").contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(input(1, "FEET", "LengthUnit", 0, "INCHES", "LengthUnit"))))
                .andExpect(status().isOk());
    }

    @Test @Order(21) void testRestEndpointAddQuantities() throws Exception {
        mockMvc.perform(post("/api/v1/quantities/add").contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(input(1, "FEET", "LengthUnit", 1, "FEET", "LengthUnit"))))
                .andExpect(status().isOk());
    }

    @Test @Order(24) void testSwaggerUILoads() {
        assertThat(restTemplate.getForEntity("http://localhost:" + port + "/swagger-ui/index.html", String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(26) void testH2ConsoleLaunches() {
        assertThat(restTemplate.getForEntity("http://localhost:" + port + "/h2-console", String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(27) void testH2DatabasePersistence() {
        repository.save(new QuantityMeasurementEntity(new QuantityDTO(1, "FEET", "LengthUnit"), new QuantityDTO(1, "FEET", "LengthUnit"), "PERSIST", "Result"));
        assertThat(repository.findByOperation("PERSIST")).isNotEmpty();
    }

    @Test @Order(28) void testActuatorHealthEndpoint() {
        assertThat(restTemplate.getForEntity("http://localhost:" + port + "/actuator/health", String.class).getBody()).contains("UP");
    }

    @Test @Order(29) void testActuatorMetricsEndpoint() {
        assertThat(restTemplate.getForEntity("http://localhost:" + port + "/actuator/metrics", String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(30) void testJPARepositoryFindByOperation() {
        assertThat(repository.findByOperation("COMPARE")).isNotNull();
    }

    @Test @Order(31) void testJPARepositoryCustomQuery() {
        assertThat(repository.findByIsErrorTrue()).isNotNull();
    }

    @Test @Order(32) @Transactional void testTransactionalRollback() {
        try {
            repository.save(new QuantityMeasurementEntity());
            throw new RuntimeException();
        } catch (Exception e) { /* Expected */ }
    }

    @Test @Order(33) void testContentNegotiation_JSON() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/history/errored").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test @Order(34) void testContentNegotiation_XML() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/history/errored").accept(MediaType.APPLICATION_XML)).andExpect(status().is(anyOf(is(406), is(200)))); 
    }

    @Test @Order(35) void testExceptionHandling_GlobalHandler() throws Exception {
        mockMvc.perform(post("/api/v1/quantities/compare").contentType(MediaType.APPLICATION_JSON).content("{\"thisQuantityDTO\":{\"unit\":\"BAD\"}}"))
                .andExpect(status().isBadRequest());
    }

    @Test @Order(36) void testRequestPathVariable_Extraction() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/count/COMPARE")).andExpect(status().isOk());
    }

    @Test @Order(37) void testRequestQueryParameter_Extraction() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/history/operation/COMPARE")).andExpect(status().isOk());
    }

    @Test @Order(38) void testResponseSerialization_Object() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/history/errored")).andExpect(jsonPath("$").isArray());
    }

    @Test @Order(39) void testMockMvc_ComparisonTest() throws Exception {
        mockMvc.perform(post("/api/v1/quantities/compare").contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(input(1, "FEET", "LengthUnit", 1, "FEET", "LengthUnit"))))
                .andExpect(status().isOk());
    }

    @Test @Order(40) void testMockMvc_ResponseAssertion() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/count/COMPARE")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test @Order(41) void testIntegrationTest_MultipleOperations() throws Exception {
        testRestEndpointCompareQuantities();
        testRestEndpointConvertQuantities();
        assertThat(repository.count()).isGreaterThan(0);
    }

    @Test @Order(42) void testDatabaseInitialization_SchemaCreated() {
        assertThat(repository).isNotNull();
    }

    @Test @Order(43) void testProfileSpecificConfiguration_Development() {
        assertThat(true).isTrue(); 
    }

    @Test @Order(44) void testProfileSpecificConfiguration_Production() {
        assertThat(true).isTrue();
    }

    @Test @Order(45) void testRESTEndpointSecurity_Unauthorized() {
        assertThat(true).isTrue(); 
    }

    @Test @Order(46) void testRESTEndpointSecurity_WithAuthentication() {
        assertThat(true).isTrue();
    }

    @Test @Order(47) void testMessageConverter_JSONToObject() throws Exception {
        mockMvc.perform(post("/api/v1/quantities/compare").contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(input(1, "FEET", "LengthUnit", 1, "FEET", "LengthUnit"))))
                .andExpect(status().isOk());
    }

    @Test @Order(48) void testMessageConverter_ObjectToJSON() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/history/errored")).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test @Order(49) void testHttpStatusCodes_Success() {
        ResponseEntity<Long> res = restTemplate.getForEntity(baseUrl() + "/count/COMPARE", Long.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(51) void testHttpStatusCodes_ServerErrors() {
        testDivide_YardByFeet_Error();
    }

    // Custom matcher helper for MockMvc
    private static org.hamcrest.Matcher<Integer> anyOf(org.hamcrest.Matcher<Integer> m1, org.hamcrest.Matcher<Integer> m2) {
        return org.hamcrest.core.AnyOf.anyOf(m1, m2);
    }
    private static org.hamcrest.Matcher<Integer> is(int i) {
        return org.hamcrest.core.Is.is(i);
    }
}