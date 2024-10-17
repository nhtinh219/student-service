package com.kienlong.api.studentservice.repo;

import com.kienlong.api.studentservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    @Query("SELECT rt FROM RefreshToken AS rt WHERE rt.user.username = ?1 ")
    List<RefreshToken> findByUsername(String username);

    @Query("DELETE FROM RefreshToken AS rt WHERE rt.expiryTime <= CURRENT_TIME")
    @Modifying
    int deleteByExpiryTime();
}
	