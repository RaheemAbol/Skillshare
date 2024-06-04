package com.abolent.skillSharev2.repository;


import com.abolent.skillSharev2.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
