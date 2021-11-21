package uz.java.maniac.thisthingdoesnotexistbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.java.maniac.thisthingdoesnotexistbot.model.TelegramUser;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
}