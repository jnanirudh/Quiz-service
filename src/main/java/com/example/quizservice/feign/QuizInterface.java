package com.example.quizservice.feign;

import com.example.quizservice.model.QuestionWrapper;
import com.example.quizservice.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "QUESTION-SERVICE", url = "http://localhost:8081/questions")
public interface QuizInterface {

// Call question-service to get ID's
    @GetMapping("generate")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(
            @RequestParam String subject, @RequestParam Integer numQ);

    // Call Question Service to get Question details (as Wrappers/DTOs) for those IDs
    @PostMapping("getQuestionsFromId")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(@RequestBody List<Integer> questionIds);

    @PostMapping("getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<Response> responses);
}