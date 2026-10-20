package com.example.coffeeshop.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status",nullable = false)
    private OrderStatus status;
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "duration")
    private Long durationSeconds;
    @Column(name = "deleted",nullable = false)
    private boolean deleted = false;
    public Long getId() {
        return id;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public Long getDurationSeconds() {
        return durationSeconds;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public Order() {
        status = OrderStatus.OPEN;
        createdAt = LocalDateTime.now();
        deleted = false;
    }
    public void completeOrder(){
        if(status==OrderStatus.INPROGRESS){
            status = OrderStatus.COMPLETED;
            durationSeconds = ChronoUnit.SECONDS.between(createdAt,LocalDateTime.now());
        }
        else throw new IllegalStateException("Cannot complete an inactive order");
    }
    public void cancelOrder(){
        if(status==OrderStatus.OPEN ){
            status = OrderStatus.CANCELLED;
            durationSeconds = ChronoUnit.SECONDS.between(createdAt,LocalDateTime.now());
        } else throw new IllegalStateException("Only an open order can be cancelled");
    }
    public void deleteOrder(){
        if(status!=OrderStatus.INPROGRESS && status!=OrderStatus.OPEN){
            deleted=true;
        }else throw new IllegalStateException("Cannot complete an active order");
    }
    public void startOrder(){
        if(status==OrderStatus.OPEN) {
            status=OrderStatus.INPROGRESS;
        }
        else throw new IllegalStateException("Only open orders can be accepted for processing");
    }
    public String StringForPrint(){
        return String.valueOf(id)+": "+status+", "+createdAt;
    }
}
