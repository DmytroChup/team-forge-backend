package com.teamforge.backend.model;

import com.teamforge.backend.model.enums.DotaPosition;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "dota_profiles", indexes = {
        @Index(name = "idx_dota_user_id", columnList = "user_id"),
        @Index(name = "idx_dota_rank_tier", columnList = "rank_tier")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DotaProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    /**
     * Raw rank_tier from OpenDota API.
     * First digit: rank (1=Herald, 2=Guardian, ..., 8=Immortal).
     * Second digit: stars (1–5, 0 for Immortal).
     * Example: 54 = Divine 4, 80 = Immortal.
     */
    @Column(name = "rank_tier")
    private Integer rankTier;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dota_profile_positions", joinColumns = @JoinColumn(name = "profile_id"))
    @Enumerated(EnumType.STRING)
    private Set<DotaPosition> positions;

    @Column(name = "estimated_mmr")
    private Integer estimatedMmr;

    @Column(precision = 5, scale = 2) // e.g., 55.25%
    private BigDecimal winRate;

    @Column(name = "total_matches")
    private Integer totalMatches;

    @Column(length = 1000)
    private String aboutMe;

    @Builder.Default
    @Column(nullable = false)
    private boolean lookingForTeam = false;

    @Column(name = "last_stats_refreshed_at")
    private LocalDateTime lastStatsRefreshedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void assignToUser(User user) {
        this.user = user;
        if (user != null) {
            user.setDotaProfile(this);
        }
    }
}
