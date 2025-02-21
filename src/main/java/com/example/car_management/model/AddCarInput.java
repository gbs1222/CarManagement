package com.example.car_management.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCarInput {

        @NotBlank(message = "Brand is required")
        private String brand;

        @NotBlank(message = "Model is required")
        private String model;

        @NotBlank(message = "Color is required")
        private String color;

        @NotNull(message = "Year of production is required")
        @Min(value = 1950, message = "Year must be >= 1950")
        private Integer yearOfProduction;

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be >= 0")
        private BigDecimal price;
}
