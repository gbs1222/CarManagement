package com.example.car_management.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarFilter {
    private String brand;

    private String color;

    private Integer yearOfProduction;

    private BigDecimal price;

}
