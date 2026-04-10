package com.example.quizservice.feign;

import com.example.quizservice.model.ApiResponse;
import com.example.quizservice.model.QuestionWrapper;
import com.example.quizservice.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// Fixed: base URL changed from /questions → /question (matches QuestionController @RequestMapping)
@FeignClient(name = "QUESTION-SERVICE", url = "http://localhost:8081/question")
public interface QuizInterface {

    // Fixed: path was "generate" — now has leading "/" for clarity
    @GetMapping("/generate")
    ResponseEntity<ApiResponse<List<Integer>>> getQuestionsForQuiz(
            @RequestParam String subject, @RequestParam Integer numQ);

    // Fixed: path was "getQuestionsFromId" — actual endpoint is "/getQuestions"
    // Fixed: return type was bare List<QuestionWrapper> — now correctly wrapped in ApiResponse
    @PostMapping("/getQuestions")
    ResponseEntity<ApiResponse<List<QuestionWrapper>>> getQuestionsFromId(
            @RequestBody List<Integer> questionIds);

    // Fixed: return type was bare Integer — now correctly wrapped in ApiResponse
    @PostMapping("/getScore")
    ResponseEntity<ApiResponse<Integer>> getScore(@RequestBody List<Response> responses);
}