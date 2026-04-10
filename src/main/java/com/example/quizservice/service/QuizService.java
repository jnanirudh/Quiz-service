package com.example.quizservice.service;

import com.example.quizservice.dao.QuizDao;
import com.example.quizservice.feign.QuizInterface;
import com.example.quizservice.feign.ReportInterface;
import com.example.quizservice.model.ApiResponse;
import com.example.quizservice.model.QuestionWrapper;
import com.example.quizservice.model.Response;
import com.example.quizservice.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuizInterface quizInterface;

    @Autowired
    ReportInterface reportInterface;

    @Autowired
    RestTemplate restTemplate;

    // --- Proctoring helpers (calls FastAPI on localhost:8000) ---

    private void startProctoring(String studentName, Integer quizId) {
        try {
            String url = "http://localhost:8000/start-proctoring?studentName=" + studentName + "&quizId=" + quizId;
            restTemplate.postForEntity(url, null, String.class);
        } catch (Exception e) {
            // Proctor service may not be running — log and continue
            System.out.println("Proctoring service not available: " + e.getMessage());
        }
    }

    private void stopProctoring() {
        try {
            String url = "http://localhost:8000/stop-proctoring";
            restTemplate.postForEntity(url, null, String.class);
        } catch (Exception e) {
            System.out.println("Proctoring service not available: " + e.getMessage());
        }
    }

    // --- Quiz lifecycle ---

    public ResponseEntity<String> createQuiz(String subject, Integer numQ, String title) {
        ResponseEntity<ApiResponse<List<Integer>>> response = quizInterface.getQuestionsForQuiz(subject, numQ);

        if (response.getBody() == null) {
            return new ResponseEntity<>("Failed to reach Question Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<Integer> questions = response.getBody().getData();

        if (questions == null || questions.isEmpty()) {
            return new ResponseEntity<>("No questions found for subject: " + subject, HttpStatus.BAD_REQUEST);
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);

        Quiz savedQuiz = quizDao.save(quiz);
        return new ResponseEntity<>("Quiz Created Successfully. Quiz ID: " + savedQuiz.getId(), HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer quizId, String studentName) {
        Quiz quiz = quizDao.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));

        List<Integer> questionIds = quiz.getQuestionIds();

        ResponseEntity<ApiResponse<List<QuestionWrapper>>> response =
                quizInterface.getQuestionsFromId(questionIds);

        if (response.getBody() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Start proctoring when student loads quiz questions
        if (studentName != null && !studentName.isBlank()) {
            startProctoring(studentName, quizId);
        }

        return ResponseEntity.ok(response.getBody().getData());
    }

    public ResponseEntity<String> submitQuiz(Integer quizId, String studentName, List<Response> responses) {
        // 1. Stop proctoring
        stopProctoring();

        // 2. Call Question-service to calculate score
        ResponseEntity<ApiResponse<Integer>> scoreResponse = quizInterface.getScore(responses);

        if (scoreResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to calculate score — Question Service unreachable");
        }

        Integer score = scoreResponse.getBody().getData();

        // 3. Call Report-service to save the result
        try {
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("studentName", studentName);
            reportData.put("quizId", quizId);
            reportData.put("score", score);
            reportInterface.saveReport(reportData);
        } catch (Exception e) {
            System.out.println("Report service error: " + e.getMessage());
        }

        return ResponseEntity.ok("Quiz submitted! Score: " + score);
    }
}