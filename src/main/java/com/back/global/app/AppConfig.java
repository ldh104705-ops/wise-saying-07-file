package com.back.global.app;

/**
 * 실행 모드 설정.
 * - prod : db/wiseSaying 에 저장
 * - test : db_test/wiseSaying 에 저장 (테스트 전용 폴더)
 */
public class AppConfig {
    private static String mode = "prod";
    private static boolean sampleDataEnabled = true;

    public static void setModeToTest() {
        mode = "test";
    }

    public static boolean isTestMode() {
        return mode.equals("test");
    }

    public static String getDbDirPath() {
        return isTestMode() ? "db_test/wiseSaying" : "db/wiseSaying";
    }

    public static boolean isSampleDataEnabled() {
        return sampleDataEnabled;
    }

    public static void setSampleDataEnabled(boolean sampleDataEnabled) {
        AppConfig.sampleDataEnabled = sampleDataEnabled;
    }
}
