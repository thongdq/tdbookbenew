package td.book.tdbook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import td.book.tdbook.model.Role;
import td.book.tdbook.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping()
    public List<Role> findAll() {
        return roleService.findAll();
    }

}
