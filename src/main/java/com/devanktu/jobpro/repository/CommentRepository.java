package com.devanktu.jobpro.repository;

import com.devanktu.jobpro.domain.Comment;
import com.devanktu.jobpro.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    Comment findCommentByBlogId(Long blogId);

    List<Comment> findByBlogId(Long blogId);
    List<Comment> findByParentId(Long parentId);

    Page<Comment> findByBlogIdAndParentIsNull(Long blogId, Pageable pageable);


}
