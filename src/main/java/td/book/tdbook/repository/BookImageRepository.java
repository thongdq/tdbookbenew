package td.book.tdbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import td.book.tdbook.model.BookImage;
import td.book.tdbook.model.BookImageId;

@Repository
public interface BookImageRepository extends JpaRepository<BookImage, BookImageId> {
}
