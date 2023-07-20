package com.mdss.mscatalog.resources;

import com.mdss.mscatalog.dto.UserDTO;
import com.mdss.mscatalog.dto.UserInsertDTO;
import com.mdss.mscatalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable){
        Page<UserDTO> page = userService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findByid(@PathVariable Long id){
        UserDTO dto = userService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserInsertDTO> insert(@RequestBody UserInsertDTO dto){
        UserDTO newDto = userService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserInsertDTO dto){
        UserDTO newDto = userService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
