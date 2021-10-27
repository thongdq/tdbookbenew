package td.book.tdbook.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import td.book.tdbook.model.User;
import td.book.tdbook.repository.UserRepository;
import td.book.tdbook.service.UserServiceImp;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserServiceImp userService;

    @Mock
    UserRepository userRepository;

    // @Test
    // public void addUser() {
    //     User user1 = new User("user1", "email1", "password1");
    //     when(userRepository.save(user1)).the
    // }

    @Test
    public void testFindAllUsers() {
        List<User> users = new ArrayList<>();
        User user1 = new User.UserBuilder().withUserName("username1")
                                           .withEmail("email1")
                                           .build();
        User user2 = new User.UserBuilder().withUserName("username2")
                                           .withEmail("email2")
                                           .build();

        users.add(user1);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> emptyUsers = userService.findAll();

        assertEquals(2, emptyUsers.size());
        verify(userRepository, times(1)).findAll();
    }

}
