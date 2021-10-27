package td.book.tdbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import td.book.tdbook.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByuserName(String userName);
    Boolean existsByuserName(String userName);
    Boolean existsByEmail(String email);

}
