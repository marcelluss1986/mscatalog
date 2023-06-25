package com.mdss.mscatalog.services;

import com.mdss.mscatalog.dto.ProductDTO;
import com.mdss.mscatalog.entities.Product;
import com.mdss.mscatalog.repositories.ProductRepository;
import com.mdss.mscatalog.services.execptions.DataException;
import com.mdss.mscatalog.services.execptions.ResourceNotFoundException;
import com.mdss.mscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long integrityId;
    private Product product;
    private ProductDTO dto;

    private PageImpl<Product> page;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 2L;
        integrityId = 3L;
        product = Factory.productCreateNewPRoduct();
        page = new PageImpl<>(List.of(product));
        dto = Factory.createProductDTO();

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById((nonExistingId))).thenReturn(Optional.empty());

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(integrityId);
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(integrityId)).thenReturn(true);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).getReferenceById(nonExistingId);
    }

    @Test
    public void updateShoulThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> {
            Product entity = repository.getReferenceById(nonExistingId);
            entity = repository.save(entity);
        });
    }


    @Test
    public void updateShouldReturnProductDTOWhenIdExistes(){
       Product entity = repository.getReferenceById(existingId);
       entity = repository.save(entity);

       Assertions.assertNotNull(entity.getId());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundWhenIdDoesNotExist(){
        Assertions.assertThrows(NoSuchElementException.class, ()-> {
           Optional<Product> obj = repository.findById(nonExistingId);
           Product entity = obj.get();
           service.findById(entity.getId());
           Mockito.verify(repository).findById(nonExistingId);
        });
    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExistis(){
        Optional<Product> obj = repository.findById(existingId);
        ProductDTO dto = new ProductDTO(obj.get());

        Assertions.assertNotNull(dto);
        Mockito.verify(repository).findById(existingId);
    }

    @Test
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAll(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdHadDependency(){
        Assertions.assertThrows(DataException.class,()-> {
            service.delete(integrityId);

        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists(){
       Assertions.assertDoesNotThrow(()-> {
           service.delete(existingId);
           Mockito.verify(repository).deleteById(existingId);
       });
      }

    @Test
    public void deleteByIdShouldThowsExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);

            Mockito.verify(repository).deleteById(nonExistingId);
        });
    }
}
