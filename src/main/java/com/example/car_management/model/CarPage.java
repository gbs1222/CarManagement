package com.example.car_management.model;

import com.example.car_management.db.entity.Car;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarPage {
    private List<Car> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
}