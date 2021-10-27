package td.book.tdbook.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import td.book.tdbook.model.Book;
import td.book.tdbook.model.Comment;
import td.book.tdbook.model.User;
import td.book.tdbook.payload.book.CommentDTO;
import td.book.tdbook.security.services.UserDetailsImpl;
import td.book.tdbook.service.BookService;
import td.book.tdbook.service.CommentService;
import td.book.tdbook.service.UserService;

@RestController
@CrossOrigin(origins = "${tdbook.fe.url}")
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<HttpStatus> createComment(@RequestParam String message, @RequestParam Long bookId) {

        UserDetailsImpl userLogin = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Comment comment = new Comment();
        comment.setMessage(message);

        if(bookId != null) {
            Optional<Book> book = bookService.findById(bookId);
            comment.setBook(book.get());
        }

        if(userLogin != null) {
            Optional<User> user = userService.findById(userLogin.getId());
            comment.setUser(user.get());
        }

        commentService.save(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/child")
    public ResponseEntity<HttpStatus> createChildComment(@RequestParam String message,
                                                         @RequestParam Long bookId,
                                                         @RequestParam Long commentId) {

        UserDetailsImpl userLogin = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Comment> pComment = commentService.findById(commentId);

        if(pComment.isPresent()) {
            Comment comment = new Comment();
            comment.setMessage(message);

            if(bookId != null) {
                Optional<Book> book = bookService.findById(bookId);
                comment.setBook(book.get());
            }

            if(userLogin != null) {
                Optional<User> user = userService.findById(userLogin.getId());
                comment.setUser(user.get());
            }

            comment.setParentComment(pComment.get());

            commentService.save(comment);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    // @GetMapping
    // public ResponseEntity<Map<String, Object>> getCommentOfBook(@RequestParam(required = true) Long bookId) {
    //     List<Comment> comments = new ArrayList<>();
    //     comments = commentService.findByBookId(bookId);

    //     Map<String, Object> response = new HashMap<>();
    //     response.put("comments", comments);

    //     return new ResponseEntity<>(response, HttpStatus.OK);
    // }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getCommentOfBook(@RequestParam(required = true) Long bookId) {
        List<Comment> comments = new ArrayList<>();
        comments = commentService.findByBookId(bookId);

        List<CommentDTO> commentDTOs = new ArrayList<>();

        if(!comments.isEmpty()) {
            for(int i = 0; i < comments.size(); i++) {

                Comment comment = comments.get(i);
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setId(comment.getId());
                commentDTO.setMessage(comment.getMessage());
                commentDTO.setCreated(comment.getCreated());

                Optional<User> oUser = userService.findById(comments.get(i).getUser().getId());
                if(oUser.isPresent()) {
                    User user = oUser.get();
                    commentDTO.setAvatar(user.getAvatar());
                    commentDTO.setUsername(user.getUserName());
                }

                if(!comment.getChildComments().isEmpty()) {
                    List<CommentDTO> childCommentDTO = new ArrayList<>();
                    for(int j = 0; j < comment.getChildComments().size(); j++) {
                        Comment c = comment.getChildComments().get(j);
                        CommentDTO cDTO = new CommentDTO();
                        cDTO.setId(c.getId());
                        cDTO.setMessage(c.getMessage());
                        cDTO.setCreated(c.getCreated());

                        Optional<User> oU = userService.findById(c.getUser().getId());
                        if(oU.isPresent()) {
                            User u = oU.get();
                            cDTO.setUsername(u.getUserName());
                            cDTO.setAvatar(u.getAvatar());
                        }

                        childCommentDTO.add(cDTO);
                    }

                    commentDTO.setChildComments(childCommentDTO);
                }

                commentDTOs.add(commentDTO);
            }
        }

        return new ResponseEntity<>(commentDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable long id) {
        Optional<Comment> oComment = commentService.findById(id);
        if(oComment.isPresent()) {
            Comment comment = oComment.get();
            List<Comment> comments = comment.getChildComments();
            if(!comments.isEmpty()) {
                for (Comment c : comments) {
                    commentService.deleteById(c.getId());
                }
            }
            commentService.deleteById(id);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // @GetMapping("/child")
    // public ResponseEntity<Map<String, Object>> getChildComment(@RequestParam(required = true) Long commentId) {
    //     List<Comment> comments = new ArrayList<>();
    //     comments = commentService.findByBookId(bookId);

    //     Map<String, Object> response = new HashMap<>();
    //     response.put("comments", comments);

    //     return new ResponseEntity<>(response, HttpStatus.OK);
    // }
    
}
