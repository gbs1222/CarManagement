package com.example.car_management.controller;

import com.example.car_management.db.entity.Car;
import com.example.car_management.model.*;
import com.example.car_management.service.CarService;
import graphql.GraphQLException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
@Controller
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @QueryMapping
    CarPage cars(@Argument CarFilter filter, @Argument PageInput pageInput) {
        Page<Car> carPage = carService.getCars(filter, pageInput, "createdAt");
        return new CarPage(
                carPage.getContent(),
                carPage.getTotalPages(),
                carPage.getTotalElements(),
                carPage.getSize(),
                carPage.getNumber()
        );
    }

    @QueryMapping
    Car carById(@Argument UUID id) {
        try {
            return carService.getCarById(id);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @MutationMapping
    Car addCar(@Valid @Argument AddCarInput input) {
        return carService.addCar(input);
    }

    @MutationMapping
    Car updateCarDetails(@Valid @Argument UpdateCarInput input) {
        try{
            return carService.updateCarDetails(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @MutationMapping
    Boolean deleteCar(@Argument UUID id) {
        try {
            return carService.deleteCar(id);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }
}
