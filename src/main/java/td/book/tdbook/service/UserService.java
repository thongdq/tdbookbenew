package td.book.tdbook.service;

import java.util.List;
import java.util.Optional;

import td.book.tdbook.model.Book;
import td.book.tdbook.model.User;

public interface UserService {

    List<User> findAll();
    Optional<User> findByuserName(String userName);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    void save(User user);
    Boolean existsByuserName(String userName);
    Boolean existsByEmail(String email);
    List<Book> getAllBooks(String userName);

}
