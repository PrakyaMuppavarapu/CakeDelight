package com.cakedelight.catalog.service;

import com.cakedelight.catalog.entity.Cake;
import com.cakedelight.catalog.repository.CakeRepository;
import org.springframework.stereotype.Service;

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

    public void deleteCake(Long id) {
        cakeRepository.deleteById(id);
    }
}