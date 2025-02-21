package com.example.car_management.controller;

import com.example.car_management.db.repository.CarRepository;
import com.example.car_management.model.AddCarInput;
import com.example.car_management.model.CarPage;
import com.example.car_management.model.UpdateCarInput;
import com.example.car_management.util.MutationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureGraphQlTester
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
public class CarSubscriptionTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private CarRepository carRepository;

    private static final String CAR_SUBSCRIPTION = """
        subscription carSubscription($filter: FilterInput, $pageInput: PageInput) {
          carSubscription(filter: $filter, pageInput: $pageInput) {
            content {
              id
              brand
              model
              color
              yearOfProduction
              price
            }
            totalPages
            totalElements
            size
            number
          }
        }
    """;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
    }

    @Test
    void testCarSubscriptionWithUpdate() {

        Map<String, Object> filter = Map.of(
                "brand", "Toyota"
        );

        AddCarInput firstCar = new AddCarInput("Toyota", "Camry", "Red", 2020, BigDecimal.valueOf(2000));
        AtomicReference<UUID> firstCarId = new AtomicReference<>();
        AddCarInput secondCar = new AddCarInput("Toyota", "Camry", "Blue", 2021, BigDecimal.valueOf(2300));
        AddCarInput filteredCar = new AddCarInput("Audi", "A8", "Black", 2021, BigDecimal.valueOf(3000));

        MutationUtils.addCar(graphQlTester, filteredCar);

        Flux<CarPage> carUpdates = graphQlTester
                .document(CAR_SUBSCRIPTION)
                .variable("filter", filter)
                .executeSubscription()
                .toFlux("carSubscription", CarPage.class);

        StepVerifier.create(carUpdates)
                .expectNextCount(1)
                .then(() -> firstCarId.set(MutationUtils.addCar(graphQlTester, firstCar)
                        .path("addCar.id")
                        .entity(UUID.class).get()))
                .assertNext(carPage -> {
                    assertThat(carPage).isNotNull();
                    assertThat(carPage.getTotalElements()).isEqualTo(1);

                    assertThat(carPage.getContent().get(0).getId()).isEqualTo(firstCarId.get());
                    assertThat(carPage.getContent().get(0).getBrand()).isEqualTo(firstCar.getBrand());
                    assertThat(carPage.getContent().get(0).getModel()).isEqualTo(firstCar.getModel());
                    assertThat(carPage.getContent().get(0).getColor()).isEqualTo(firstCar.getColor());
                    assertThat(carPage.getContent().get(0).getYearOfProduction()).isEqualTo(firstCar.getYearOfProduction());
                })
                .then(() -> MutationUtils.addCar(graphQlTester, secondCar))
                .assertNext(carPage -> {
                    assertThat(carPage).isNotNull();
                    assertThat(carPage.getTotalElements()).isEqualTo(2);

                    assertThat(carPage.getContent().get(0).getBrand()).isEqualTo(secondCar.getBrand());
                    assertThat(carPage.getContent().get(0).getModel()).isEqualTo(secondCar.getModel());
                    assertThat(carPage.getContent().get(0).getColor()).isEqualTo(secondCar.getColor());
                    assertThat(carPage.getContent().get(0).getYearOfProduction()).isEqualTo(secondCar.getYearOfProduction());

                    assertThat(carPage.getContent().get(1).getBrand()).isEqualTo(firstCar.getBrand());
                    assertThat(carPage.getContent().get(1).getModel()).isEqualTo(firstCar.getModel());
                    assertThat(carPage.getContent().get(1).getColor()).isEqualTo(firstCar.getColor());
                    assertThat(carPage.getContent().get(1).getYearOfProduction()).isEqualTo(firstCar.getYearOfProduction());
                })
                .then(() -> {
                    UpdateCarInput updateFirstCarInput = new UpdateCarInput(firstCarId.get(), null, null, "Yellow", null, BigDecimal.valueOf(2500));
                    MutationUtils.updateCar(graphQlTester, updateFirstCarInput)
                            .path("updateCarDetails.color").entity(String.class).isEqualTo(updateFirstCarInput.getColor());
                })
                .assertNext(carPage -> {
                    assertThat(carPage).isNotNull();
                    assertThat(carPage.getTotalElements()).isEqualTo(2);
                    assertThat(carPage.getContent()).isNotEmpty();

                    // order changed based on updatedAt
                    assertThat(carPage.getContent().get(0).getBrand()).isEqualTo(firstCar.getBrand());
                    assertThat(carPage.getContent().get(0).getModel()).isEqualTo(firstCar.getModel());
                    assertThat(carPage.getContent().get(0).getColor()).isEqualTo("Yellow");
                    assertThat(carPage.getContent().get(0).getYearOfProduction()).isEqualTo(firstCar.getYearOfProduction());

                    assertThat(carPage.getContent().get(1).getBrand()).isEqualTo(secondCar.getBrand());
                    assertThat(carPage.getContent().get(1).getModel()).isEqualTo(secondCar.getModel());
                    assertThat(carPage.getContent().get(1).getColor()).isEqualTo(secondCar.getColor());
                    assertThat(carPage.getContent().get(1).getYearOfProduction()).isEqualTo(secondCar.getYearOfProduction());
                })
                .thenCancel()
                .verify();
    }
}
