package com.teamforge.backend.repository;

import com.teamforge.backend.model.DotaProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DotaProfileRepository extends JpaRepository<DotaProfile, Long>, JpaSpecificationExecutor<DotaProfile> {

    Optional<DotaProfile> findByUser_Nickname(String nickname);

    @Query("SELECT p FROM DotaProfile p WHERE p.user.steamId IS NOT NULL")
    List<DotaProfile> findAllWithSteamId();
}
