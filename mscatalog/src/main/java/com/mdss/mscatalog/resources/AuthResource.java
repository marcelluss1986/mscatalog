package com.mdss.mscatalog.resources;

import com.mdss.mscatalog.dto.EmailDTO;
import com.mdss.mscatalog.dto.NewPasswordDTO;
import com.mdss.mscatalog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/oauth")
public class AuthResource {

	@Autowired
	private AuthService authService;

	@PostMapping(value = "/recover-token")
	public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDTO body) {
		authService.createRecoverToken(body);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(value = "/new-password")
	public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO newPassword) {
		authService.saveNewPassword(newPassword);
		return ResponseEntity.noContent().build();
	}
}
