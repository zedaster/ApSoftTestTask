package me.zedaster.apsofttask.service;

import me.zedaster.apsofttask.exception.ChapterParsingException;
import me.zedaster.apsofttask.model.Chapter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Service to work with chapters in text
 */
@Service
public class ChapterService {
    /**
     * Parses chapter from {@link InputStreamSource}
     *
     * @param source object that can open a new {@link java.io.InputStream}
     * @return chapter including all its children
     */
    public Chapter parseChapters(InputStreamSource source) throws ChapterParsingException {
        Chapter rootChapter = new Chapter();
        Chapter workingChapter = rootChapter;
        List<String> contentLines = new ArrayList<>();
        boolean isFirstLine = true;

        try (BufferedReader reader = toUtf8BufferReader(source)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int startHashes = getStartHashes(line);
                if (startHashes == 0) {
                    if (isFirstLine) {
                        rootChapter.setTitle(line);
                        isFirstLine = false;
                        continue;
                    }
                    contentLines.add(line);
                    continue;
                }

                isFirstLine = false;

                if (!contentLines.isEmpty()) {
                    workingChapter.setContent(String.join("\n", contentLines));
                    contentLines.clear();
                }

                Chapter parent = rootChapter;
                for (int i = 0; i < startHashes - 1; i++) {
                    parent = parent.createOrGetLastChild();
                }
                String title = line.substring(startHashes);
                workingChapter = new Chapter();
                workingChapter.setTitle(title);
                parent.addChild(workingChapter);
            }

            if (!contentLines.isEmpty()) {
                workingChapter.setContent(String.join("\n", contentLines));
            }

            return rootChapter;
        } catch (IOException e) {
            if (e instanceof MalformedInputException) {
                throw new ChapterParsingException("Некорректный файл! Загрузите текстовый файл в кодировке UTF-8!");
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Wraps specified InputStreamSource to BufferedReader with UTF-8 encoding.
     * When {@link BufferedReader#read()}, {@link BufferedReader#readLine()} etc. is called and encoding is not UTF-8,
     * {@link MalformedInputException} will be thrown
     * @param source InputStreamSource
     * @return BufferedReader
     * @throws IOException delegated from {@link InputStreamSource#getInputStream()}
     */
    private BufferedReader toUtf8BufferReader(InputStreamSource source) throws IOException {
        final CharsetDecoder decoder = StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT);
        return new BufferedReader(new InputStreamReader(source.getInputStream(), decoder));
    }

    /**
     * Counts number of hashes (#) at beginning of string
     * @param str The string
     * @return number of hashes
     */
    private int getStartHashes(String str) {
        int result = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '#') {
                result++;
            } else {
                break;
            }
        }
        return result;
    }
}
