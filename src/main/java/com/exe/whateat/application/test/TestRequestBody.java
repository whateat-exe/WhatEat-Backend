package com.exe.whateat.application.test;

import com.exe.whateat.application.common.AbstractController;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestRequestBody {

    public record TestRequestBodyStringUntrimmedRequest(String value) {

    }

    @Profile("dev")
    @RestController
    public static final class Controller extends AbstractController {

        @PostMapping("/test/untrimmed-string-body")
        public ResponseEntity<String> untrimmedStringTest(@RequestBody TestRequestBodyStringUntrimmedRequest request) {
            return ResponseEntity.ok(request.value);
        }
    }
}
