package com.cakedelight.catalog.controller;

import com.cakedelight.catalog.entity.Cake;
import com.cakedelight.catalog.service.CakeService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

import java.util.List;

@RestController
@RequestMapping("/cakes")
public class CakeController {

    private final CakeService cakeService;

    public CakeController(CakeService cakeService) {
        this.cakeService = cakeService;
    }

    @GetMapping
    public List<Cake> getAllCakes() {
        return cakeService.getAllCakes();
    }

    @GetMapping("/filter")
    public List<Cake> filterCakes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        return cakeService.filterCakes(
                name,
                category,
                minPrice,
                maxPrice
        );
    }

    @GetMapping("/{id}")
    public Cake getCakeById(@PathVariable Long id) {
        return cakeService.getCakeById(id);
    }

    @PostMapping
    public Cake addCake(@RequestBody Cake cake) {
        return cakeService.saveCake(cake);
    }

    @PutMapping("/{id}")
    public Cake updateCake(@PathVariable Long id,
                           @RequestBody Cake cake) {
        return cakeService.updateCake(id, cake);
    }

    @DeleteMapping("/{id}")
    public void deleteCake(@PathVariable Long id) {
        cakeService.deleteCake(id);
    }
}