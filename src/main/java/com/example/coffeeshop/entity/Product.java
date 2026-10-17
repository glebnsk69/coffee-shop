package com.example.coffeeshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.math.BigDecimal;


@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@Column(name = "name",nullable = false,unique = true)
    private String name;
@Column(name = "price", nullable = false)
    private Long price;
@Column(name = "discount",nullable = false)
    private Integer discount;
@Column(name = "active",nullable = false)
    private Boolean active;

    public Long getId() {
        return id;
    }

    public Integer getDiscount() {
        return discount;
    }

    public Boolean getActive() {
        return active;
    }

    public void deactivate(){
        active=false;
    }
    public void activate(){
        active=true;
    }

    public String getStringForPrint(){
        return String.valueOf(id)+": "+name+" - "+
                BigDecimal.valueOf(price).movePointLeft(2)+ "( "+
                discount+"% "+
                " - "+
                BigDecimal.valueOf(getPriceWithDiscount()).movePointLeft(2)+")"+
                (active?" active":" deactive");
    }
    protected Product() {
    }

    public Product(String name, Long price, Integer discount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Product price must be non-negative");
        }
        if (discount == null || discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Product discount must be between 0 and 100");
        }

        this.name = name;
        this.price = price;
        this.discount = discount;
        active=true;

    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public Long getPriceWithDiscount(){
        return (price * (100 - discount) + 50) / 100;
    }

    public void setPrice(Long price) {
        if(price==null || price<0){
                throw new IllegalArgumentException("Price must be non-negative ");        }
        this.price = price;

    }

    public void setDiscount(Integer discount) {
        if(discount==null || discount<0 || discount>100) {
            throw new IllegalArgumentException("Discount mast be from 0 to 100");
        }
        this.discount = discount;
    }
}