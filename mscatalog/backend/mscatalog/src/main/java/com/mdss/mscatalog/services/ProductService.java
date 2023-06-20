package com.mdss.mscatalog.services;

import com.mdss.mscatalog.dto.ProductDTO;
import com.mdss.mscatalog.entities.Product;
import com.mdss.mscatalog.repositories.ProductRepository;
import com.mdss.mscatalog.services.execptions.DataException;
import com.mdss.mscatalog.services.execptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Page<ProductDTO> findAll(Pageable pageable){
        Page<Product> page = productRepository.findAll(pageable);
        return page.map(x -> new ProductDTO(x));

    }

    public ProductDTO findById(Long id){
        Optional<Product> obj = productRepository.findById(id);
        Product entity = obj.orElseThrow(()-> new ResourceNotFoundException("Not found! " + id));
        return new ProductDTO(entity);
    }

    public ProductDTO newProduct(ProductDTO dto){
        Product entity = new Product();
        entity.setName(dto.getName());
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try {
            Product entity = productRepository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = productRepository.save(entity);
            return new ProductDTO(entity);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Not at all");
        }
    }

    public void delete(Long id){
        if(!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Id does not exist: " + id);
        }
        try {
            productRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DataException("Resource has dependecy");
        }
    }



}
