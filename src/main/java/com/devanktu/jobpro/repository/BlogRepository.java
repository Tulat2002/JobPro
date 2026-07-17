package com.devanktu.jobpro.repository;

import com.devanktu.jobpro.domain.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {

    List<Blog> findByUserId(Long userId);

//    List<Skill> findByIdIn(List<Long> id);

}
