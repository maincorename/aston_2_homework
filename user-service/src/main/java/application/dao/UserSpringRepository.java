package application.dao;

import application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSpringRepository extends JpaRepository<User, Long> {
}
