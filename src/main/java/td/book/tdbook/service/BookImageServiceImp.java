package td.book.tdbook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import td.book.tdbook.model.BookImage;
import td.book.tdbook.repository.BookImageRepository;

@Service
public class BookImageServiceImp implements BookImageService {

    @Autowired
    BookImageRepository bookImageRepository;

    @Override
    public void save(BookImage bookImage) {
        bookImageRepository.save(bookImage);
    }

    @Override
    public void delete(BookImage bookImage) {
        bookImageRepository.delete(bookImage);
    }

}
