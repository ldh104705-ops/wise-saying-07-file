package com.back.domain.wiseSaying.service;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.repository.WiseSayingRepository;
import com.back.standard.dto.Page;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * 순수 비즈니스 로직. Scanner·콘솔 출력 사용 금지.
 */
public class WiseSayingService {
    public static final int PAGE_SIZE = 5;

    private final WiseSayingRepository wiseSayingRepository;

    public WiseSayingService(WiseSayingRepository wiseSayingRepository) {
        this.wiseSayingRepository = wiseSayingRepository;
    }

    public void makeSampleDataIfEmpty() {
        if (wiseSayingRepository.count() > 0) return;

        IntStream.rangeClosed(1, 10)
                .forEach(i -> write("명언 " + i, "작자미상 " + i));
    }

    public WiseSaying write(String content, String author) {
        return wiseSayingRepository.save(new WiseSaying(content, author));
    }

    public Page<WiseSaying> findForList(String keywordType, String keyword, int page) {
        if (page < 1) page = 1;

        return wiseSayingRepository.findForList(keywordType, keyword, page, PAGE_SIZE);
    }

    public Optional<WiseSaying> findById(int id) {
        return wiseSayingRepository.findById(id);
    }

    public boolean delete(int id) {
        return wiseSayingRepository.delete(id);
    }

    public void build() {
        wiseSayingRepository.build();
    }

    public void modify(WiseSaying wiseSaying, String content, String author) {
        wiseSaying.setContent(content);
        wiseSaying.setAuthor(author);
        wiseSayingRepository.save(wiseSaying);
    }
}
