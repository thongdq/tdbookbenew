package td.book.tdbook.service;

import java.util.List;
import java.util.Optional;

import td.book.tdbook.model.Comment;

public interface CommentService {
    Optional<Comment> findById(Long id);
    List<Comment> findByBookId(Long bookId);
    void deleteByBookId(Long bookId);
    void save(Comment comment);
    void deleteById(Long id);
}
