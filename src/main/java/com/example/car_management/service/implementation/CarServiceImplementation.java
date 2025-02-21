package com.example.car_management.service.implementation;

import com.example.car_management.db.entity.Car;
import com.example.car_management.db.repository.CarRepository;
import com.example.car_management.model.AddCarInput;
import com.example.car_management.model.UpdateCarInput;
import com.example.car_management.model.CarFilter;
import com.example.car_management.model.PageInput;
import com.example.car_management.service.CarService;
import com.example.car_management.util.FilterUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CarServiceImplementation implements CarService {

    private final CarRepository carRepository;

    public CarServiceImplementation(CarRepository carRepository) {
        this.carRepository = carRepository;
    }


    @Override
    public Page<Car> getCars(CarFilter filter, PageInput pageInput, String sortBy) {
        var specification = FilterUtils.fromFilter(filter);
        var page = FilterUtils.toPageable(pageInput, sortBy);

        return carRepository.findAll(specification, page);
    }

    @Override
    public Car getCarById(UUID id) {
        return getCar(id);
    }

    @Override
    public Car addCar(AddCarInput input) {
        Car newCar = new Car(input.getBrand(), input.getModel(), input.getColor(), input.getYearOfProduction(), input.getPrice());
        return carRepository.save(newCar);
    }

    @Override
    public Car updateCarDetails(UpdateCarInput input) {
        Car car = getCar(input.getId());

        if (input.getBrand() != null) car.setBrand(input.getBrand());
        if (input.getModel() != null) car.setModel(input.getModel());
        if (input.getColor() != null) car.setColor(input.getColor());
        if (input.getYearOfProduction() != null) car.setYearOfProduction(input.getYearOfProduction());
        if (input.getPrice() != null) car.setPrice(input.getPrice());

        return carRepository.save(car);
    }

    @Override
    public Boolean deleteCar(UUID id) {
        Car car = getCar(id);
        carRepository.delete(car);
        return true;
    }

    private Car getCar(UUID id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));
    }
}
