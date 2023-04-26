package com.danim.repository;

import com.danim.entity.RedisPage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeLineRedisRepository extends CrudRepository<RedisPage, Integer> {
}
