package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.standard.dto.Page;
import com.back.standard.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 메모리(리스트) 저장소. 빌드는 지정한 경로에 data.json 을 쓴다.
 */
public class WiseSayingMemoryRepository implements WiseSayingRepository {
    private final List<WiseSaying> wiseSayings = new ArrayList<>();
    private final String buildFilePath;
    private int lastId = 0;

    public WiseSayingMemoryRepository(String buildFilePath) {
        this.buildFilePath = buildFilePath;
    }

    @Override
    public WiseSaying save(WiseSaying wiseSaying) {
        if (wiseSaying.isNew()) {
            wiseSaying.setId(++lastId);
            wiseSayings.add(wiseSaying);
        }

        return wiseSaying;
    }

    @Override
    public List<WiseSaying> findAll() {
        return List.copyOf(wiseSayings);
    }

    @Override
    public Page<WiseSaying> findForList(String keywordType, String keyword, int page, int pageSize) {
        return WiseSayingRepository.toPage(wiseSayings.reversed(), keywordType, keyword, page, pageSize);
    }

    @Override
    public int count() {
        return wiseSayings.size();
    }

    @Override
    public Optional<WiseSaying> findById(int id) {
        return wiseSayings.stream()
                .filter(wiseSaying -> wiseSaying.getId() == id)
                .findFirst();
    }

    @Override
    public boolean delete(int id) {
        return wiseSayings.removeIf(wiseSaying -> wiseSaying.getId() == id);
    }

    @Override
    public void build() {
        JsonUtil.writeFile(buildFilePath, JsonUtil.toJsonArray(findAll().stream().map(WiseSaying::toMap).toList()));
    }
}
