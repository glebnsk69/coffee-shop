package com.example.coffeeshop.entity;
/*
public enum OrderStatus {
    OPEN,
    INPROGRESS,
    COMPLETED,
    CANCELLED,
    READY,
    INVALID
}
*/
public enum OrderStatus {

    READY("Готов"),
    INPROGRESS("В процессе"),
    OPEN("Открыт"),
    COMPLETED("Завершён"),
    CANCELLED("Отменён"),
    INVALID("Удалён");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}