package com.teamforge.backend.repository;

import com.teamforge.backend.model.DotaProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DotaProfileRepository extends JpaRepository<DotaProfile, Long>, JpaSpecificationExecutor<DotaProfile> {

    Optional<DotaProfile> findByUserId(Long userId);
}
