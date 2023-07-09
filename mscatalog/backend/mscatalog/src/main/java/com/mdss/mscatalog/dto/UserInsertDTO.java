package com.mdss.mscatalog.dto;

import com.mdss.mscatalog.services.validation.UserInsertValid;

@UserInsertValid
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