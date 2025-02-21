package com.example.car_management.util;

import com.example.car_management.model.AddCarInput;
import com.example.car_management.model.UpdateCarInput;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MutationUtils {
    public static final String ADD_CAR_MUTATION = """
        mutation addCarOperation($input: AddCarInput!) {
          addCar(input: $input) {
            id
            brand
            model
            color
            yearOfProduction
            price
          }
        }
    """;

    public static final String UPDATE_CAR_MUTATION = """
        mutation updateCarOperation($input: UpdateCarInput!) {
          updateCarDetails(input: $input) {
            id
            brand
            model
            color
            yearOfProduction
            price
          }
        }
    """;

    public static final String DELETE_CAR_MUTATION = """
        mutation deleteCarOperation($id: ID!) {
          deleteCar(id: $id)
        }
    """;

    public static GraphQlTester.Response addCar(GraphQlTester tester, AddCarInput input) {
        Map<String, Object> addCarInput = Map.of(
                "brand", input.getBrand(),
                "model", input.getModel(),
                "color", input.getColor(),
                "yearOfProduction", input.getYearOfProduction(),
                "price", input.getPrice()
        );


        return tester.document(ADD_CAR_MUTATION)
                .variable("input", addCarInput)
                .execute();
    }

    public static GraphQlTester.Response updateCar(GraphQlTester tester, UpdateCarInput input) {
        Map<String, Object> updateCarInput = new HashMap<>();

        updateCarInput.put("id", input.getId());
        updateCarInput.put("brand", input.getBrand());
        updateCarInput.put("model", input.getModel());
        updateCarInput.put("color", input.getColor());
        updateCarInput.put("yearOfProduction", input.getYearOfProduction());
        updateCarInput.put("price", input.getPrice());


        return tester.document(UPDATE_CAR_MUTATION)
                .variable("input", updateCarInput)
                .execute();
    }

    public static GraphQlTester.Response deleteCar(GraphQlTester tester, UUID id) {
        return tester.document(DELETE_CAR_MUTATION)
                .variable("id", id)
                .execute();
    }

}
