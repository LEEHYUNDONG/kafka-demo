//package com.easttwave.pr.kafkademo.message.kafka;
//
//import com.easttwave.pr.kafkademo.message.kafka.dto.Message;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
//)
////@EmbeddedKafka(partitions = 1,
////        brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
////        ports = { 9092 }
////)
//public class POST_specs {
//
//    @Test
//    void 카프카_메시지_발행이_정상적으로_되는가(
//            @Autowired TestRestTemplate testRestTemplate
//    ) {
//        // arrange
//        var url = "/api/v1/message/publish";
//        // act
//        var message = new Message(1L, "kafka-demo-topic", "Hello, Kafka!");
//        ResponseEntity<Void> response = testRestTemplate.postForEntity(url, message, Void.class);
//
//        // assert
//        assertThat(response.getStatusCode().value()).isEqualTo(200);
//    }
//
//    @Test
//    void 카프카_메시지_발행후_컨슈머가_정상적으로_메시지를_수신하는가(
//            @Autowired TestRestTemplate testRestTemplate
//    ) throws InterruptedException {
//        // arrange
//        var url = "/api/v1/message/publish";
//        ExecutorService executorService = Executors.newFixedThreadPool(100);
//        CountDownLatch latch = new CountDownLatch(100);
//
//        // act
//        executorService.submit(() -> {
//            try {
//                for (int i = 0; i < 100; i++) {
//                    var message = new Message((long) latch.getCount(), "kafka-demo-topic", "Hello, Kafka! " + String.valueOf(i));
//                    ResponseEntity<Void> response = testRestTemplate.postForEntity(
//                            url,
//                            message,
//                            Void.class
//                    );
//                    // assert
//                    assertThat(response.getStatusCode().value()).isEqualTo(200);
//                }
//            } finally {
//                latch.countDown();
//            }
//        });
//        latch.await();
//        executorService.shutdown();
//    }
//
//
//}
