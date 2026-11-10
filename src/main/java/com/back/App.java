package com.back;

import com.back.domain.system.controller.SystemController;
import com.back.domain.wiseSaying.controller.WiseSayingController;
import com.back.global.app.AppConfig;
import com.back.global.app.AppContext;
import com.back.global.rq.Rq;

import java.util.Scanner;

/**
 * 입력을 받아 알맞은 컨트롤러로 넘기기만 한다.
 */
public class App {
    private final Scanner scanner;
    private final SystemController systemController;
    private final WiseSayingController wiseSayingController;

    public App(Scanner scanner) {
        AppContext.renew(scanner);

        this.scanner = AppContext.scanner;
        this.systemController = AppContext.systemController;
        this.wiseSayingController = AppContext.wiseSayingController;
    }

    public void run() {
        if (AppConfig.isSampleDataEnabled()) {
            wiseSayingController.makeSampleData();
        }

        System.out.println("== 명언 앱 ==");

        while (true) {
            System.out.print("명령) ");

            if (!scanner.hasNextLine()) return;

            String cmd = scanner.nextLine().trim();

            if (cmd.isBlank()) continue;

            Rq rq = new Rq(cmd);

            switch (rq.getActionName()) {
                case "종료" -> {
                    systemController.actionExit();
                    return;
                }
                case "등록" -> wiseSayingController.actionWrite();
                case "목록" -> wiseSayingController.actionList(rq);
                case "삭제" -> wiseSayingController.actionDelete(rq);
                case "수정" -> wiseSayingController.actionModify(rq);
                case "빌드" -> wiseSayingController.actionBuild();
                default -> System.out.println("알 수 없는 명령입니다.");
            }
        }
    }
}
