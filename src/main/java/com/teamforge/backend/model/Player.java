package com.teamforge.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String steamId;

    @Enumerated(EnumType.STRING)
    private Rank rank;

    @ElementCollection(targetClass = Position.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "player_positions", joinColumns = @JoinColumn(name = "player_id"))
    @Enumerated(EnumType.STRING)
    private Set<Position> positions;

    private String discordId;

    private LocalDateTime createdAt;

    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
