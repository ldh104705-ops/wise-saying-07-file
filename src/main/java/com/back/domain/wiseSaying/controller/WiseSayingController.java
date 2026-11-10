package com.back.domain.wiseSaying.controller;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.service.WiseSayingService;
import com.back.global.rq.Rq;
import com.back.standard.dto.Page;

import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 명언 관련 응대(등록·목록·삭제·수정). 입출력은 컨트롤러에서만 한다.
 */
public class WiseSayingController {
    private final Scanner scanner;
    private final WiseSayingService wiseSayingService;

    public WiseSayingController(Scanner scanner, WiseSayingService wiseSayingService) {
        this.scanner = scanner;
        this.wiseSayingService = wiseSayingService;
    }

    public void actionWrite() {
        System.out.print("명언 : ");
        String content = scanner.nextLine().trim();

        System.out.print("작가 : ");
        String author = scanner.nextLine().trim();

        WiseSaying wiseSaying = wiseSayingService.write(content, author);

        System.out.printf("%d번 명언이 등록되었습니다.%n", wiseSaying.getId());
    }

    public void makeSampleData() {
        wiseSayingService.makeSampleDataIfEmpty();
    }

    public void actionList(Rq rq) {
        String keywordType = rq.getParam("keywordType", "content");
        String keyword = rq.getParam("keyword", "");
        int page = rq.getParamAsInt("page", 1);

        if (!keyword.isBlank()) {
            System.out.println("----------------------");
            System.out.printf("검색타입 : %s%n", keywordType);
            System.out.printf("검색어 : %s%n", keyword);
            System.out.println("----------------------");
        }

        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");

        Page<WiseSaying> wiseSayingPage = wiseSayingService.findForList(keywordType, keyword, page);

        for (WiseSaying wiseSaying : wiseSayingPage.content()) {
            System.out.printf("%d / %s / %s%n", wiseSaying.getId(), wiseSaying.getAuthor(), wiseSaying.getContent());
        }

        System.out.println("----------------------");
        printPageMenu(wiseSayingPage);
    }

    private void printPageMenu(Page<WiseSaying> wiseSayingPage) {
        String pageMenu = IntStream.rangeClosed(1, wiseSayingPage.getTotalPages())
                .mapToObj(i -> i == wiseSayingPage.page() ? "[" + i + "]" : String.valueOf(i))
                .collect(Collectors.joining(" / "));

        System.out.println("페이지 : " + pageMenu);
    }

    public void actionBuild() {
        wiseSayingService.build();

        System.out.println("data.json 파일의 내용이 갱신되었습니다.");
    }

    public void actionDelete(Rq rq) {
        int id = rq.getParamAsInt("id", -1);

        if (id == -1) {
            System.out.println("id를 (숫자로) 입력해주세요.");
            return;
        }

        if (!wiseSayingService.delete(id)) {
            System.out.printf("%d번 명언은 존재하지 않습니다.%n", id);
            return;
        }

        System.out.printf("%d번 명언이 삭제되었습니다.%n", id);
    }

    public void actionModify(Rq rq) {
        int id = rq.getParamAsInt("id", -1);

        if (id == -1) {
            System.out.println("id를 (숫자로) 입력해주세요.");
            return;
        }

        WiseSaying wiseSaying = wiseSayingService.findById(id).orElse(null);

        if (wiseSaying == null) {
            System.out.printf("%d번 명언은 존재하지 않습니다.%n", id);
            return;
        }

        System.out.printf("명언(기존) : %s%n", wiseSaying.getContent());
        System.out.print("명언 : ");
        String content = scanner.nextLine().trim();

        System.out.printf("작가(기존) : %s%n", wiseSaying.getAuthor());
        System.out.print("작가 : ");
        String author = scanner.nextLine().trim();

        wiseSayingService.modify(wiseSaying, content, author);

        System.out.printf("%d번 명언이 수정되었습니다.%n", id);
    }
}
