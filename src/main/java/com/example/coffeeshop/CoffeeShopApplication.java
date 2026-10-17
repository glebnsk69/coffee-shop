package com.example.coffeeshop;

import com.example.coffeeshop.entity.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.coffeeshop.repository.ProductRepository;

import java.math.BigDecimal;


@SpringBootApplication
public class CoffeeShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeShopApplication.class, args);
    }

    private void updateProduct(ProductRepository repository,String name,Long price,Integer discount,boolean active){
        repository.findByName(name).ifPresentOrElse(product -> {
            product.setPrice(price);
            product.setDiscount(discount);
            if(active) product.activate();
            else product.deactivate();
            repository.save(product);
        },()->{
            Product newProduct = new Product(name, price,discount);
            if(active) newProduct.activate();
            else newProduct.deactivate();
            repository.save(newProduct);
        });
    }
    private void initializeProduct(ProductRepository repository){
        updateProduct(repository,"Cappuchino", 30000L,15,true);
        updateProduct(repository,"Late", 20000L,10,true);
        updateProduct(repository,"Burrito", 35000L,5,true);
        updateProduct(repository,"Biar", 50000L,0,false);
    }

    @Bean
    CommandLineRunner testProductRepository(ProductRepository repository){
        return args -> {
            initializeProduct(repository);
            //repository.save(new Product("Cappuchino", 30000L,15));
            System.out.println("Products count: " + repository.count());
            repository.findAll().forEach((product -> System.out.println(product.getStringForPrint())));

            repository.findById(1L).ifPresentOrElse(
                    product -> System.out.println("Found "+product.getName()),
                    () -> System.out.println("product not found"));

            repository.findById(4L).ifPresent(product -> {
                product.activate();
                product.setDiscount(100);
                repository.save(product);
            });
/*
            repository.findById(2L).ifPresent((product -> {
                product.deactivate();
                repository.save(product);
            }));

 */
            repository.findAll().stream().
                    filter(Product::getActive).
                    findFirst().
                    ifPresent(product -> {product.deactivate();repository.save(product);});
            repository.findAll().forEach((product -> System.out.println(product.getStringForPrint())));
            repository.findByActiveTrue().forEach(product -> System.out.println(product.getName()+" "+(product.getStringForPrint())));
            System.out.println("_".repeat(80));
            repository.findByDiscount(10).forEach(product -> System.out.println(product.getName()+" "+(product.getStringForPrint())));
            System.out.println("_".repeat(80));
            repository.findByActiveAndDiscount(false,15).forEach(product -> System.out.println(product.getName()+" "+(product.getStringForPrint())));
        };
    }
}