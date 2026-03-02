package com.satyanand.springsecuritydemoapp.repositories;

import com.satyanand.springsecuritydemoapp.entities.UserAuthProvider;
import com.satyanand.springsecuritydemoapp.entities.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {

    Optional<UserAuthProvider> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    boolean existsByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}
