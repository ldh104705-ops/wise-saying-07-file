package com.back.standard.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JSON 라이브러리 없이 평평한(1단계) JSON 객체를 만들고 읽는다.
 * 값은 문자열 또는 숫자만 지원한다.
 */
public final class JsonUtil {
    private JsonUtil() {
    }

    // ===== 쓰기 =====

    public static String toJson(Map<String, Object> map) {
        return toJson(map, "");
    }

    private static String toJson(Map<String, Object> map, String indent) {
        String body = map.entrySet().stream()
                .map(e -> indent + "  " + quote(e.getKey()) + ": " + toJsonValue(e.getValue()))
                .collect(Collectors.joining(",\n"));

        return indent + "{\n" + body + "\n" + indent + "}";
    }

    public static String toJsonArray(List<Map<String, Object>> maps) {
        if (maps.isEmpty()) return "[]";

        String body = maps.stream()
                .map(map -> toJson(map, "  "))
                .collect(Collectors.joining(",\n"));

        return "[\n" + body + "\n]";
    }

    private static String toJsonValue(Object value) {
        if (value == null) return "null";
        if (value instanceof Number || value instanceof Boolean) return value.toString();

        return quote(value.toString());
    }

    private static String quote(String s) {
        StringBuilder sb = new StringBuilder("\"");

        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }

        return sb.append('"').toString();
    }

    // ===== 읽기 =====

    public static Map<String, Object> parseObject(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        String s = json.trim();

        if (!s.startsWith("{") || !s.endsWith("}")) throw new IllegalArgumentException("JSON 객체가 아닙니다: " + json);

        int i = 1;
        int end = s.length() - 1;

        while (i < end) {
            i = skipWhitespaceAndComma(s, i, end);
            if (i >= end) break;

            // key
            int[] keyEnd = new int[1];
            String key = readString(s, i, keyEnd);
            i = keyEnd[0];

            i = skipWhitespace(s, i, end);
            if (s.charAt(i) != ':') throw new IllegalArgumentException("':' 이 필요합니다: " + json);
            i = skipWhitespace(s, i + 1, end);

            // value
            if (s.charAt(i) == '"') {
                int[] valueEnd = new int[1];
                map.put(key, readString(s, i, valueEnd));
                i = valueEnd[0];
            } else {
                int start = i;
                while (i < end && s.charAt(i) != ',' && !Character.isWhitespace(s.charAt(i))) i++;
                map.put(key, parseLiteral(s.substring(start, i)));
            }
        }

        return map;
    }

    private static Object parseLiteral(String literal) {
        return switch (literal) {
            case "null" -> null;
            case "true" -> true;
            case "false" -> false;
            default -> literal.contains(".") ? (Object) Double.parseDouble(literal) : (Object) Long.parseLong(literal);
        };
    }

    private static String readString(String s, int start, int[] endOut) {
        if (s.charAt(start) != '"') throw new IllegalArgumentException("문자열이 필요합니다: " + s);

        StringBuilder sb = new StringBuilder();
        int i = start + 1;

        while (s.charAt(i) != '"') {
            char c = s.charAt(i);

            if (c == '\\') {
                char next = s.charAt(++i);
                switch (next) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                        i += 4;
                    }
                    default -> sb.append(next);
                }
            } else {
                sb.append(c);
            }

            i++;
        }

        endOut[0] = i + 1;
        return sb.toString();
    }

    private static int skipWhitespace(String s, int i, int end) {
        while (i < end && Character.isWhitespace(s.charAt(i))) i++;
        return i;
    }

    private static int skipWhitespaceAndComma(String s, int i, int end) {
        while (i < end && (Character.isWhitespace(s.charAt(i)) || s.charAt(i) == ',')) i++;
        return i;
    }

    // ===== 파일 =====

    public static void writeFile(String filePath, String content) {
        try {
            Path path = Path.of(filePath);
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void deleteDir(String dirPath) {
        Path path = Path.of(dirPath);

        if (!Files.exists(path)) return;

        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
