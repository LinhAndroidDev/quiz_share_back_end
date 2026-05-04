package com.quizshare.repository;

import com.quizshare.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestionIdOrderBySort(Long questionId);

    void deleteByQuestionId(Long questionId);
}
