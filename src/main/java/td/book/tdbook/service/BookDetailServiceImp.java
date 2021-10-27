package td.book.tdbook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import td.book.tdbook.model.BookDetail;
import td.book.tdbook.repository.BookDetailRepository;

@Service
public class BookDetailServiceImp implements BookDetailService {

    @Autowired
    BookDetailRepository bookDetailRepository;

    @Override
    public void save(BookDetail bookDetail) {
        bookDetailRepository.save(bookDetail);
    }
    
}
