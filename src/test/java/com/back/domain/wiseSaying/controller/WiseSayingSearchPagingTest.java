package com.back.domain.wiseSaying.controller;

import com.back.AppTestRunner;
import com.back.global.app.AppConfig;
import com.back.standard.util.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WiseSayingSearchPagingTest {
    @BeforeAll
    static void beforeAll() {
        AppConfig.setModeToTest();
    }

    @BeforeEach
    void beforeEach() {
        JsonUtil.deleteDir(AppConfig.getDbDirPath());
    }

    @AfterEach
    void afterEach() {
        AppConfig.setSampleDataEnabled(false);
    }

    @Test
    @DisplayName("목록?keywordType=content&keyword=과거")
    void t1() {
        AppConfig.setSampleDataEnabled(false);

        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                목록?keywordType=content&keyword=과거
                종료
                """);

        assertThat(out)
                .containsSubsequence(
                        "----------------------",
                        "검색타입 : content",
                        "검색어 : 과거",
                        "----------------------",
                        "번호 / 작가 / 명언",
                        "2 / 작자미상 / 과거에 집착하지 마라."
                )
                .doesNotContain("1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("목록?keywordType=author&keyword=작자")
    void t2() {
        AppConfig.setSampleDataEnabled(false);

        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                홍길동
                목록?keywordType=author&keyword=작자
                종료
                """);

        assertThat(out)
                .contains("검색타입 : author")
                .contains("검색어 : 작자")
                .contains("1 / 작자미상 / 현재를 사랑하라.")
                .doesNotContain("2 / 홍길동 / 과거에 집착하지 마라.");
    }

    @Test
    @DisplayName("명언이 0개면 샘플 10개 생성, 목록 1페이지는 10~6번")
    void t3() {
        AppConfig.setSampleDataEnabled(true);

        String out = AppTestRunner.run("""
                목록
                종료
                """);

        assertThat(out)
                .containsSubsequence(
                        "10 / 작자미상 10 / 명언 10",
                        "9 / 작자미상 9 / 명언 9",
                        "8 / 작자미상 8 / 명언 8",
                        "7 / 작자미상 7 / 명언 7",
                        "6 / 작자미상 6 / 명언 6",
                        "----------------------",
                        "페이지 : [1] / 2"
                )
                .doesNotContain("5 / 작자미상 5 / 명언 5");
    }

    @Test
    @DisplayName("목록?page=2 는 5~1번")
    void t4() {
        AppConfig.setSampleDataEnabled(true);

        String out = AppTestRunner.run("""
                목록?page=2
                종료
                """);

        assertThat(out)
                .containsSubsequence(
                        "5 / 작자미상 5 / 명언 5",
                        "4 / 작자미상 4 / 명언 4",
                        "3 / 작자미상 3 / 명언 3",
                        "2 / 작자미상 2 / 명언 2",
                        "1 / 작자미상 1 / 명언 1",
                        "페이지 : 1 / [2]"
                )
                .doesNotContain("6 / 작자미상 6 / 명언 6");
    }

    @Test
    @DisplayName("검색 결과를 기준으로 페이징 - 내용에 1 검색하면 10번, 1번")
    void t5() {
        AppConfig.setSampleDataEnabled(true);

        String out = AppTestRunner.run("""
                목록?keywordType=content&keyword=1
                종료
                """);

        assertThat(out)
                .containsSubsequence(
                        "10 / 작자미상 10 / 명언 10",
                        "1 / 작자미상 1 / 명언 1",
                        "페이지 : [1]"
                )
                .doesNotContain("2 / 작자미상 2 / 명언 2")
                .doesNotContain("페이지 : [1] / 2");
    }

    @Test
    @DisplayName("검색 + page - 검색 결과 기준으로 2페이지")
    void t6() {
        AppConfig.setSampleDataEnabled(true);

        String out = AppTestRunner.run("""
                목록?keywordType=author&keyword=작자&page=2
                종료
                """);

        assertThat(out)
                .contains("검색타입 : author")
                .contains("5 / 작자미상 5 / 명언 5")
                .contains("페이지 : 1 / [2]")
                .doesNotContain("6 / 작자미상 6 / 명언 6");
    }
}
