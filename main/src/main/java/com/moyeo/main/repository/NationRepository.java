package com.moyeo.main.repository;

import com.moyeo.main.entity.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationRepository extends JpaRepository<Nation, Integer> {
    Nation findFirstByName(String address1);

}
