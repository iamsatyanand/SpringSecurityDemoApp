package com.satyanand.springsecuritydemoapp.repositories;


import com.satyanand.springsecuritydemoapp.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
