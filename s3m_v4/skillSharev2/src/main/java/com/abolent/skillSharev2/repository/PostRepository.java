package com.abolent.skillSharev2.repository;


import com.abolent.skillSharev2.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
