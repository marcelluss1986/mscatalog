package com.mdss.mscatalog.repositories;

import com.mdss.mscatalog.entities.Product;
import com.mdss.mscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
