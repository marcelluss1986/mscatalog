package com.mdss.mscatalog;

import com.mdss.mscatalog.repositories.ProductRepository;
import com.mdss.mscatalog.services.ProductService;
import com.mdss.mscatalog.services.execptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private int countTotalProducts;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists(){
        productService.delete(existingId);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            productService.delete(nonExistingId);
        });
    }

}
