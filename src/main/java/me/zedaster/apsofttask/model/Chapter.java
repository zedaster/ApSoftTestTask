package me.zedaster.apsofttask.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Model of Chapter. The class contains title, content and children chapters.
 */
@JsonIncludeProperties({"title", "content", "children"})
public class Chapter {
    /**
     * Title of the chapter
     */
    private String title;

    /**
     * Content of the chapter
     */
    private String content;

    /**
     * Subchapters for the chapter
     */
    private List<Chapter> children;

    public Chapter() {
        this.title = null;
        this.content = null;
        this.children = new ArrayList<>();
    }

    public Chapter(String title, String content) {
        this.title = title;
        this.content = content;
        this.children = new ArrayList<>();
    }

    /**
     * Returns title of the chapter
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns content of the chapter
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns an unmodifiable view of the subchapter list for the chapter.
     */
    public List<Chapter> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Sets title for the chapter
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets content for the chapter
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the last child of the chapter.
     * If the chapter doesn't have any children, the method creates a new child chapter and returns it
     *
     * @return Chapter
     */
    public Chapter createOrGetLastChild() {
        if (children.isEmpty()) {
            Chapter chapter = new Chapter();
            children.add(chapter);
            return chapter;
        }
        return children.get(children.size() - 1);
    }

    /**
     * Adds new child (subchapter) for the chapter
     * @param child subchapter to add
     */
    public void addChild(Chapter child) {
        children.add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(title, chapter.title) &&
                Objects.equals(content, chapter.content) &&
                Objects.equals(children, chapter.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, children);
    }
}
