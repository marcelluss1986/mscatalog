package com.mdss.mscatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdss.mscatalog.dto.ProductDTO;
import com.mdss.mscatalog.entities.Product;
import com.mdss.mscatalog.repositories.ProductRepository;
import com.mdss.mscatalog.services.ProductService;
import com.mdss.mscatalog.services.exceptions.DataException;
import com.mdss.mscatalog.services.exceptions.ResourceNotFoundException;
import com.mdss.mscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)

public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService service;

    @MockBean
    private ProductRepository repository;

    private ProductDTO productDTO;
    private Product product;

    private PageImpl<ProductDTO> page;

    private long existingId;
    private long nonExistingId;
    private long integrityId;

    @BeforeEach
    void setUp() throws Exception {
        product = Factory.productCreateNewPRoduct();
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
        existingId = 1L;
        nonExistingId = 2l;

        Mockito.when(service.insert((ArgumentMatchers.any()))).thenReturn(productDTO);

        Mockito.doNothing().when(service).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        Mockito.doThrow(DataException.class).when(service).delete(integrityId);

        Mockito.when(service.update(eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
        Mockito.when(service.update(eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

        Mockito.when(service.findById(existingId)).thenReturn(productDTO);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(service.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
    }

    @Test
    public void deleteShouldReeturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/products/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleleShouldReturnNoContentWhenIdExists() throws Exception {
      ResultActions resultActions =  mockMvc.perform(delete("/products/{id}", existingId));
        resultActions.andExpect(status().isNoContent());
    }


    @Test
    public void insertShouldReturnCreatedWhenidExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
       ResultActions result = mockMvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

               result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());

    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
                .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{id}",  nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findAllShouldReturnPageable() throws Exception {
       ResultActions result = mockMvc.perform(get("/products")
               .accept(MediaType.APPLICATION_JSON));

       result.andExpect(status().isOk());
    }

}
