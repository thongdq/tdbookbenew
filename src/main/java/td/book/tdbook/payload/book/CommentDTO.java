package td.book.tdbook.payload.book;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDTO {
    
    private Long id;
    private String username;
    private Date created;
    private String message;
    private String avatar;
    private List<CommentDTO> childComments;

}
