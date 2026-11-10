package com.back.domain.wiseSaying.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class WiseSaying {
    private int id;
    private String content;
    private String author;

    public WiseSaying(String content, String author) {
        this(0, content, author);
    }

    public WiseSaying(int id, String content, String author) {
        this.id = id;
        this.content = content;
        this.author = author;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("content", content);
        map.put("author", author);
        return map;
    }

    public static WiseSaying fromMap(Map<String, Object> map) {
        return new WiseSaying(
                ((Number) map.get("id")).intValue(),
                (String) map.get("content"),
                (String) map.get("author")
        );
    }

    public boolean isNew() {
        return id == 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
