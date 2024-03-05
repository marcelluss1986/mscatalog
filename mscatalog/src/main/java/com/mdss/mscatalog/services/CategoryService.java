package com.mdss.mscatalog.services;

import com.mdss.mscatalog.dto.CategoryDTO;
import com.mdss.mscatalog.entities.Category;
import com.mdss.mscatalog.repositories.CategoryRepository;
import com.mdss.mscatalog.services.exceptions.DataException;
import com.mdss.mscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> findAll(){
        List<Category> list = categoryRepository.findAll();
        return list.stream().map(x -> new CategoryDTO(x)).toList();

    }

    public CategoryDTO findById(Long id){
        Optional<Category> obj = categoryRepository.findById(id);
        Category entity = obj.orElseThrow(()-> new ResourceNotFoundException("Not found! " + id));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO newCategory(CategoryDTO dto){
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
        try {
            Category entity = categoryRepository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = categoryRepository.save(entity);
            return new CategoryDTO(entity);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Not at all");
        }
    }

    public void delete(Long id){
        if(!categoryRepository.existsById(id)){
            throw new ResourceNotFoundException("Id does not exist: " + id);
        }
        try {
            categoryRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DataException("Resource has dependecy");
        }
    }



}
