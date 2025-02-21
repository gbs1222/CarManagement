package com.example.car_management.model;

import com.example.car_management.validator.NullableNotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCarInput {

        @NotNull(message = "Car ID is required")
        private UUID id;

        @NullableNotBlank(message = "Brand can't be empty")
        private String brand;
        @NullableNotBlank(message = "Model can't be empty")
        private String model;
        @NullableNotBlank(message = "Color can't be empty")
        private String color;

        @Min(value = 1950, message = "Year must be >= 1950")
        private Integer yearOfProduction;

        @Min(value = 0, message = "Price must be >= 0")
        private BigDecimal price;

}
