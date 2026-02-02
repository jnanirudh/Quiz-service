package com.example.quizservice.service;

import com.example.quizservice.dao.QuizDao;
import com.example.quizservice.feign.QuizInterface;
import com.example.quizservice.model.QuestionWrapper;
import com.example.quizservice.model.Response;
import com.example.quizservice.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuizInterface quizInterface;

    public ResponseEntity<String> createQuiz(String subject, Integer numQ, String title) {
        // Call Feign to get IDs from Question Service (Now calling /questions/generate)
        ResponseEntity<List<Integer>> response = quizInterface.getQuestionsForQuiz(subject, numQ);
        List<Integer> questions = response.getBody();

        // Create and Save the Quiz object in quiz_db
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Quiz Created Successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        // Get the Quiz record from the local database
        Quiz quiz = quizDao.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        List<Integer> questionIds = quiz.getQuestionIds();

        // Ask Question Service for the Wrappers (DTOs) for these IDs
        return quizInterface.getQuestionsFromId(questionIds);
    }

    public ResponseEntity<Integer> calculateScore(Integer id, List<Response> responses) {
        return quizInterface.getScore(responses);
    }
}