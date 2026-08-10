package com.cakedelight.catalog.service;

import com.cakedelight.catalog.entity.Cake;
import com.cakedelight.catalog.repository.CakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

import java.util.List;

@Service
public class CakeService {

    private final CakeRepository cakeRepository;

    public CakeService(CakeRepository cakeRepository) {
        this.cakeRepository = cakeRepository;
    }

    public List<Cake> getAllCakes() {
        return cakeRepository.findAll();
    }

    public Cake getCakeById(Long id) {
        return cakeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cake not found"));
    }

    public Cake saveCake(Cake cake) {
        return cakeRepository.save(cake);
    }

    public Cake updateCake(Long id, Cake updatedCake) {

        Cake cake = cakeRepository.findById(id).orElse(null);

        if (cake == null) {
            return null;
        }

        cake.setName(updatedCake.getName());
        cake.setDescription(updatedCake.getDescription());
        cake.setCategory(updatedCake.getCategory());
        cake.setPrice(updatedCake.getPrice());
        cake.setAvailable(updatedCake.getAvailable());
        cake.setImageUrl(updatedCake.getImageUrl());

        return cakeRepository.save(cake);
    }

    public List<Cake> filterCakes(
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        Specification<Cake> specification = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isBlank()) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.like(
                                    cb.lower(root.get("name")),
                                    "%" + name.toLowerCase() + "%"
                            )
            );
        }

        if (category != null && !category.isBlank()) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(
                                    cb.lower(root.get("category")),
                                    category.toLowerCase()
                            )
            );
        }

        if (minPrice != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get("price"),
                                    minPrice
                            )
            );
        }

        if (maxPrice != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.get("price"),
                                    maxPrice
                            )
            );
        }

        return cakeRepository.findAll(specification);
    }

    public void deleteCake(Long id) {
        cakeRepository.deleteById(id);
    }
}