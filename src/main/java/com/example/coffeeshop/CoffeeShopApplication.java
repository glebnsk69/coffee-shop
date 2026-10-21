package com.example.coffeeshop;

import com.example.coffeeshop.service.ProductService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.coffeeshop.repository.ProductRepository;


@SpringBootApplication
public class CoffeeShopApplication {

    public static void main(String[] args) {

        SpringApplication.run(CoffeeShopApplication.class, args);
    }
    @Bean
//    CommandLineRunner testProductRepository(ProductRepository repository, ProductService productService){
    CommandLineRunner testProductRepository( ProductService productService,ConsoleMenu menu){
        return args -> {
//            productService.initializeProducts();
            menu.run();
/*            System.out.println("Products count: " + repository.count());
            repository.findAll().forEach((product -> System.out.println(product.getStringForPrint())));

            repository.findById(1L).ifPresentOrElse(
                    product -> System.out.println("Found "+product.getName()),
                    () -> System.out.println("product not found"));

            productService.updateOrCreateProduct("Пиво Безалкагольное",50000L,0,6,true);

            repository.findById(4L).ifPresent(product -> {
                product.activate();
                product.setDiscount(100);
                repository.save(product);
            });

            System.out.printf("%s %s %s %n","_".repeat(30),"findByActiveTrue","_".repeat(30));
            repository.findByActiveTrue().forEach(product -> System.out.println(product.getName()+" "+(product.getStringForPrint())));

            System.out.printf("%s %s %s %n","_".repeat(30),"findByDiscount","_".repeat(30));
            repository.findByDiscount(10).forEach(product -> System.out.println(product.getName()+" "+(product.getStringForPrint())));

            System.out.printf("%s %s %s %n","_".repeat(30),"findByActiveAndDiscount","_".repeat(30));
            repository.findByActiveAndDiscount(true,15).forEach(product -> System.out.println(product.getName()+" "+(product.getStringForPrint())));

            System.out.printf("%s %s %s %n","_".repeat(30),"findByCategoryId","_".repeat(30));
            repository.findByCategoryId(4).forEach(product -> System.out.println(product.getName() )
            );

            System.out.printf("%s %s %s %n","_".repeat(30),"findByCategoryIdAndActiveTrue","_".repeat(30));
            repository.findByCategoryIdAndActiveTrue(4).forEach(product -> System.out.printf("%s - %d%n",product.getName(),product.getPrice()));

            System.out.printf("%s %s %s %n","_".repeat(30),"findByCategoryIdAndActiveTrueAndDiscountGreaterThen","_".repeat(30));
            repository.findByCategoryIdAndActiveTrueAndDiscountGreaterThanEqual(4,10).
                    forEach(product -> System.out.println(product.getStringForPrint()));

            System.out.println("=".repeat(80));
            repository.findByCategoryId(6).forEach(product -> System.out.println(product.getName()+" - "+product.getPrice()));

            repository.findByActiveFalse().forEach(product -> System.out.println(product.getStringForPrint()));
*/

        };


    }
}