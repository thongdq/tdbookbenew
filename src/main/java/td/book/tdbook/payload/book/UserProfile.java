package td.book.tdbook.payload.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserProfile {

    private Long Id;
    private String username;
    private String password;
    private String avatar;
    private String email;

}
