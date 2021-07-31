package com.example.sse;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@EnableAsync
@Slf4j
public class SseController {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final int SIZE = 20;

    @GetMapping("/rbe")
    public ResponseBodyEmitter rbe() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        executorService.execute(() -> {
            try {
                for (int i = 0; i < SIZE; i++) {
                    String msg = "<p>" + i + "</p>";
                    emitter.send(msg);
                    sleep();
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @GetMapping("/sse")
    public ResponseBodyEmitter sse() {
        ResponseBodyEmitter emitter = new SseEmitter();
        executorService.execute(() -> {
            try {
                for (int i = 0; i < SIZE; i++) {
                    String msg = String.valueOf(i);
                    emitter.send(msg);
                    sleep();
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @GetMapping("/srb")
    public StreamingResponseBody srb() {
        return out -> {
            for (int i = 0; i < SIZE; i++) {
                String msg = "<p>" + i + "</p>";
                out.write(msg.getBytes());
                out.flush();
                sleep();
            }
        };
    }

    @SneakyThrows
    private void sleep() {
        Thread.sleep(500);
    }

}
