package com.telegram.directory.query.model;

import jakarta.persistence.*;

@Entity
@Table(name = "professional")
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trade;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(length = 50)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(name = "experience_years")
    private int experienceYears;

    @Column(length = 500)
    private String description;

    private boolean verified;

    private double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Professional() {
    }

    public Professional(String trade, String name, String city) {
        this.trade = trade;
        this.name = name;
        this.city = city;
    }

    public Professional(String trade, String name, String city, String phone, String email,
                        int experienceYears, String description, boolean verified,
                        double rating, Category category) {
        this.trade = trade;
        this.name = name;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.experienceYears = experienceYears;
        this.description = description;
        this.verified = verified;
        this.rating = rating;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Professional{" +
                "id=" + id +
                ", trade='" + trade + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", experienceYears=" + experienceYears +
                ", description='" + description + '\'' +
                ", verified=" + verified +
                ", rating=" + rating +
                ", category=" + (category != null ? category.getId() : null) +
                '}';
    }
}

