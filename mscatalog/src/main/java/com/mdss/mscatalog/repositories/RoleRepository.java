package com.mdss.mscatalog.repositories;

import com.mdss.mscatalog.entities.Product;
import com.mdss.mscatalog.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByAuthority(String authority);
}
