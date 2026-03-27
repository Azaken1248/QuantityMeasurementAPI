package com.seveneleven.quantity_measurement_app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * This is the QuantityInputDTO class which serves as a Data Transfer Object for receiving
 * input data for quantity measurement operations. It contains three fields: thisQuantityDTO,
 * thatQuantityDTO, and an optional targetQuantityDTO for addition and subtraction operations.
 * The class is annotated with Lombok's @Data for generating boilerplate code and Swagger's
 * @Schema for API documentation. Validation annotations are used to ensure that the required
 * fields are not null and that the nested QuantityDTO objects are valid.
 *
 * We had to create this new DTO class to accommodate passing multiple QuantityDTO objects
 * in a single request for operations like addition and subtraction, where we need both the
 * quantities to be operated on and an optional target unit for the result. This allows us to
 * have a cleaner API design and better encapsulation of the input data for these operations.
 *
 * @author Developer
 * @version 17.0
 * @since 17.0
 */
@Data
@Schema(example = """
        {
          "thisQuantityDTO":   { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
          "thatQuantityDTO":   { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" },
          "targetQuantityDTO": { "value": 0.0,  "unit": "INCHES", "measurementType": "LengthUnit" }
        }
        """)
public class QuantityInputDTO {

    @Valid
    @NotNull(message = "First quantity cannot be null")
    private QuantityDTO thisQuantityDTO;

    @Valid
    @NotNull(message = "Second quantity cannot be null")
    private QuantityDTO thatQuantityDTO;

    // Optional field for addition and subtraction operations
    @Valid
    @Schema(nullable = true)
    private QuantityDTO targetQuantityDTO;
}