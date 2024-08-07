package com.tracker.repository;

import com.tracker.domain.Message;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
    @Query("select message from Message message where message.toUserId.login = ?#{authentication.name}")
    List<Message> findByToUserIdIsCurrentUser();

    @Query("select message from Message message where message.fromUserId.login = ?#{authentication.name}")
    List<Message> findByFromUserIdIsCurrentUser();
}
