package td.book.tdbook.controller;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import td.book.tdbook.service.FileService;
import td.book.tdbook.service.UserService;
import td.book.tdbook.service.aws.AWSS3Service;
import td.book.tdbook.util.StringUtil;
import td.book.tdbook.model.Book;
import td.book.tdbook.model.Image;
import td.book.tdbook.model.User;
import td.book.tdbook.payload.LoginRequest;
import td.book.tdbook.payload.book.UserProfile;

@RestController
@CrossOrigin(origins = "${tdbook.fe.url}")
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    @Autowired
    UserService userService;

    @Autowired
    AWSS3Service awss3Service;

    @Autowired
    FileService fileService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @GetMapping()
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> findById(@PathVariable("id") long id) {

        UserProfile uProfile = new UserProfile();

        Optional<User> oUser = userService.findById(id);
        if(oUser.isPresent()) {
            User user = oUser.get();

            uProfile.setId(user.getId());
            uProfile.setUsername(user.getUserName());
            uProfile.setAvatar(user.getAvatar());
            uProfile.setEmail(user.getEmail());
        }

        return new ResponseEntity<>(uProfile, HttpStatus.OK);
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> getAllBooks(@RequestBody LoginRequest loginRequest) {
        List<Book> books = userService.getAllBooks(loginRequest.getUsername());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<HttpStatus> updateUserProfile(@RequestBody UserProfile userProfile) {
        Optional<User> oUser = userService.findById(userProfile.getId());

        if(oUser.isPresent()) {
            User user = oUser.get();
            user.setUserName(userProfile.getUsername());
            user.setEmail(userProfile.getEmail());
            user.setPassword(passwordEncoder.encode(userProfile.getPassword()));

            userService.save(user);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/avatar")
    @Transactional
    public ResponseEntity<UserProfile> updateUserAvatar(@RequestPart("user") String userProfile,
                                                        @RequestPart("file") MultipartFile file) {

        UserProfile uProfile = new UserProfile();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            uProfile = objectMapper.readValue(userProfile, UserProfile.class);
        } catch (JsonMappingException e) {
            LOGGER.error("Error: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<User> oUser = userService.findById(uProfile.getId());
        if(oUser.isPresent()) {
            User u = oUser.get();
            Image image = new Image();
            if(file != null) {
                int index = file.getOriginalFilename().lastIndexOf(".");
                String key = StringUtil.keepOnlyLetterAndNumber(file.getOriginalFilename().substring(0, index + 1))
                                + System.currentTimeMillis() + "."
                                + file.getOriginalFilename().substring(index + 1, file.getOriginalFilename().length());
                String avatarUrl = "";
                //If user just signup and has no avatar, add avatar to aws S3
                if(u.getAvatar() == null) {

                    avatarUrl = awss3Service.uploadFile(file, bucket, key);

                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    image = new Image(fileName,
                                        file.getContentType(),
                                        "",
                                        avatarUrl,
                                        bucket,
                                        key);

                    fileService.save(image);

                    u.setImage(image);
                    u.setAvatar(avatarUrl);
                    userService.save(u);

                    uProfile.setAvatar(avatarUrl);
                } else {
                    //If user already has avatar, update avatar to aws s3
                    Image img = u.getImage();

                    if(!awss3Service.checkS3ObjectIsExist(img.getS3_bucket(), key)) {
                        awss3Service.deleteObject(img.getS3_bucket(), img.getS3_key());
                        avatarUrl = awss3Service.uploadFile(file, img.getS3_bucket(), key);
                    }

                    img.setName(StringUtils.cleanPath(file.getOriginalFilename()));
                    img.setType(file.getContentType());
                    img.setS3_bucket(bucket);
                    img.setS3_key(key);
                    img.setUrl(avatarUrl);
                    fileService.save(image);

                    u.setAvatar(avatarUrl);
                    userService.save(u);

                    uProfile.setAvatar(avatarUrl);
                }
            }
        }

        return new ResponseEntity<>(uProfile, HttpStatus.OK);
    }

}
