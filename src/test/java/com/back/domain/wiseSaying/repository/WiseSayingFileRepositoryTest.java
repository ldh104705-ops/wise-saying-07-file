package com.back.domain.wiseSaying.repository;

import com.back.AppTestRunner;
import com.back.global.app.AppConfig;
import com.back.standard.util.JsonUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WiseSayingFileRepositoryTest {
    private static Path dir() {
        return Path.of(AppConfig.getDbDirPath());
    }

    @BeforeAll
    static void beforeAll() {
        AppConfig.setModeToTest();
    }

    @BeforeEach
    void beforeEach() {
        // 테스트 전용 폴더를 매 테스트 전에 비운다.
        JsonUtil.deleteDir(AppConfig.getDbDirPath());
        AppConfig.setSampleDataEnabled(false);
    }

    @Test
    @DisplayName("테스트는 실제 db/ 가 아닌 테스트 전용 폴더를 쓴다")
    void t0() {
        assertThat(AppConfig.getDbDirPath()).isEqualTo("db_test/wiseSaying");
    }

    @Test
    @DisplayName("등록하면 {번호}.json 과 lastId.txt 가 생긴다")
    void t1() throws IOException {
        AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                종료
                """);

        Path file = dir().resolve("1.json");
        assertThat(file).exists();

        String json = Files.readString(file, StandardCharsets.UTF_8);
        assertThat(json).isEqualTo("""
                {
                  "id": 1,
                  "content": "현재를 사랑하라.",
                  "author": "작자미상"
                }""");

        Map<String, Object> map = JsonUtil.parseObject(json);
        assertThat(map.get("id")).isEqualTo(1L);
        assertThat(map.get("content")).isEqualTo("현재를 사랑하라.");

        assertThat(Files.readString(dir().resolve("lastId.txt")).trim()).isEqualTo("1");
    }

    @Test
    @DisplayName("프로그램을 다시 시작해도 목록이 남아 있다")
    void t2() {
        AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                종료
                """);

        // 재시작
        String out = AppTestRunner.run("""
                목록
                종료
                """);

        assertThat(out).containsSubsequence(
                "2 / 작자미상 / 과거에 집착하지 마라.",
                "1 / 작자미상 / 현재를 사랑하라."
        );
    }

    @Test
    @DisplayName("수정하면 파일이 갱신되고 재시작 후에도 유지된다")
    void t3() throws IOException {
        AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                수정?id=1
                현재와 자신을 사랑하라.
                홍길동
                종료
                """);

        assertThat(Files.readString(dir().resolve("1.json"), StandardCharsets.UTF_8))
                .contains("\"content\": \"현재와 자신을 사랑하라.\"")
                .contains("\"author\": \"홍길동\"");

        String out = AppTestRunner.run("""
                목록
                종료
                """);

        assertThat(out).contains("1 / 홍길동 / 현재와 자신을 사랑하라.");
    }

    @Test
    @DisplayName("삭제하면 파일이 지워지고, 재시작 후 다음 번호는 삭제된 번호를 재사용하지 않는다")
    void t4() {
        AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                삭제?id=2
                종료
                """);

        assertThat(dir().resolve("2.json")).doesNotExist();
        assertThat(dir().resolve("1.json")).exists();

        // 재시작 후 등록 → 3번
        String out = AppTestRunner.run("""
                등록
                새 명언
                새 작가
                목록
                종료
                """);

        assertThat(out)
                .contains("3번 명언이 등록되었습니다.")
                .doesNotContain("2번 명언이 등록되었습니다.")
                .containsSubsequence("3 / 새 작가 / 새 명언", "1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("빌드하면 모든 명언이 배열 형태의 data.json 으로 만들어진다")
    void t5() throws IOException {
        String out = AppTestRunner.run("""
                등록
                명언 1
                작가 1
                등록
                명언 2
                작가 2
                빌드
                종료
                """);

        assertThat(out).contains("data.json 파일의 내용이 갱신되었습니다.");

        String json = Files.readString(dir().resolve("data.json"), StandardCharsets.UTF_8);
        assertThat(json).isEqualTo("""
                [
                  {
                    "id": 1,
                    "content": "명언 1",
                    "author": "작가 1"
                  },
                  {
                    "id": 2,
                    "content": "명언 2",
                    "author": "작가 2"
                  }
                ]""");
    }

    @Test
    @DisplayName("따옴표·역슬래시가 들어간 명언도 저장/복원된다")
    void t6() {
        AppTestRunner.run("""
                등록
                그는 "괜찮아" 라고 말했다 \\ 끝
                작자미상
                종료
                """);

        String out = AppTestRunner.run("""
                목록
                종료
                """);

        assertThat(out).contains("1 / 작자미상 / 그는 \"괜찮아\" 라고 말했다 \\ 끝");
    }
}
