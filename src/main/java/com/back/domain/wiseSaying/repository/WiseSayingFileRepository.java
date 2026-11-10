package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.standard.dto.Page;
import com.back.standard.util.JsonUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 파일 저장소.
 * - 명언 하나 = 파일 하나 : {dir}/{id}.json
 * - 마지막 번호 : {dir}/lastId.txt
 * - 빌드 결과 : {dir}/data.json
 * 파일 처리는 이 리포지터리에만 있다.
 */
public class WiseSayingFileRepository implements WiseSayingRepository {
    private final Path dirPath;

    public WiseSayingFileRepository(String dirPath) {
        this.dirPath = Path.of(dirPath);
    }

    private Path getFilePath(int id) {
        return dirPath.resolve(id + ".json");
    }

    private Path getLastIdPath() {
        return dirPath.resolve("lastId.txt");
    }

    public Path getBuildPath() {
        return dirPath.resolve("data.json");
    }

    public int getLastId() {
        Path path = getLastIdPath();

        if (!Files.exists(path)) return 0;

        try {
            String text = Files.readString(path, StandardCharsets.UTF_8).trim();
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void setLastId(int lastId) {
        JsonUtil.writeFile(getLastIdPath().toString(), String.valueOf(lastId));
    }

    @Override
    public WiseSaying save(WiseSaying wiseSaying) {
        if (wiseSaying.isNew()) {
            int newId = getLastId() + 1;
            wiseSaying.setId(newId);
            setLastId(newId);
        }

        JsonUtil.writeFile(getFilePath(wiseSaying.getId()).toString(), JsonUtil.toJson(wiseSaying.toMap()));

        return wiseSaying;
    }

    @Override
    public List<WiseSaying> findAll() {
        if (!Files.exists(dirPath)) return List.of();

        try (Stream<Path> paths = Files.list(dirPath)) {
            return paths
                    .filter(path -> path.getFileName().toString().matches("\\d+\\.json"))
                    .map(this::read)
                    .sorted(Comparator.comparingInt(WiseSaying::getId))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private WiseSaying read(Path path) {
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            return WiseSaying.fromMap(JsonUtil.parseObject(json));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Page<WiseSaying> findForList(String keywordType, String keyword, int page, int pageSize) {
        return WiseSayingRepository.toPage(findAll().reversed(), keywordType, keyword, page, pageSize);
    }

    @Override
    public int count() {
        return findAll().size();
    }

    @Override
    public Optional<WiseSaying> findById(int id) {
        Path path = getFilePath(id);

        if (!Files.exists(path)) return Optional.empty();

        return Optional.of(read(path));
    }

    @Override
    public boolean delete(int id) {
        try {
            return Files.deleteIfExists(getFilePath(id));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void build() {
        List<java.util.Map<String, Object>> maps = findAll().stream().map(WiseSaying::toMap).toList();
        JsonUtil.writeFile(getBuildPath().toString(), JsonUtil.toJsonArray(maps));
    }
}
