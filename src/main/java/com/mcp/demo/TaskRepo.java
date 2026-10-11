package com.mcp.demo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findByDoneFalse();

    List<Task> findBySubjectIgnoreCaseAndDoneFalse(String subject);
    List<Task> findByTitleContainingIgnoreCase(String keyword);
}