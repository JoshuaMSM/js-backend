package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    @Autowired
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product saveProduct(Product product) {
        return repo.save(product);
    }
}
