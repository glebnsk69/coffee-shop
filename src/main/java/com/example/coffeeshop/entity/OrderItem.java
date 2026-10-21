package com.example.coffeeshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name="order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="order_id",nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;
    @Column(name = "item_name",nullable = false)
    private String  name;
    @Column(name="cost",nullable = false)
    private Long cost;
    @Column(name="discount",nullable = false)
    private Integer discount;
    @Column(name="quantity",nullable = false)
    private Integer quantity;

    public OrderItem() {
    }

    public OrderItem(Product product, Order order, Integer quantity) {
        if(product==null || order==null || quantity==null ||quantity<=0) throw new IllegalArgumentException("OrderItem: order,product and quantity must be not null. And quantity must be > 0");
        this.product = product;
        this.order = order;
        this.quantity = quantity;
        this.name = product.getName();
        this.cost = product.getPrice();
        this.discount=product.getDiscount();
    }

    public Long getId() {
        return id;
    }
    public Order getOrder() {
        return order;
    }
    public Product getProduct() {
        return product;
    }
    public String getName() {
        return name;
    }
    public Long getCost() {
        return cost;
    }
    public Integer getDiscount() {
        return discount;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public String getStringForPrint(){
        return String.valueOf(product.getId())+":"+getName()+" - "+getCost()+" " + getQuantity();
    }
}
