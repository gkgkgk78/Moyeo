package com.moyeo.main.repository;

import com.moyeo.main.entity.MessageBox;
import com.moyeo.main.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface MessageBoxRepository extends JpaRepository<MessageBox, Long> {

    List<MessageBox> findByUserIdOrderByIsCheckedAscCreateTimeDesc(User userId);

    @Modifying
    @Transactional
    @Query("update MessageBox set isChecked = true where messageId = :messageId")
    void markAsCheckedById(Long messageId);

    @Modifying
    @Transactional
    @Query("UPDATE MessageBox m SET m.isChecked = true WHERE m.userId.userId = :userId")
    void markAsCheckedByUser(Long userId);

    void deleteByMessageId(Long messageId);

}
