package com.mdss.mscatalog.services;

import com.mdss.mscatalog.dto.CategoryDTO;
import com.mdss.mscatalog.dto.ProductDTO;
import com.mdss.mscatalog.entities.Category;
import com.mdss.mscatalog.entities.Product;
import com.mdss.mscatalog.projections.ProductProjection;
import com.mdss.mscatalog.repositories.CategoryRepository;
import com.mdss.mscatalog.repositories.ProductRepository;
import com.mdss.mscatalog.services.exceptions.DataException;
import com.mdss.mscatalog.services.exceptions.ResourceNotFoundException;
import com.mdss.mscatalog.util.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<ProductDTO> searchAll(Pageable pageable){
        Page<Product> page = productRepository.searchAll(pageable);
        return page.map(x -> new ProductDTO(x));
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(String name, String categoryId, Pageable pageable){
    //   String[] splitCategoryId = categoryId.split(",");
    //   List<String> list = Arrays.asList(splitCategoryId);
         List<Long> categoryIds = Arrays.asList();
         if(!"0".equals(categoryId))
              categoryIds = Arrays.asList(categoryId.split(",")).stream().map(x-> Long.parseLong(x)).toList();
         Page<ProductProjection> page = productRepository.searchProducts(categoryIds, name, pageable);
         List<Long> productIds = page.map(x-> x.getId()).toList();

         List<Product> entities = productRepository.searchProductsWithCategories(productIds);
         entities = (List<Product>) Utils.replace(page.getContent(), entities);

         List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();

         Page<ProductDTO> pageDto = new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
         return pageDto;
    }

    public ProductDTO findById(Long id){
        Optional<Product> obj = productRepository.findById(id);
        Product entity = obj.orElseThrow(()-> new ResourceNotFoundException("Not found! " + id));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        Product entity = new Product();
        entityToDto(entity, dto);
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try {
            Product entity = productRepository.getReferenceById(id);
            entityToDto(entity, dto);
            entity = productRepository.save(entity);
            return new ProductDTO(entity);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Not at all");
        }
    }

    @Transactional
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


    private void entityToDto(Product entity, ProductDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();
        for(CategoryDTO catDTO: dto.getCategories()){
            Category category = categoryRepository.getReferenceById(catDTO.getId());
            entity.getCategories().add(category);
        }
    }


}
