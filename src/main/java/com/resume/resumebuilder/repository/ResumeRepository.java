//package com.resume.resumebuilder.repository;
//
//import com.resume.resumebuilder.entity.Resume;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//@Repository
//public interface ResumeRepository extends JpaRepository<Resume, Long> {
//
//	Resume findTopByOrderByIdDesc();
//}

package com.resume.resumebuilder.repository;

import com.resume.resumebuilder.entity.Resume;
import com.resume.resumebuilder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUser(User user);
}