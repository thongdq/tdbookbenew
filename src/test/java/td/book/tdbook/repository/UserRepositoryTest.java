package td.book.tdbook.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import td.book.tdbook.model.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
//Using application-test.properties file
//@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testCreateReadDelete() {
        User user = new User.UserBuilder().withUserName("username1")
                                          .withEmail("email1")
                                          .withPassword("password1")
                                          .build();

        userRepository.save(user);

        Iterable<User> users = userRepository.findAll();
        Assertions.assertThat(users).extracting(User::getUserName).containsOnly("username1");
    }

}
