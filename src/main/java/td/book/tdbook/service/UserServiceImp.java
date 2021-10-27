package td.book.tdbook.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import td.book.tdbook.model.Book;
import td.book.tdbook.model.User;
import td.book.tdbook.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByuserName(String userName) {
        return userRepository.findByuserName(userName);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Boolean existsByuserName(String userName) {
        return userRepository.existsByuserName(userName);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<Book> getAllBooks(String userName) {
        List<Book> books = new ArrayList<>();
        Optional<User> user = userRepository.findByuserName(userName);
        if(user.isPresent()) {
            books = user.get().getBooks();
        }
        return books;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

}
