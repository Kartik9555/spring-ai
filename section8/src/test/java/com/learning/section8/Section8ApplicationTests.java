package com.learning.section8;

import com.learning.section8.controller.ChatController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "spring.ai.openai.api-key=${OPENAI_API_KEY:test-key}",
        "logging.level.org.springframework.ai=DEBUG"
})
public class Section8ApplicationTests {

    @Autowired
    private ChatController chatController;

    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;
    private RelevancyEvaluator relevancyEvaluator;

    @Value("${test.relevancy.min-score:0.7}")
    private float minRelevancyScore;

    @BeforeEach
    void setup() {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor());
        this.chatClient = chatClientBuilder.build();
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    }

    @Test
    @DisplayName("Should return relevant response for basic geography question")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    public void evaluateChatControllerResponseRelevancy() {
        // Given
        String question = "What is the capital of India?";

        // When
        String aiResponse = chatController.chat(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(question, aiResponse);
        EvaluationResponse response = relevancyEvaluator.evaluate(evaluationRequest);
        Assertions.assertAll(
                () -> assertThat(aiResponse).isNotBlank(),
                () -> assertThat(response.isPass())
                        .withFailMessage("""
                               =======================================
                               The answer was not considered relevant.
                               Question: "%s"
                               Response: "%s"
                               =======================================
                               """, question, aiResponse)
                        .isTrue(),
                () -> assertThat(response.getScore())
                        .withFailMessage("""
                               ==============================================================
                               The score %.2f is lower than the minimum required score %.2f".
                               Question: "%s"
                               Response: "%s"
                               ==============================================================
                               """, response.getScore(), minRelevancyScore, question, aiResponse)
                        .isGreaterThan(minRelevancyScore)
        );
    }
}
