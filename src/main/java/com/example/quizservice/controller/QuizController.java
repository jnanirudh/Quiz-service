package com.example.quizservice.controller;

import com.example.quizservice.model.QuestionWrapper;
import com.example.quizservice.model.Response;
import com.example.quizservice.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    QuizService quizService;

    // Admin creates a quiz
    @PostMapping("create")
    public ResponseEntity<String> createQuiz(
            @RequestParam String subject,
            @RequestParam Integer numQ,
            @RequestParam String title) {
        return quizService.createQuiz(subject, numQ, title);
    }

    // Student loads quiz questions (starts proctoring if studentName provided)
    @GetMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(
            @PathVariable Integer id,
            @RequestParam(required = false) String studentName) {
        return quizService.getQuizQuestions(id, studentName);
    }

    // Student submits answers (stops proctoring, scores, saves report)
    @PostMapping("submit/{id}")
    public ResponseEntity<String> submitQuiz(
            @PathVariable Integer id,
            @RequestParam String studentName,
            @RequestBody List<Response> responses) {
        return quizService.submitQuiz(id, studentName, responses);
    }
}