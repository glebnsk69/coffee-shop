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

    public boolean isInvalid() {
        if(status==OrderStatus.INVALID) return true;
        else return false;
    }

    public void readyOrder() {
        if (status == OrderStatus.INPROGRESS) {
            status = OrderStatus.READY;
        } else {
            throw new IllegalStateException(
                    "Only an order in progress can be marked as ready");
        }
    }
    public void changeStatusByAdmin(OrderStatus newStatus) {
        if (newStatus == null || newStatus == OrderStatus.INVALID) {
            throw new IllegalArgumentException("Недопустимый статус");
        }

        status = newStatus;

        if (newStatus == OrderStatus.COMPLETED
                || newStatus == OrderStatus.CANCELLED) {
            durationSeconds = ChronoUnit.SECONDS.between(
                    createdAt,
                    LocalDateTime.now()
            );
        }
    }

    public void restoreOrder(OrderStatus newStatus) {
        if (status != OrderStatus.INVALID
                || newStatus == null
                || newStatus == OrderStatus.INVALID) {
            throw new IllegalStateException("Invalid order restoration");
        }

        status = newStatus;
    }

    public void markAsInvalid(){
        if(status!=OrderStatus.INVALID){
            status = OrderStatus.INVALID;
        }else throw new IllegalStateException("Order is already invalid");
    }
    public Order() {
        status = OrderStatus.OPEN;
        createdAt = LocalDateTime.now();
    }

    public void completeOrder(){
        if(status==OrderStatus.READY){
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
    public void startOrder(){
        if(status==OrderStatus.OPEN) {
            status=OrderStatus.INPROGRESS;
        }
        else throw new IllegalStateException("Only open orders can be accepted for processing");
    }
    public String StringForPrint(){
        return String.valueOf(id)+": "+status+", "+createdAt+((status==OrderStatus.INVALID)?"(удален)":"");
    }
}
