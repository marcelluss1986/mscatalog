package com.mdss.mscatalog.resources;

import com.mdss.mscatalog.dto.ProductDTO;
import com.mdss.mscatalog.projections.ProductProjection;
import com.mdss.mscatalog.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

    @Autowired
    private ProductService productService;

//    @GetMapping
//    public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable){
//        Page<ProductDTO> page = productService.searchAll(pageable);
//        return ResponseEntity.ok().body(page);
//    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "categoryId", defaultValue = "0") String categoryId,
            Pageable pageable){
        Page<ProductDTO> page = productService.findAllPaged( name, categoryId, pageable);
        return ResponseEntity.ok().body(page);
    }


    @PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findByid(@PathVariable Long id){
        ProductDTO dto = productService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto){
        dto = productService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(@Valid @PathVariable Long id, @RequestBody ProductDTO dto){
        dto = productService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
