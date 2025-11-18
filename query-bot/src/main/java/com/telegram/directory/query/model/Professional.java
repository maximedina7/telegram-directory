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
    
    public Professional() {
    }
    
    public Professional(String trade, String name, String city) {
        this.trade = trade;
        this.name = name;
        this.city = city;
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
    
    @Override
    public String toString() {
        return "Professional{" +
                "id=" + id +
                ", trade='" + trade + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}

