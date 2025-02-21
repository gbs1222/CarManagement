package com.example.car_management.service;

import com.example.car_management.db.entity.Car;
import com.example.car_management.model.AddCarInput;
import com.example.car_management.model.UpdateCarInput;
import com.example.car_management.model.CarFilter;
import com.example.car_management.model.PageInput;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CarService {
    Page<Car> getCars(CarFilter filter, PageInput pageInput, String sortBy);

    Car getCarById(UUID id);

    Car addCar(AddCarInput input);

    Car updateCarDetails(UpdateCarInput input);

    Boolean deleteCar(UUID id);
}
