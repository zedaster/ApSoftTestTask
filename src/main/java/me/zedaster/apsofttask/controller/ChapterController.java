package me.zedaster.apsofttask.controller;

import me.zedaster.apsofttask.exception.ChapterParsingException;
import me.zedaster.apsofttask.model.Chapter;
import me.zedaster.apsofttask.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller to work with chapters in text
 */
@RestController
@RequestMapping("/chapters")
public class ChapterController {
    /**
     * Service to work with chapters in text
     */
    @Autowired
    private ChapterService chapterService;

    /**
     * Endpoint to parse chapters from a text
     */
    @PostMapping("/parse")
    public ResponseEntity<Chapter> parseChapters(@RequestParam("file") MultipartFile file) {
        if (file.getSize() > 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл должен быть меньше 1 мегабайта!");
        }

        try {
            return new ResponseEntity<>(chapterService.parseChapters(file), HttpStatus.OK);
        } catch (ChapterParsingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
