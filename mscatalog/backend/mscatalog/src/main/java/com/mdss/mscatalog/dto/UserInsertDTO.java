package com.mdss.mscatalog.dto;

import com.mdss.mscatalog.entities.User;

public class UserInsertDTO extends UserDTO{

    private String password;

    public UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
