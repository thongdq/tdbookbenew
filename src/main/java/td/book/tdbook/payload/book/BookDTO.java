package td.book.tdbook.payload.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String image;

}
