package td.book.tdbook.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import td.book.tdbook.model.Book;

public interface BookService {
    List<Book> findAll();
    Page<Book> findAll(Pageable pageable);
    Page<Book> findByTitleContaining(String title, Pageable pageable);
    Optional<Book> findById(Long id);
    Book save(Book book);
    void delete(Long id);
}
