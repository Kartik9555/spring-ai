package com.learning.section8.controller;

import com.learning.section8.exception.InvalidAnswerException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SelfEvaluatingChatController {

    @Value("classpath:/promptTemplates/hrPolicy.st")
    Resource hrPolicyTemplate;

    private final ChatClient chatClient;
    private final FactCheckingEvaluator factCheckingEvaluator;

    public SelfEvaluatingChatController(ChatClient.Builder chatClientBuilder,
                                        @Value("classpath:/promptTemplates/factcheck.st") Resource factCheckTemplate
    ) throws IOException {
        this.chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.factCheckingEvaluator = FactCheckingEvaluator.builder(chatClientBuilder)
                .evaluationPrompt(factCheckTemplate.getContentAsString(Charset.defaultCharset()))
                .build();
    }

    @Retryable(retryFor = InvalidAnswerException.class, maxAttempts = 3)
    @GetMapping("/evaluate/chat")
    public String chat(@RequestParam("message") String message) {
        String answer = chatClient.prompt()
                .user(message)
                .call()
                .content();
        validateAnswer(message, List.of(), answer);
        return answer;
    }

    @GetMapping("/evaluate/prompt-stuffing")
    public String promptStuffing(@RequestParam("message") String message) throws IOException {
        String answer = chatClient.prompt()
                .system(hrPolicyTemplate)
                .user(message)
                .call()
                .content();
        List<Document> dataList = List.of(new Document(hrPolicyTemplate.getContentAsString(Charset.defaultCharset())));
        validateAnswer(message, dataList, answer);
        return answer;
    }

    private void validateAnswer(String question, List<Document> dataList, String answer) {
        EvaluationRequest request = new EvaluationRequest(question, dataList, answer);
        EvaluationResponse response = factCheckingEvaluator.evaluate(request);
        if(!response.isPass()){
            throw new InvalidAnswerException(question, answer);
        }
    }

    @Recover
    public String recover(InvalidAnswerException exception) {
        return "Sorry, I couldn't answer your question. Please try rephrasing it.";
    }
}
