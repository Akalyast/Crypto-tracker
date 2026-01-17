package com.CryptoProject.CryptoInfosys.repository;

import com.CryptoProject.CryptoInfosys.model.Trade;
import com.CryptoProject.CryptoInfosys.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByUser(User user);
    List<Trade> findByUserId(Long userId);
    List<Trade> findByUserIdOrderByExecutedAtAsc(Long userId);
    List<Trade> findByUserOrderByExecutedAtAsc(User user);
    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long findUserIdByEmail(@Param("email") String email);
    Optional<Trade> findByIdAndUser(Long id, User user);

}
