package com.example.coffeeshop.service;

import com.example.coffeeshop.entity.Product;
import com.example.coffeeshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public void initializeProducts(){
        updateOrCreateProduct("Эспрессо", 120L, 15, 1,true);
        updateOrCreateProduct("Двойной Эспрессо", 180L, 0, 1,true);
        updateOrCreateProduct("Американо", 150L, 5, 1,true);
        updateOrCreateProduct("Капучино", 220L, 0, 1,true);
        updateOrCreateProduct("Латте Макиато", 240L, 10, 1,true);
        updateOrCreateProduct("Флэт Уайт", 260L, 0, 1,true);

// --- АВТОРСКИЙ КОФЕ И НАПИТКИ ---
        updateOrCreateProduct("Раф Ванильный", 280L, 0, 2,true);
        updateOrCreateProduct("Раф Цитрусовый", 300L, 15, 2,true);
        updateOrCreateProduct("Латте Халва", 290L, 0, 2,true);
        updateOrCreateProduct("Колд Брю", 200L, 5, 2,false); // Сезонный / Неактивен
        updateOrCreateProduct("Бамбл Кофе", 270L, 0, 2,false); // Сезонный / Неактивен
        updateOrCreateProduct("Матча Латте", 260L, 15, 2,true);

// --- ЧАЙНАЯ КАРТА ---
        updateOrCreateProduct("Чай Эрл Грей", 160L, 0, 3,true);
        updateOrCreateProduct("Чай Сенча", 160L, 0, 3,true);
        updateOrCreateProduct("Чай Облепиховый", 250L, 20, 3,true);
        updateOrCreateProduct("Чай Марокканский", 240L, 0, 3,true);

// --- ДЕСЕРТЫ ---
        updateOrCreateProduct("Чизкейк Нью-Йорк", 320L, 0, 4,true);
        updateOrCreateProduct("Тирамису", 350L, 10, 4,true);
        updateOrCreateProduct("Торт Красный Бархат", 380L, 15, 4,true);
        updateOrCreateProduct("Фисташковый Рулет", 420L, 0, 4,true);
        updateOrCreateProduct("Мусс Три Шоколада", 340L, 0, 4,false); // На стопе
        updateOrCreateProduct("Дубайский Шоколад", 450L, 20, 4,true); // Премиум со скидкой

// --- ВЫПЕЧКА ---
        updateOrCreateProduct("Круассан Классический", 140L, 0, 5,true);
        updateOrCreateProduct("Круассан с Миндалем", 190L, 5, 5,true);
        updateOrCreateProduct("Улитка с Изюмом", 110L, 0, 5,true);
    }

@Transactional
    public Product updateOrCreateProduct(
            String name,
            Long price,
            Integer discount,
            Integer categoryId,
            boolean active )
    {
        return repository.findByName(name).map(product -> {
            product.setPrice(price);
            product.setDiscount(discount);
            product.setCategoryId(categoryId);
            if(active) product.activate();
            else product.deactivate();
            return product;
        })
                .orElseGet(() -> {
                    Product newProduct = new Product(name, price,discount,categoryId);
                    if(active) newProduct.activate();
                    else newProduct.deactivate();
                    return repository.save(newProduct);
        });
    }
    public List<Product> getActiveProducts(){
        return repository.findByActiveTrue();
    }
    public Optional<Product> findProductById(Long id){
        return repository.findById(id);
    }
}
