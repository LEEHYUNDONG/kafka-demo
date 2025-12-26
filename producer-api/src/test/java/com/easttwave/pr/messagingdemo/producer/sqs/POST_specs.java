package com.easttwave.pr.messagingdemo.producer.sqs;

import com.easttwave.pr.messagingdemo.platform.sqs.dto.SqsMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class POST_specs {

    @Test
    void 메시지를_sqs로_전송히면_200_Ok를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ) {
        //arrange
        var url = "/api/v1/sqs-message/send";
        var message = new SqsMessage(1L, "Hello, SQS!");
        //act
        ResponseEntity<Void> response = testRestTemplate.postForEntity(
                url,
                message,
                Void.class
        );
        //assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void 약_2000건의_메시지를_sqs로_전송히면_204_NoContent를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ) throws InterruptedException {
        var url = "/api/v1/sqs-message/send";
        int messageCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(messageCount);

        for (int i = 0; i < messageCount; i++) {
            final int idx = i;
            executorService.submit(() -> {
                var message = new SqsMessage((long) idx, "Hello, SQS! " + idx);
                ResponseEntity<Void> response = testRestTemplate.postForEntity(
                        url,
                        message,
                        Void.class
                );
                assertThat(response.getStatusCode().value()).isEqualTo(204);
                latch.countDown();
            });
        }

        // 모든 작업이 끝날 때까지 대기 (최대 30초)
        boolean completed = latch.await(30, java.util.concurrent.TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        executorService.shutdown();
    }


}
