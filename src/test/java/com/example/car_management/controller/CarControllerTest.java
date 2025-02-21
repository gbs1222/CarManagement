package com.example.car_management.controller;

import com.example.car_management.db.repository.CarRepository;
import com.example.car_management.model.AddCarInput;
import com.example.car_management.model.UpdateCarInput;
import com.example.car_management.util.MutationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureGraphQlTester
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
public class CarControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    @Mock
    private CarRepository carRepository;

    private static final String GET_CARS_QUERY = """
        query carsQuery($filter: FilterInput, $pageInput: PageInput) {
          cars(filter: $filter, pageInput: $pageInput) {
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

    private static final String GET_CAR_BY_ID_QUERY = """
        query carByIdQuery($id: ID!) {
          carById(id: $id) {
            id
            brand
            model
            color
            yearOfProduction
            price
          }
        }
    """;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
    }

    @Test
    void testAddCarMutationSuccess() {
        AddCarInput input = new AddCarInput("Toyota", "Camry", "Red", 2020, BigDecimal.valueOf(1999.99));
        MutationUtils.addCar(graphQlTester, input)
                .path("addCar.id").hasValue()
                .path("addCar.brand").entity(String.class).isEqualTo(input.getBrand())
                .path("addCar.model").entity(String.class).isEqualTo(input.getModel())
                .path("addCar.color").entity(String.class).isEqualTo(input.getColor())
                .path("addCar.yearOfProduction").entity(Integer.class).isEqualTo(input.getYearOfProduction())
                .path("addCar.price").entity(BigDecimal.class).satisfies(price ->
                        assertThat(price.compareTo(input.getPrice())).isEqualTo(0)
                );
    }

    @Test
    void testAddCarMutationFailure() {
        Map<String, Object> input = Map.of(
                "brand", "Toyota",
                "model", "Camry",
                "color", "Blue",
                "yearOfProduction", 1940,
                "price", new BigDecimal("2000")
        );

        graphQlTester.document(MutationUtils.ADD_CAR_MUTATION)
                .variable("input", input)
                .execute()
                .errors()
                .expect(error -> {
                    assertThat(error.getMessage()).contains("Year must be >= 1950");
                    return true;
                });
    }

    @Test
    void testUpdateCarMutation() {
        AddCarInput car = new AddCarInput("Toyota", "Camry", "Red", 2020, BigDecimal.valueOf(1999.99));
        UUID existingId = MutationUtils.addCar(graphQlTester, car)
                .path("addCar.id")
                .entity(UUID.class).get();

        UpdateCarInput updateInput = new UpdateCarInput(existingId, null, null, "Green", null, BigDecimal.valueOf(2500));
        MutationUtils.updateCar(graphQlTester, updateInput)
                .path("updateCarDetails.id").entity(UUID.class).isEqualTo(existingId)
                .path("updateCarDetails.brand").entity(String.class).isEqualTo(car.getBrand())
                .path("updateCarDetails.model").entity(String.class).isEqualTo(car.getModel())
                .path("updateCarDetails.color").entity(String.class).isEqualTo(updateInput.getColor())
                .path("updateCarDetails.yearOfProduction").entity(Integer.class).isEqualTo(car.getYearOfProduction())
                .path("updateCarDetails.price").entity(BigDecimal.class).satisfies(price ->
                        assertThat(price.compareTo(updateInput.getPrice())).isEqualTo(0)
                );
    }

    @Test
    void testDeleteCarMutation() {
        AddCarInput car = new AddCarInput("Toyota", "Camry", "Red", 2020, BigDecimal.valueOf(1999.99));
        UUID existingId = MutationUtils.addCar(graphQlTester, car)
                .path("addCar.id")
                .entity(UUID.class).get();

        MutationUtils.deleteCar(graphQlTester, existingId)
                .path("deleteCar").entity(Boolean.class).isEqualTo(true);

        // try to delete already deleted car
        MutationUtils.deleteCar(graphQlTester, existingId)
                .errors()
                .expect(error -> {
                    assertThat(error.getMessage()).contains("Car not found with ID");
                    return true;
                });
    }

    @Test
    void testGetCarsQuery() throws InterruptedException {
        MutationUtils.addCar(graphQlTester, new AddCarInput("Toyota", "Camry", "Red", 2020, BigDecimal.valueOf(2000)));
        Thread.sleep(50);
        MutationUtils.addCar(graphQlTester, new AddCarInput("Audi", "A8", "Black", 2021, BigDecimal.valueOf(3000)));
        Thread.sleep(50);
        MutationUtils.addCar(graphQlTester, new AddCarInput("Toyota", "Camry", "Silver", 2022, BigDecimal.valueOf(4000)));

        Map<String, Object> filter = Map.of(
                "brand", "Toyota"
        );

        graphQlTester.document(GET_CARS_QUERY)
                .execute()
                .path("cars.totalElements").entity(Integer.class).isEqualTo(3)
                .path("cars.content[0].yearOfProduction").entity(Integer.class).isEqualTo(2022)
                .path("cars.content[1].yearOfProduction").entity(Integer.class).isEqualTo(2021)
                .path("cars.content[2].yearOfProduction").entity(Integer.class).isEqualTo(2020);

        // with filter
        graphQlTester.document(GET_CARS_QUERY)
                .variable("filter", filter)
                .execute()
                .path("cars.totalElements").entity(Integer.class).isEqualTo(2)
                .path("cars.content[0].yearOfProduction").entity(Integer.class).isEqualTo(2022)
                .path("cars.content[1].yearOfProduction").entity(Integer.class).isEqualTo(2020);
    }

    @Test
    void testGetCarByIdQuery() {
        AddCarInput car = new AddCarInput("Toyota", "Camry", "Red", 2020, BigDecimal.valueOf(1999.99));
        UUID existingId = MutationUtils.addCar(graphQlTester, car)
                .path("addCar.id")
                .entity(UUID.class).get();

        for (int i = 1; i <= 5; i++) {
            MutationUtils.addCar(graphQlTester, new AddCarInput("Toyota", "Camry", "Red", 2010 + i, BigDecimal.valueOf(1999.99)));
        }

        graphQlTester.document(GET_CAR_BY_ID_QUERY)
                .variable("id", existingId)
                .execute()
                .path("carById.id").entity(UUID.class).isEqualTo(existingId)
                .path("carById.brand").entity(String.class).isEqualTo(car.getBrand())
                .path("carById.model").entity(String.class).isEqualTo(car.getModel())
                .path("carById.color").entity(String.class).isEqualTo(car.getColor())
                .path("carById.yearOfProduction").entity(Integer.class).isEqualTo(car.getYearOfProduction())
                .path("carById.price").entity(BigDecimal.class).satisfies(price ->
                        assertThat(price.compareTo(car.getPrice())).isEqualTo(0)
                );
    }

    @Test
    void testGetCarsQueryPaging() throws InterruptedException {
        for (int i = 1; i <= 12; i++) {
            Thread.sleep(50);
            MutationUtils.addCar(graphQlTester, new AddCarInput("Toyota", "Camry", "Red", 2010 + i, BigDecimal.valueOf(1999.99)));
        }
        Map<String, Object> pageInput = Map.of(
                "page", 0,
                "size", 5
        );

        graphQlTester.document(GET_CARS_QUERY)
                .variable("pageInput", pageInput)
                .execute()
                .path("cars.totalElements").entity(Integer.class).isEqualTo(12)
                .path("cars.totalPages").entity(Integer.class).isEqualTo(3)
                .path("cars.size").entity(Integer.class).isEqualTo(5)
                .path("cars.number").entity(Integer.class).isEqualTo(0)
                .path("cars.content[0].yearOfProduction").entity(Integer.class).isEqualTo(2022)
                .path("cars.content[1].yearOfProduction").entity(Integer.class).isEqualTo(2021)
                .path("cars.content[2].yearOfProduction").entity(Integer.class).isEqualTo(2020)
                .path("cars.content[3].yearOfProduction").entity(Integer.class).isEqualTo(2019)
                .path("cars.content[4].yearOfProduction").entity(Integer.class).isEqualTo(2018);

        Map<String, Object> pageInput2 = Map.of(
                "page", 1,
                "size", 5
        );

        graphQlTester.document(GET_CARS_QUERY)
                .variable("pageInput", pageInput2)
                .execute()
                .path("cars.totalElements").entity(Integer.class).isEqualTo(12)
                .path("cars.totalPages").entity(Integer.class).isEqualTo(3)
                .path("cars.size").entity(Integer.class).isEqualTo(5)
                .path("cars.number").entity(Integer.class).isEqualTo(1)
                .path("cars.content[0].yearOfProduction").entity(Integer.class).isEqualTo(2017)
                .path("cars.content[1].yearOfProduction").entity(Integer.class).isEqualTo(2016)
                .path("cars.content[2].yearOfProduction").entity(Integer.class).isEqualTo(2015)
                .path("cars.content[3].yearOfProduction").entity(Integer.class).isEqualTo(2014)
                .path("cars.content[4].yearOfProduction").entity(Integer.class).isEqualTo(2013);

        Map<String, Object> pageInput3 = Map.of(
                "page", 2,
                "size", 5
        );

        graphQlTester.document(GET_CARS_QUERY)
                .variable("pageInput", pageInput3)
                .execute()
                .path("cars.totalElements").entity(Integer.class).isEqualTo(12)
                .path("cars.totalPages").entity(Integer.class).isEqualTo(3)
                .path("cars.size").entity(Integer.class).isEqualTo(5)
                .path("cars.number").entity(Integer.class).isEqualTo(2)
                .path("cars.content[0].yearOfProduction").entity(Integer.class).isEqualTo(2012)
                .path("cars.content[1].yearOfProduction").entity(Integer.class).isEqualTo(2011);

        Map<String, Object> pageInput4 = Map.of(
                "page", 3,
                "size", 5
        );

        graphQlTester.document(GET_CARS_QUERY)
                .variable("pageInput", pageInput4)
                .execute()
                .path("cars.totalElements").entity(Integer.class).isEqualTo(12)
                .path("cars.totalPages").entity(Integer.class).isEqualTo(3)
                .path("cars.size").entity(Integer.class).isEqualTo(5)
                .path("cars.number").entity(Integer.class).isEqualTo(3)
                .path("cars.content").entityList(Object.class).hasSize(0);
    }
}
