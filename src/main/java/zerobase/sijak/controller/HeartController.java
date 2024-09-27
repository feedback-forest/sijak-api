package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.service.HeartService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HeartController {

    private final HeartService heartService;

    @GetMapping("/api/hearts")
    public ResponseEntity<?> readHearts(@RequestHeader("Authorization") String token) {

        heartService.readHearts(token);

        return null;
    }


    @PostMapping("hearts")
    public ResponseEntity<?> appendHearts(@RequestHeader("Authorization") String token) {

        heartService.appendHearts(token);

        return null;
    }

}
