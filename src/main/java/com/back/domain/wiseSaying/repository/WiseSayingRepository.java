package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.standard.dto.Page;

import java.util.List;
import java.util.Optional;

/**
 * 데이터 생성·조회·수정·삭제 담당.
 * 구현체(메모리/파일)만 갈아 끼우면 컨트롤러·서비스는 그대로 동작한다.
 */
public interface WiseSayingRepository {
    WiseSaying save(WiseSaying wiseSaying);

    /** 번호 오름차순 전체 목록 */
    List<WiseSaying> findAll();

    /** 검색 + 최신순 + 페이징 */
    Page<WiseSaying> findForList(String keywordType, String keyword, int page, int pageSize);

    int count();

    Optional<WiseSaying> findById(int id);

    boolean delete(int id);

    /** 모든 명언을 data.json 으로 내보낸다. */
    void build();

    static boolean matches(WiseSaying wiseSaying, String keywordType, String keyword) {
        if (keyword == null || keyword.isBlank()) return true;

        return switch (keywordType) {
            case "content" -> wiseSaying.getContent().contains(keyword);
            case "author" -> wiseSaying.getAuthor().contains(keyword);
            default -> wiseSaying.getContent().contains(keyword) || wiseSaying.getAuthor().contains(keyword);
        };
    }

    static Page<WiseSaying> toPage(List<WiseSaying> latestFirst, String keywordType, String keyword, int page, int pageSize) {
        List<WiseSaying> filtered = latestFirst.stream()
                .filter(wiseSaying -> matches(wiseSaying, keywordType, keyword))
                .toList();

        List<WiseSaying> content = filtered.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();

        return new Page<>(content, filtered.size(), page, pageSize);
    }
}
