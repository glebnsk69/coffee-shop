package com.example.coffeeshop;
import com.example.coffeeshop.dto.CreateOrderRequest;
import com.example.coffeeshop.dto.OrderItemRequest;
import com.example.coffeeshop.entity.Order;
import com.example.coffeeshop.entity.OrderItem;
import com.example.coffeeshop.repository.OrderItemRepository;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import com.example.coffeeshop.service.OrderService;

@Component
public class ConsoleMenu {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public ConsoleMenu(OrderService orderService) {
        this.orderService = orderService;
    }
    public void showMesssage(@Nonnull String message){
        int w = message.length();
        System.out.println(ANSI_BLUE+"_".repeat(w+4));
        System.out.println("| "+message+" |");
        System.out.println("-".repeat(w+4)+ANSI_RESET);
    }
    enum CommandList {
        EXIT("Выход"),
        CREATEORDER("Создать заказ"),
        SHOWORDERSLIST("Показать список заказов"),
        SHOWORDER("Показать заказ"),
        START("Начать обработку заказа"),
        END("Завершить заказ"),
        CANCEL("Отменить заказ"),
        DELETE("Удалить заказ");


        private final String message;

        CommandList(String message){
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
    private final Scanner scanner = new Scanner(System.in);
    private final OrderService orderService;

    public void showMenu(){
        showMesssage("Выберите действие:");
        for(CommandList command:CommandList.values()){
            System.out.printf("%d: %s%n",command.ordinal(),command.getMessage());
        }
    }
    public CommandList readCommand() {
        CommandList[] commands = CommandList.values();
        while (true){
            System.out.printf("\nВведите номер команды (0-"+(commands.length-1) +"): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if(choice>=0 && choice<commands.length)
                    return commands[choice];
                else System.out.println("Такой команды нет");
            } catch (NumberFormatException e) {
                System.out.println("Введите номер команды числом.");
            }

        }
    }
    public void run() {
        boolean isRunning = true;
        while (isRunning) {
            showMenu();
            switch (readCommand()) {
                case START -> changeOrderStatus((long)readOrderId(),CommandList.START);
                case CANCEL -> changeOrderStatus((long)readOrderId(),CommandList.CANCEL);
                case CREATEORDER -> createOrder();
                case DELETE -> changeOrderStatus((long)readOrderId(),CommandList.DELETE);
                case SHOWORDER -> showOrder((long)readOrderId());
                case SHOWORDERSLIST -> showOrderList();
                case END -> changeOrderStatus((long)readOrderId(),CommandList.END);
                case EXIT -> isRunning=false;
            }
        }
    }
    private int readProductId() {
        int productId = 0;
        System.out.printf(" Введите ID товара (0-Завершить) ");
        while (true) {
            try {
                productId = Integer.parseInt(scanner.nextLine().trim());
                if(productId>=0) return productId;
                else {
                    System.out.println("ID товара должен быть неотрицательным.");

                }
            } catch (NumberFormatException e) {
                System.out.println("Введите ID числом.");

            }
        }

    }
    private int readQuantity(){
        int productQuantity = 0;

        System.out.printf(" Введите количество товара ");
        while (true) {
            try {
                productQuantity = Integer.parseInt(scanner.nextLine().trim());
                if(productQuantity>0) return productQuantity;
                else {
                    System.out.println("Количество товара должен быть положительным.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите Количество.");
            }
        }
    }
    private int readOrderId(){
        int orderId = 0;
        System.out.printf(" Введите ID заказа (0-Завершить) ");
        while (true) {
            try {
                orderId = Integer.parseInt(scanner.nextLine().trim());
                if(orderId>=0) return orderId;
                else {
                    System.out.println("ID заказа должен быть неотрицательным.");

                }
            } catch (NumberFormatException e) {
                System.out.println("Введите ID числом.");
            }
        }
    }
    public void createOrder(){
        List<OrderItemRequest> list = new ArrayList<>();
        showMesssage("Создание Заказа (0 - завершить)");
        int productId;
        int quantity;
        boolean isContinue = true;
        while (isContinue){
            productId = readProductId();
            if(productId==0) isContinue=false;
            else {
                quantity = readQuantity();
                list.add(new OrderItemRequest((long) productId,quantity));
            }
        }
        System.out.println(" Количество позиций: " + list.size());
        if(list.size()==0) {
            System.out.println("Заказ не создан. ");
            return;
        }
        CreateOrderRequest newRequest = new CreateOrderRequest(list);
        try {
            Order order = orderService.createOrder(newRequest);
            showMesssage("Заказ "+order.getId()+" Status:"+order.getStatus()+" Позиций в заказе:"+list.size());
        }catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }
    private void showOrderList(){
        List<Order> orders = orderService.getAllOrders();
        if(orders.isEmpty()) System.out.println("Заказов нет");
        else orders.forEach(order -> System.out.println("OrderID = "+order.getId()+" Order status = "+order.getStatus()+" Created "+order.getCreatedAt()));
    }
    private void showOrder(Long orderID){
        Optional<Order> order = orderService.findOrderById(orderID);
        if(order.isEmpty()) {
            System.out.println("Заказ не найден");
            return;
        }
        if(order.get().isDeleted()) System.out.println("Заказ Удален.");
        else showMesssage(order.get().StringForPrint());

        orderService.getOrderItems(orderID).forEach(orderItem -> System.out.println(orderItem.getStringForPrint()));
    }

    private void changeOrderStatus(Long orderID,CommandList status){
        Optional<Order> order = orderService.findOrderById(orderID);
        if(order.isEmpty()) {
            System.out.println("Заказ не найден");
            return;
        }

        if(order.get().isDeleted()) System.out.println("Заказ Удален.");
        else {
            showMesssage(order.get().StringForPrint());
            try {
                switch (status){
                    case END -> orderService.completeOrder(orderID);
                    case DELETE -> orderService.deleteOrder(orderID);
                    case CANCEL -> orderService.cancelOrder(orderID);
                    case START -> orderService.startOrder(orderID);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Что то пошло не так",e);
            }

            showMesssage(order.get().StringForPrint());
        }

    }
}
