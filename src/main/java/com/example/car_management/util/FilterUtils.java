package com.example.car_management.util;

import com.example.car_management.db.entity.Car;
import com.example.car_management.model.CarFilter;
import com.example.car_management.model.PageInput;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {

    public static PageRequest toPageable(PageInput pageInput, String sortBy) {
        int page = pageInput == null ? 0 : pageInput.getPage();
        int size = pageInput == null ? 10 : pageInput.getSize();
        return PageRequest.of(page, size, Sort.by(sortBy).descending());
    }

    public static Specification<Car> fromFilter(CarFilter filter) {
        if (filter == null) {
            return null;
        }
        return (Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getBrand() != null && !filter.getBrand().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("brand"), filter.getBrand()));
            }

            if (filter.getColor() != null && !filter.getColor().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("color"), filter.getColor()));
            }

            if (filter.getYearOfProduction() != null) {
                predicates.add(criteriaBuilder.equal(root.get("yearOfProduction"), filter.getYearOfProduction()));
            }

            if (filter.getPrice() != null) {
                predicates.add(criteriaBuilder.equal(root.get("price"), filter.getPrice()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
