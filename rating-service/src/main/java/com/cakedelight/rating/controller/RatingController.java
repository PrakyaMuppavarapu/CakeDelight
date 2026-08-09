package com.cakedelight.rating.controller;

import com.cakedelight.rating.entity.Rating;
import com.cakedelight.rating.service.RatingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable Long id) {
        return ratingService.getRatingById(id);
    }

    @PostMapping
    public Rating createRating(@RequestBody Rating rating) {
        return ratingService.saveRating(rating);
    }

    @PutMapping("/{id}")
    public Rating updateRating(@PathVariable Long id,
                               @RequestBody Rating rating) {
        return ratingService.updateRating(id, rating);
    }

    @GetMapping("/cake/{cakeId}")
    public List<Rating> getRatingsByCakeId(@PathVariable Long cakeId) {
        return ratingService.getRatingsByCakeId(cakeId);
    }

    @GetMapping("/cake/{cakeId}/average")
    public double getAverageRating(@PathVariable Long cakeId) {
        return ratingService.getAverageRating(cakeId);
    }

    @DeleteMapping("/{id}")
    public void deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
    }
}