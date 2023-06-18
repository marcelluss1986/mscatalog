package com.mdss.mscatalog.services;

import com.mdss.mscatalog.dto.CategoryDTO;
import com.mdss.mscatalog.entities.Category;
import com.mdss.mscatalog.repositories.CategoryRepository;
import com.mdss.mscatalog.services.execptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public Page<CategoryDTO> findAll(Pageable pageable){
        Page<Category> page = categoryRepository.findAll(pageable);
        return page.map(x -> new CategoryDTO(x));

    }

    public CategoryDTO findById(Long id){
        Optional<Category> obj = categoryRepository.findById(id);
        Category entity = obj.orElseThrow(()-> new ResourceNotFoundException("Not found! " + id));
        return new CategoryDTO(entity);
    }

    public CategoryDTO newCategory(CategoryDTO dto){
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    public CategoryDTO update(Long id, CategoryDTO dto){
        Category obj = categoryRepository.getReferenceById(id);
        Category entity = obj;
    }



}
