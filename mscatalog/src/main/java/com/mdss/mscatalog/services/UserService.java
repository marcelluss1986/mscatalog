package com.mdss.mscatalog.services;

import com.mdss.mscatalog.dto.RoleDTO;
import com.mdss.mscatalog.dto.UserDTO;
import com.mdss.mscatalog.dto.UserInsertDTO;
import com.mdss.mscatalog.entities.Role;
import com.mdss.mscatalog.entities.User;
import com.mdss.mscatalog.projections.UserDetailsProjection;
import com.mdss.mscatalog.repositories.RoleRepository;
import com.mdss.mscatalog.repositories.UserRepository;
import com.mdss.mscatalog.services.exceptions.DataException;
import com.mdss.mscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthService authService;

    public Page<UserDTO> findAll(Pageable pageable){
        Page<User> page = userRepository.findAll(pageable);
        return page.map(x -> new UserDTO(x));

    }

    public UserDTO findUserClient(){
        User entity = authService.authenticated();
        return new UserDTO(entity);
    }

    public UserDTO findById(Long id){
        Optional<User> obj = userRepository.findById(id);
        User entity = obj.orElseThrow(()-> new ResourceNotFoundException("Not found! " + id));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto){
        User entity = new User();
        copyDtoToEntity(entity, dto);
        entity.getRoles().clear();
        Role role = roleRepository.findByAuthority("ROLE_OPERATOR");
        entity.getRoles().add(role);
        entity.setPassword(passwordEncoder.encode( dto.getPassword()));
        entity = userRepository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserInsertDTO dto){
        try {
            User entity = userRepository.getReferenceById(id);
            copyDtoToEntity(entity, dto);
            entity = userRepository.save(entity);
            return new UserDTO(entity);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Not at all");
        }
    }

    public void delete(Long id){
        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("Id does not exist: " + id);
        }
        try {
            userRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DataException("Resource has dependecy");
        }
    }

    private void copyDtoToEntity(User entity, UserDTO dto) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();
        for(RoleDTO roleDTO: dto.getRoles()){
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            entity.getRoles().add(role);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if(result.size() == 0){
            throw new UsernameNotFoundException("email not found");
        }

        User user = new User();
        user.setEmail(result.get(0).getUsername());
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result){
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }
}
