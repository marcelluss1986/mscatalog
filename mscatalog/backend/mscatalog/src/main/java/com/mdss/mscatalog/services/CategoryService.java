package com.mdss.mscatalog.services;

import com.mdss.mscatalog.dto.CategoryDTO;
import com.mdss.mscatalog.entities.Category;
import com.mdss.mscatalog.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
