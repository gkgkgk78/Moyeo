package com.example.notification.repository;


import com.example.notification.entity.MessageBox;
import com.example.notification.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface MessageBoxRepository extends JpaRepository<MessageBox, Long> {





}
