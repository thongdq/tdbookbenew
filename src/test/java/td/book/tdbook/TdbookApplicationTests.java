package td.book.tdbook;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import td.book.tdbook.controller.UserController;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TdbookApplicationTests {

	@Autowired
	UserController userController;

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
	}

}
