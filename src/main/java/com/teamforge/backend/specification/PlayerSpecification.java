package com.teamforge.backend.specification;

import com.teamforge.backend.dto.PlayerSearchRequest;
import com.teamforge.backend.model.Player;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

@UtilityClass
public class PlayerSpecification {

    public static Specification<Player> getSpec(PlayerSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if(!CollectionUtils.isEmpty(request.ranks())) {
                predicates.add(root.get("rank").in(request.ranks()));
            }

            if(!CollectionUtils.isEmpty(request.positions())) {
                predicates.add(root.join("positions").in(request.positions()));
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
