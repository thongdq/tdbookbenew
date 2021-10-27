package td.book.tdbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import td.book.tdbook.model.BookDetail;

@Repository
public interface BookDetailRepository extends JpaRepository<BookDetail, Long> {

}
