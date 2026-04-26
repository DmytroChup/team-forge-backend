package com.teamforge.backend.specification;

import com.teamforge.backend.dto.dota.DotaProfileSearchRequest;
import com.teamforge.backend.model.DotaProfile;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

@UtilityClass
public class PlayerSpecification {

    public static Specification<DotaProfile> getSpec(DotaProfileSearchRequest request) {
        return Specification.where(hasRanks(request))
                .and(hasPositions(request))
                .and(hasMinWinRate(request))
                .and(hasMinMatches(request))
                .and(requiresSteam(request))
                .and(isLookingForTeam(request));
    }

    private static Specification<DotaProfile> hasRanks(DotaProfileSearchRequest request) {
        return (root, query, cb) -> {
            boolean hasRanks = !CollectionUtils.isEmpty(request.rankTiers());

            if (!hasRanks && !request.includeUnranked()) {
                return cb.conjunction();
            }

            var rankPredicates = new ArrayList<Predicate>();
            if (hasRanks) {
                rankPredicates.add(root.get("rankTier").in(request.rankTiers()));
            }
            if (request.includeUnranked()) {
                rankPredicates.add(root.get("rankTier").isNull());
            }

            return cb.or(rankPredicates.toArray(Predicate[]::new));
        };
    }

    private static Specification<DotaProfile> hasPositions(DotaProfileSearchRequest request) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(request.positions())) {
                return cb.conjunction();
            }

            if(query.getResultType() != Long.class) {
                query.distinct(true);
            }

            return root.join("positions", JoinType.LEFT).in(request.positions());
        };
    }

    private static Specification<DotaProfile> hasMinWinRate(DotaProfileSearchRequest request) {
        return (root, query, cb) -> request.minWinRate() == null ? cb.conjunction() :
                cb.greaterThanOrEqualTo(root.get("winRate"), request.minWinRate());
    }

    private static Specification<DotaProfile> hasMinMatches(DotaProfileSearchRequest request) {
        return (root, query, cb) -> request.minMatches() == null ? cb.conjunction() :
                cb.greaterThanOrEqualTo(root.get("totalMatches"), request.minMatches());
    }

    private static Specification<DotaProfile> requiresSteam(DotaProfileSearchRequest request) {
        return (root, query, cb) -> Boolean.TRUE.equals(request.requireSteam()) ?
                cb.isNotNull(root.join("user", JoinType.LEFT).get("steamId")) : cb.conjunction();
    }

    private static Specification<DotaProfile> isLookingForTeam(DotaProfileSearchRequest request) {
        return (root, query, cb) -> Boolean.TRUE.equals(request.lookingForTeam()) ?
                cb.isTrue(root.get("lookingForTeam")) : cb.conjunction();
    }
}