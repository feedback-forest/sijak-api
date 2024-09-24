package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sijak")
public class ScrapController {

    private final ScrapService scrapService;

    @GetMapping("/class/{id}")
    public ResponseEntity<Map<Object, Object>> readClass(@PathVariable Long id) {
        Map<Object, Object> classInfo = scrapService.readClass(id);

        return ResponseEntity.ok(classInfo);
    }

}
