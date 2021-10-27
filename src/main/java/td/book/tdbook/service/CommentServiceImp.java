package td.book.tdbook.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import td.book.tdbook.model.Comment;
import td.book.tdbook.repository.CommentRepository;

@Service
public class CommentServiceImp implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    public void deleteByBookId(Long bookId) {
        commentRepository.deleteByBookId(bookId);
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

}
