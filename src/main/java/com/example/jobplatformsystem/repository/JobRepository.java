package com.example.jobplatformsystem.repository;

import com.example.jobplatformsystem.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository
        extends JpaRepository<Job, Long> {
    List<Job> findByTitleContainingIgnoreCase(String keyword);

}