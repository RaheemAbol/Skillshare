package com.abolent.skillSharev2.repository;


import com.abolent.skillSharev2.model.Buddy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuddyRepository extends JpaRepository<Buddy, Long> {
    Optional<Buddy> findByEmail(String email);

    List<Buddy> findBySkillsId(Long skillId);

}