package com.teamforge.backend.model;

import com.teamforge.backend.model.enums.DotaPosition;
import com.teamforge.backend.model.enums.DotaRank;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "dota_profiles", indexes = {
        @Index(name = "idx_dota_user_id", columnList = "user_id"),
        @Index(name = "idx_dota_rank", columnList = "rank")
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

    @Enumerated(EnumType.STRING)
    private DotaRank rank;

    private Integer stars;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dota_profile_positions", joinColumns = @JoinColumn(name = "profile_id"))
    @Enumerated(EnumType.STRING)
    private Set<DotaPosition> positions;

    private Integer mmr;

    @Builder.Default
    @Column(nullable = false)
    private boolean lookingForTeam = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
