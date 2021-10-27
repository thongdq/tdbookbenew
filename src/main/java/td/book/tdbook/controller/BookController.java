package td.book.tdbook.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import td.book.tdbook.exception.S3ImageException;
import td.book.tdbook.model.Book;
import td.book.tdbook.model.BookDetail;
import td.book.tdbook.model.BookImage;
import td.book.tdbook.model.BookImageId;
import td.book.tdbook.model.Comment;
import td.book.tdbook.model.Image;
import td.book.tdbook.model.User;
import td.book.tdbook.payload.book.BookDTO;
import td.book.tdbook.payload.book.BookDetailDTO;
import td.book.tdbook.security.services.UserDetailsImpl;
import td.book.tdbook.service.BookDetailService;
import td.book.tdbook.service.BookImageService;
import td.book.tdbook.service.BookService;
import td.book.tdbook.service.CommentService;
import td.book.tdbook.service.FileService;
import td.book.tdbook.service.UserService;
import td.book.tdbook.service.aws.AWSS3Service;
import td.book.tdbook.util.StringUtil;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "${tdbook.fe.url}")
@RequestMapping("/books")
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    @Autowired
    BookDetailService bookDetailService;

    @Autowired
    FileService fileService;

    @Autowired
    BookImageService bookImageService;

    @Autowired
    AWSS3Service awss3Service;

    @Autowired
    CommentService commentService;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private Sort.Direction getSortDirection(String direction) {
        if(direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if(direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(@RequestParam(required = false) String title,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "3") int size,
                                  @RequestParam(defaultValue = "id, asc") String[] sort) {
        // LOGGER.trace("This is TRACE");
        // LOGGER.debug("This is DEBUG");
        // LOGGER.info("This is INFO");
        // LOGGER.warn("This is WARN");
        // LOGGER.error("This is ERROR");

        try {

            List<Order> orders = new ArrayList<>();

            if(sort[0].contains(",")) {
                for(String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Book> books = new ArrayList<>();
            Pageable paging = PageRequest.of(page, size, Sort.by(orders));

            Page<Book> pageBooks;
            if(title == null) {
                pageBooks = bookService.findAll(paging);
            } else {
                pageBooks = bookService.findByTitleContaining(title, paging);
            }

            books = pageBooks.getContent();

            List<BookDTO> bookDTOs = books.stream().map(book -> {
                BookDTO bookDTO = new BookDTO();
                bookDTO.setId(book.getId());
                bookDTO.setAuthor(book.getAuthor());
                bookDTO.setTitle(book.getTitle());
                bookDTO.setImage(book.getImage());

                return bookDTO;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("books", bookDTOs);
            response.put("currentPage", pageBooks.getNumber());
            response.put("totalItems", pageBooks.getTotalElements());
            response.put("totalPages", pageBooks.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") long id) {
        Optional<Book> book = bookService.findById(id);

        if(book.isPresent()) {
            return new ResponseEntity<>(book.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<BookDetailDTO> getBookDetail(@PathVariable("id") long id) {
        Optional<Book> book = bookService.findById(id);

        if (book.isPresent()) {

            Image image = null;
            List<BookImage> bookImages = book.get().getBookImages();
            if(!bookImages.isEmpty()) {
                image = bookImages.get(0).getImage();
            }

            BookDetailDTO bookDetail = new BookDetailDTO();
            bookDetail.setId(book.get().getId());
            bookDetail.setTitle(book.get().getTitle());
            if(book.get().getBookDetail() != null) {
                bookDetail.setContent(book.get().getBookDetail().getContent());
            }
            bookDetail.setAuthor(book.get().getAuthor());
            if(image != null) {
                bookDetail.setImageUrl(image.getUrl());
                 bookDetail.setImageCaption(image.getCaption());
            }
            return new ResponseEntity<>(bookDetail, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/create", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Transactional
    public ResponseEntity<HttpStatus> createBookReview(@RequestPart("book") String bDto,
                                                        @RequestPart("file") MultipartFile file) throws IOException {

        BookDetailDTO bookDetailDTO = new BookDetailDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        bookDetailDTO = objectMapper.readValue(bDto, BookDetailDTO.class);

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        //UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) loggedInUser.getPrincipal();

        Optional<User> user = userService.findByuserName(userDetailsImpl.getUsername());

        //put image to aws s3
        int index = file.getOriginalFilename().lastIndexOf(".");
        String key = StringUtil.keepOnlyLetterAndNumber(file.getOriginalFilename().substring(0, index + 1))
                    + System.currentTimeMillis() + "."
                    + file.getOriginalFilename().substring(index + 1, file.getOriginalFilename().length());
        String imageUrl="";
        try {
            imageUrl = awss3Service.uploadFile(file, bucket, key);
        } catch(S3ImageException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Image image = new Image(fileName,
                                file.getContentType(),
                                bookDetailDTO.getImageCaption(),
                                imageUrl,
                                bucket,
                                key);

        fileService.save(image);

        Book book = new Book();
        BookDetail bDetail = new BookDetail();

        bDetail.setContent(bookDetailDTO.getContent());

        bookDetailService.save(bDetail);

        book.setTitle(bookDetailDTO.getTitle());
        book.setAuthor(bookDetailDTO.getAuthor());
        book.setBookDetail(bDetail);
        book.setImage(imageUrl);
        
        book.setUser(user.get());

        bookService.save(book);

        BookImage bookImage = new BookImage(new BookImageId(book.getId(), image.getId()),
                                            new Date(), book, image);

        bookImageService.save(bookImage);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<Book> updateBookReview(@RequestPart("book") String bDto,
                                                 @RequestPart(required = false) MultipartFile file) throws IOException {

        BookDetailDTO bookDetailDTO = new BookDetailDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        bookDetailDTO = objectMapper.readValue(bDto, BookDetailDTO.class);

        Optional<Book> book = bookService.findById(bookDetailDTO.getId());
        if(book.isPresent()) {
            Book uBook = book.get();
            uBook.setTitle(bookDetailDTO.getTitle());
            uBook.getBookDetail().setContent(bookDetailDTO.getContent());
            uBook.setAuthor(bookDetailDTO.getAuthor());

            List<BookImage> images = uBook.getBookImages();
            if(!images.isEmpty()) {
                BookImage bImage = images.get(0);
                Image image = bImage.getImage();

                if(image != null) {
                    if(file != null) {
                        int index = file.getOriginalFilename().lastIndexOf(".");
                        String imageKey = StringUtil.keepOnlyLetterAndNumber(file.getOriginalFilename().substring(0, index + 1))
                                            + System.currentTimeMillis() + "."
                                            + file.getOriginalFilename().substring(index + 1, file.getOriginalFilename().length());

                        if(!awss3Service.checkS3ObjectIsExist(image.getS3_bucket(), imageKey)) {
                            awss3Service.deleteObject(image.getS3_bucket(), image.getS3_key());
                            String imageUrl = awss3Service.uploadFile(file, image.getS3_bucket(), imageKey);
                            image.setUrl(imageUrl);
                            image.setS3_key(imageKey);
                            image.setName(StringUtils.cleanPath(file.getOriginalFilename()));

                            uBook.setImage(imageUrl);
                        }
                    }

                    image.setCaption(bookDetailDTO.getImageCaption());
                    fileService.save(image);
                }
            }

            return new ResponseEntity<Book>(bookService.save(uBook), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable long id) {
        Optional<Book> oBook = bookService.findById(id);
        if(oBook.isPresent()) {
            List<BookImage> bookImages = oBook.get().getBookImages();
            if(!bookImages.isEmpty()) {
                bookImageService.delete(bookImages.get(0));
                Image image = bookImages.get(0).getImage();
                if(image.getS3_bucket() != null) {
                    if(image.getS3_key() != null) {
                        awss3Service.deleteObject(image.getS3_bucket(), image.getS3_key());
                    }
                }
                fileService.deleteById(image.getId());
            }

            commentService.deleteByBookId(oBook.get().getId());

            bookService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // @GetMapping("/image/{id}")
    // public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
    //     Optional<Image> image = fileService.findById(id);
    //     if(image.isPresent()) {
    //         return ResponseEntity.ok()
    //                                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.get().getName() + "\"")
    //                                 .body(image.get().getImg());
    //     }

    //     return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    // }

}
