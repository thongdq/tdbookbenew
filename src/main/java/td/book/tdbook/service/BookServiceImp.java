package td.book.tdbook.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import td.book.tdbook.model.Book;
import td.book.tdbook.repository.BookRepository;

@Service
public class BookServiceImp implements BookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceImp.class);

    @Autowired
    BookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        LOGGER.trace("This is TRACE");
        LOGGER.debug("This is DEBUG");
        LOGGER.info("This is INFO");
        LOGGER.warn("This is WARN");
        LOGGER.error("This is ERROR");
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> findByTitleContaining(String title, Pageable pageable) {
        return bookRepository.findByTitleContaining(title, pageable);
    }
}
