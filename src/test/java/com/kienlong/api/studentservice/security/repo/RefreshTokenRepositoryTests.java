package com.kienlong.api.studentservice.security.repo;

import com.kienlong.api.studentservice.entity.RefreshToken;
import com.kienlong.api.studentservice.repo.RefreshTokenRepository;
import jakarta.persistence.Query;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RefreshTokenRepositoryTests {

    @Autowired
    RefreshTokenRepository repository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void testFindRefreshTokenNotFound(){
        String username = "xxxxx";
        List<RefreshToken> result = repository.findByUsername(username);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void testFindRefreshTokenFound(){
        String username = "admin";
        List<RefreshToken> result = repository.findByUsername(username);

        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void testDeleteByExpiryTime() {
        String jpql = "SELECT COUNT(rt) FROM RefreshToken AS rt WHERE rt.expiryTime <= CURRENT_TIME";
        Query query = testEntityManager.getEntityManager().createQuery(jpql);

        Long numberOfExpiredRefreshTokens = (Long) query.getSingleResult();

        int effectedRow = repository.deleteByExpiryTime();

        org.junit.jupiter.api.Assertions.assertEquals(numberOfExpiredRefreshTokens, effectedRow);
    }
}
