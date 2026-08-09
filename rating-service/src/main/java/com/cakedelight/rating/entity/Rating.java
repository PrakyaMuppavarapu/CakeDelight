package com.cakedelight.rating.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ratings")
@Data
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cakeId;

    private Integer rating;

    private String review;

    private String customerName;
}