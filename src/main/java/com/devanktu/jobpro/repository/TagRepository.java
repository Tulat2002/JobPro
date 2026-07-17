package com.devanktu.jobpro.repository;

import com.devanktu.jobpro.domain.Skill;
import com.devanktu.jobpro.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    Tag findByName(String name);
    Tag findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);

    List<Tag> findByIdIn(List<Long> id);

}
