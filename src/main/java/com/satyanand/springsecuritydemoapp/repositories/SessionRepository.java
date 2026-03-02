package com.satyanand.springsecuritydemoapp.repositories;

import com.satyanand.springsecuritydemoapp.entities.Session;

import com.satyanand.springsecuritydemoapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    boolean existsByRefreshToken(String token);

    Optional<Session> findByAccessToken(String token);

    @Transactional
    @Modifying
    @Query("delete from Session s where s.user.id = :user_id")
    void deleteByUserId(@Param("user_id") Long user_id);

    List<Session> findByUserOrderByLastUsedAtAsc(User user);

    Optional<Session> findByRefreshToken( String refreshToken);
}
