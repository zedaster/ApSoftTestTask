package me.zedaster.apsofttask.controller;

import me.zedaster.apsofttask.model.Chapter;
import me.zedaster.apsofttask.exception.ChapterParsingException;
import me.zedaster.apsofttask.service.ChapterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Tests for {@link ChapterService}
 */
@SpringBootTest
public class ChapterServiceTest {
    /**
     * Testing service
     */
    private final ChapterService chapterService;

    /**
     * Wire the service by Spring
     */
    @Autowired
    public ChapterServiceTest(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    /**
     * Tests parsing of text in file "normal_text.txt"
     */
    @Test
    public void handleNormalText() throws ChapterParsingException {
        InputStreamSource isSource = getResourceAsInputStreamSource("normal_text.txt");
        Chapter rootChapter = chapterService.parseChapters(isSource);

        Assertions.assertEquals("GREATEST MAN IN ALIVE", rootChapter.getTitle());
        Assertions.assertEquals("This is a story about the greatest main in alive", rootChapter.getContent());
        Assertions.assertEquals(2, rootChapter.getChildren().size());

        Chapter chapterOne = rootChapter.getChildren().get(0);
        Assertions.assertEquals("Chapter one", chapterOne.getTitle());
        Assertions.assertEquals("this story about awesome dude that call name is Jack",
                chapterOne.getContent());
        Assertions.assertEquals(1, chapterOne.getChildren().size());

        Chapter chapterOneChars = chapterOne.getChildren().get(0);
        Assertions.assertEquals("Jack's characteristics", chapterOneChars.getTitle());
        Assertions.assertNull(chapterOneChars.getContent());
        Assertions.assertEquals(2, chapterOneChars.getChildren().size());

        List<String> characteristics = List.of("height: 71 inch", "weight: 190 pounds");
        for (int i = 0; i < 2; i++) {
            Chapter charChapter = chapterOneChars.getChildren().get(i);
            Assertions.assertEquals(characteristics.get(i), charChapter.getTitle());
            Assertions.assertNull(charChapter.getContent());
            Assertions.assertTrue(charChapter.getChildren().isEmpty());
        }

        Chapter chapterTwo = rootChapter.getChildren().get(1);
        Assertions.assertEquals("Chapter two", chapterTwo.getTitle());
        Assertions.assertEquals("Jack was most famous man in alive\nhis fame was greater than his popularity",
                chapterTwo.getContent());
        Assertions.assertEquals(1, chapterTwo.getChildren().size());

        Chapter chapterJackParents = chapterTwo.getChildren().get(0);
        Assertions.assertEquals("Jack's patents", chapterJackParents.getTitle());
        Assertions.assertNull(chapterJackParents.getContent());
        Assertions.assertEquals(3, chapterJackParents.getChildren().size());

        List<String> parents = List.of("mosquito net", "x-ray", "internal combustion engine");
        for (int i = 0; i < 3; i++) {
            Chapter jackParentChapter = chapterJackParents.getChildren().get(i);
            Assertions.assertEquals(parents.get(i), jackParentChapter.getTitle());
            Assertions.assertNull(jackParentChapter.getContent());
            Assertions.assertTrue(jackParentChapter.getChildren().isEmpty());
        }
    }

    /**
     * Tests parsing of text with no chars
     */
    @Test
    public void handleEmptyText() throws ChapterParsingException {
        InputStreamSource isSource = textAsInputStreamSource("");
        Chapter rootChapter = chapterService.parseChapters(isSource);
        Assertions.assertTrue(rootChapter.getChildren().isEmpty());
        Assertions.assertNull(rootChapter.getTitle());
        Assertions.assertNull(rootChapter.getContent());
    }

    /**
     * Tests parsing of title with hash in the middle
     */
    @Test
    public void titleWithHash() throws ChapterParsingException {
        InputStreamSource isSource = textAsInputStreamSource("#Chapter #one");
        Chapter rootChapter = chapterService.parseChapters(isSource);
        Assertions.assertEquals(1, rootChapter.getChildren().size());
        Assertions.assertNull(rootChapter.getTitle());
        Assertions.assertNull(rootChapter.getContent());

        Chapter chapterOne = rootChapter.getChildren().get(0);
        Assertions.assertTrue(chapterOne.getChildren().isEmpty());
        Assertions.assertEquals("Chapter #one", chapterOne.getTitle());
        Assertions.assertNull(chapterOne.getContent());

    }

    /**
     * Tests parsing of a second level title before a first level title.
     * The send level title should have a first level parent with null title and content.
     */
    @Test
    public void secondLevelBeforeFirst() throws ChapterParsingException {
        InputStreamSource isSource = textAsInputStreamSource("""
                ##Lower
                It is lower level
                #Higher
                It is higher level""");
        Chapter rootChapter = chapterService.parseChapters(isSource);
        Assertions.assertNull(rootChapter.getTitle());
        Assertions.assertNull(rootChapter.getContent());
        Assertions.assertEquals(2, rootChapter.getChildren().size());

        Chapter lowerParent = rootChapter.getChildren().get(0);
        Assertions.assertNull(lowerParent.getTitle());
        Assertions.assertNull(lowerParent.getContent());
        Assertions.assertEquals(1, lowerParent.getChildren().size());

        Chapter lowerChapter = lowerParent.getChildren().get(0);
        Assertions.assertEquals("Lower", lowerChapter.getTitle());
        Assertions.assertEquals("It is lower level", lowerChapter.getContent());
        Assertions.assertTrue(lowerChapter.getChildren().isEmpty());

        Chapter higherChapter = rootChapter.getChildren().get(1);
        Assertions.assertEquals("Higher", higherChapter.getTitle());
        Assertions.assertEquals("It is higher level", higherChapter.getContent());
        Assertions.assertTrue(higherChapter.getChildren().isEmpty());
    }

    /**
     * Tests parsing of non-text file
     */
    @Test
    public void nonTextFile() {
        InputStreamSource isSource = getResourceAsInputStreamSource("1x1_black.png");
        ChapterParsingException ex = Assertions.assertThrows(ChapterParsingException.class,
                () -> chapterService.parseChapters(isSource));
        Assertions.assertEquals("Некорректный файл! Загрузите текстовый файл в кодировке UTF-8!",
                ex.getMessage());
    }

    /**
     * Returns interface that can open {@link InputStream} for the resource file.
     * @param path String path relative to test/resources/ folder
     * @return {@link InputStreamSource}
     */
    private InputStreamSource getResourceAsInputStreamSource(String path) {
        return new InputStreamSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return this.getClass().getClassLoader().getResourceAsStream(path);
            }
        };
    }

    /**
     * Returns interface that can open {@link InputStream} with specified text
     * @param text The specified text
     * @return {@link InputStreamSource}
     */
    private InputStreamSource textAsInputStreamSource(String text) {
        return () -> new ByteArrayInputStream(text.getBytes());
    }
}
