package com.example.car_management.controller;

import com.example.car_management.db.entity.Car;
import com.example.car_management.model.CarFilter;
import com.example.car_management.model.CarPage;
import com.example.car_management.model.PageInput;
import com.example.car_management.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class CarSubscriptionController {

    private final CarService carService;

    public CarSubscriptionController(CarService carService) {
        this.carService = carService;
    }

    @SubscriptionMapping
    public Flux<CarPage> carSubscription(@Argument CarFilter filter, @Argument PageInput pageInput) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> {
                    Page<Car> carPage = carService.getCars(filter, pageInput, "updatedAt");
                    return new CarPage(
                            carPage.getContent(),
                            carPage.getTotalPages(),
                            carPage.getTotalElements(),
                            carPage.getSize(),
                            carPage.getNumber()
                    );
                });
    }
}
