package com.mdss.mscatalog.dto;

import com.mdss.mscatalog.services.validation.UserInsertValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserInsertValid
public class UserInsertDTO extends UserDTO{

    @NotBlank(message = "Required field.")
    @Size(min = 8, message = "Password should be minimum 8 characters.")
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