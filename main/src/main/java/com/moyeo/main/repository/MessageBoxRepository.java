package com.moyeo.main.repository;

import com.moyeo.main.entity.MessageBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageBoxRepository extends JpaRepository<MessageBox, Long> {

    List<MessageBox> findByUserIdOrderByIsCheckedAscCreateTimeDesc(Long userId);

    @Modifying
    @Query("UPDATE MessageBox m SET m.isChecked = true WHERE m.messageId = :messageId")
    void markAsCheckedById(Long messageId);

    @Modifying
    @Query("UPDATE MessageBox m SET m.isChecked = true WHERE m.userId = :userId")
    void markAsCheckedByUser(Long userId);

    void deleteByMessageId(Long messageId);

}
