package com.quizshare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "question_title", nullable = false, columnDefinition = "TEXT")
    private String questionTitle;

    @Column(name = "question_image", columnDefinition = "TEXT")
    private String questionImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_level", length = 10)
    @Builder.Default
    private QuestionLevel questionLevel = QuestionLevel.MEDIUM;

    @Column(name = "question_sort", nullable = false)
    private Integer questionSort;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum QuestionLevel {
        EASY, MEDIUM, HARD
    }
}
