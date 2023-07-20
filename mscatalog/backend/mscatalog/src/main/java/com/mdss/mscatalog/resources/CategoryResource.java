package com.mdss.mscatalog.resources;

import com.mdss.mscatalog.dto.CategoryDTO;
import com.mdss.mscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> findAll(Pageable pageable){
        Page<CategoryDTO> page = categoryService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findByid(@PathVariable Long id){
        CategoryDTO dto = categoryService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDTO> newCategory(@RequestBody CategoryDTO dto){
        dto = categoryService.newCategory(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO dto){
        dto = categoryService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
