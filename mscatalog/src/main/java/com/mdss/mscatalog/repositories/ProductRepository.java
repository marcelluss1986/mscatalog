package com.mdss.mscatalog.repositories;

import com.mdss.mscatalog.entities.Product;
import com.mdss.mscatalog.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT obj FROM Product obj JOIN FETCH obj.categories")
    List<Product> searchAll();

    @Query(value = "SELECT obj FROM Product obj JOIN FETCH obj.categories",
    countQuery = "SELECT COUNT(obj) FROM Product obj JOIN obj.categories")
    Page<Product> searchAll(Pageable pageable);

    @Query(
            nativeQuery = true, value = """
                      SELECT * FROM(
                      SELECT DISTINCT tb_product.id, tb_product.name FROM tb_product
                      INNER JOIN tb_product_category
                      ON tb_product.id = tb_product_category.product_id
                      WHERE (:categoryIds IS NULL OR tb_product_category.category_id
                      IN :categoryIds)
                      AND LOWER(tb_product.name) LIKE LOWER(CONCAT('%', :name, '%'))
                      ORDER BY tb_product.name)
                      AS tb_result
                      """, countQuery = """
            SELECT COUNT(*) From(
            SELECT DISTINCT tb_product.id, tb_product.name FROM tb_product
                      INNER JOIN tb_product_category
                      ON tb_product.id = tb_product_category.product_id
                      WHERE (:categoryIds IS NULL OR tb_product_category.category_id
                      IN :categoryIds)
                      AND LOWER(tb_product.name) LIKE LOWER(CONCAT('%', :name, '%'))
                      ORDER BY tb_product.name)
                      AS tb_result)""")
    Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);

    @Query(value = "SELECT obj FROM Product obj JOIN FETCH obj.categories " +
            "WHERE obj.id " +
            "IN :productsIds " +
            "ORDER BY obj.name ")
    List<Product> searchProductsWithCategories(List<Long> productsIds);
}
