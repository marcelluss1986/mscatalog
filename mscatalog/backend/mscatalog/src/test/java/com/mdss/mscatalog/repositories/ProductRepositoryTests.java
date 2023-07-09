package com.mdss.mscatalog.repositories;

import com.mdss.mscatalog.entities.Product;
import com.mdss.mscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    private long existingId;
    private long nonExistingId;
    private int countProduct;


    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() throws Exception{
     existingId = 1L;
     nonExistingId = 1000L;
     countProduct = 25;


    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist(){
        Optional<Product> obj = repository.findById(nonExistingId);
        Assertions.assertNotNull(obj.isEmpty());
    }

    @Test
    public void findByIdShouldReturnOptionalNotEmptyWhenIdExists(){
        Optional<Product> product = repository.findById(existingId);
        Product entity = product.get();
        Assertions.assertEquals(existingId, entity.getId());
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull(){
        Product entity = Factory.productCreateNewPRoduct();
        entity.setId(null);
        entity = repository.save(entity);

        Assertions.assertNotNull(entity.getId());
        Assertions.assertEquals(countProduct + 1, entity.getId());

    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists(){
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }


}
