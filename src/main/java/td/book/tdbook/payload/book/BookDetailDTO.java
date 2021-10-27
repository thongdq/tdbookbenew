package td.book.tdbook.payload.book;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookDetailDTO {

    private Long id;
    private String author;
    private String title;
    private String content;
    private String imageUrl;
    private String imageCaption;

}
