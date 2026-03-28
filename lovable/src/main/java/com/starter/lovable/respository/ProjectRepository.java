package com.starter.lovable.respository;

import com.starter.lovable.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // 1. Get all projects where the user is a member (Owner, Editor, or Viewer)
    @Query("""
            SELECT p FROM Project p
            JOIN ProjectMember pm ON pm.project.id = p.id
            WHERE pm.user.id = :userId
            AND p.deletedAt IS NULL
            ORDER BY p.updatedAt DESC
            """
    )
    List<Project> findAllAccessibleProjectsByUser(@Param("userId") Long userId);

    // 2. Get a specific project ONLY if the user is a member
    @Query("""
            SELECT p FROM Project p
            JOIN ProjectMember pm ON pm.project.id = p.id
            WHERE p.id = :projectId
            AND pm.user.id = :userId
            AND p.deletedAt IS NULL
            """
    )
    Optional<Project> findAllAccessibleProjectsById(@Param("projectId") Long projectId,
                                                    @Param("userId") Long userId);
}