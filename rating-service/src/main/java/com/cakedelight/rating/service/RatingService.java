package com.cakedelight.rating.service;

import com.cakedelight.rating.entity.Rating;
import com.cakedelight.rating.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RestClient restClient;
    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    public RatingService(RatingRepository ratingRepository,
                         RestClient restClient) {
        this.ratingRepository = ratingRepository;
        this.restClient = restClient;
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating getRatingById(Long id) {
        return ratingRepository.findById(id).orElse(null);
    }

    public Rating saveRating(Rating rating) {

        boolean cakeExists = checkCakeExists(rating.getCakeId());

        if (!cakeExists) {
            throw new RuntimeException(
                    "Cake with ID " + rating.getCakeId() + " does not exist"
            );
        }

        return ratingRepository.save(rating);
    }

    public Rating updateRating(Long id, Rating updatedRating) {

        Rating rating = ratingRepository.findById(id).orElse(null);

        if (rating == null) {
            return null;
        }

        boolean cakeExists = checkCakeExists(updatedRating.getCakeId());

        if (!cakeExists) {
            throw new RuntimeException(
                    "Cake with ID " + updatedRating.getCakeId() + " does not exist"
            );
        }

        rating.setCakeId(updatedRating.getCakeId());
        rating.setRating(updatedRating.getRating());
        rating.setReview(updatedRating.getReview());
        rating.setCustomerName(updatedRating.getCustomerName());

        return ratingRepository.save(rating);
    }
    public List<Rating> getRatingsByCakeId(Long cakeId) {
        return ratingRepository.findByCakeId(cakeId);
    }

    public double getAverageRating(Long cakeId) {

        List<Rating> ratings = ratingRepository.findByCakeId(cakeId);

        if (ratings.isEmpty()) {
            return 0.0;
        }

        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }

    private boolean checkCakeExists(Long cakeId) {

        try {
            restClient.get()
                    .uri(catalogServiceUrl + "/cakes/{id}", cakeId)
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}