package com.tracker.repository;

import com.tracker.domain.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Team entity.
 *
 * When extending this class, extend TeamRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface TeamRepository extends TeamRepositoryWithBagRelationships, JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    @Query("select team from Team team where team.teamLead.login = ?#{authentication.name}")
    List<Team> findByTeamLeadIsCurrentUser();

    default Optional<Team> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Team> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Team> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
