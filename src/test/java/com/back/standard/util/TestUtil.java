package com.back.standard.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class TestUtil {
    private static PrintStream originalOut;
    private static PrintStream captureOut;

    public static Scanner genScanner(String input) {
        return new Scanner(input);
    }

    public static ByteArrayOutputStream setOutToByteArray() {
        if (captureOut != null) throw new IllegalStateException("capture already active");
        originalOut = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        captureOut = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        System.setOut(captureOut);
        return bytes;
    }

    public static void clearSetOutToByteArray() {
        if (captureOut == null) return;
        System.setOut(originalOut);
        captureOut.close();
        captureOut = null;
        originalOut = null;
    }
}
