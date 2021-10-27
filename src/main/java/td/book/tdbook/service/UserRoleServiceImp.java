package td.book.tdbook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import td.book.tdbook.model.UserRole;
import td.book.tdbook.repository.UserRoleRepository;

@Service
public class UserRoleServiceImp implements UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Override
    public void save(UserRole userRole) {
        userRoleRepository.save(userRole);
    }
    
}
