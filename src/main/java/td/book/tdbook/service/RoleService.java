package td.book.tdbook.service;

import java.util.List;
import java.util.Optional;

import td.book.tdbook.enumtd.ERole;
import td.book.tdbook.model.Role;

public interface RoleService {

    List<Role> findAll();
    Optional<Role> findByName(ERole name);

}
