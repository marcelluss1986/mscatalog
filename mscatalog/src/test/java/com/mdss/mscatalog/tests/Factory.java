package com.mdss.mscatalog.tests;

import com.mdss.mscatalog.dto.ProductDTO;
import com.mdss.mscatalog.entities.Category;
import com.mdss.mscatalog.entities.Product;

public class Factory {

    public static Product productCreateNewPRoduct(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setDescription("Good phone");
        product.setPrice(5000.00);
        product.setImgUrl("https://marcelo.com");
        product.getCategories().add(new Category(1L, "My product"));
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product product = Factory.productCreateNewPRoduct();
        ProductDTO dto = new ProductDTO(product, product.getCategories());
        return dto;
    }
}
