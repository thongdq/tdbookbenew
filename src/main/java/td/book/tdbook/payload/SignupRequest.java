package td.book.tdbook.payload;

import java.util.List;

import lombok.Data;

@Data
public class SignupRequest {

    private String username;
    private String email;
    private List<String> role;
    private String password;

}
