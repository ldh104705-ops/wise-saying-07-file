package com.back.global.rq;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Rq {
    private final String actionName;
    private final Map<String, String> params;

    public Rq(String cmd) {
        String[] cmdBits = cmd.split("\\?", 2);

        actionName = cmdBits[0].trim();
        String queryString = cmdBits.length > 1 ? cmdBits[1] : "";

        params = Arrays
                .stream(queryString.split("&"))
                .map(queryParam -> queryParam.split("=", 2))
                .filter(e -> e.length == 2 && !e[0].isBlank() && !e[1].isBlank())
                .collect(Collectors.toMap(
                        bits -> bits[0].trim(),
                        bits -> bits[1].trim(),
                        (previous, current) -> current
                ));
    }

    public String getActionName() {
        return actionName;
    }

    public String getParam(String name, String defaultValue) {
        return params.getOrDefault(name, defaultValue);
    }

    public int getParamAsInt(String name, int defaultValue) {
        String value = getParam(name, "");

        if (value.isBlank()) return defaultValue;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
