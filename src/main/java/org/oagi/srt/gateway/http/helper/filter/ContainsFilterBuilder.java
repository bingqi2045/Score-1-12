package org.oagi.srt.gateway.http.helper.filter;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ContainsFilterBuilder {

    public static <T> Predicate<T> contains(String query, Function<T, String> mapper) {
        return contains(query, mapper, true);
    }

    public static <T> Predicate<T> contains(String query, Function<T, String> mapper, boolean ignoreCase) {
        if (StringUtils.isEmpty(query)) {
            return e -> true;
        }
        query = query.trim();
        if (isQuoted(query)) {
            return new ExactMatchContainsFilter<T>(unquote(query), mapper, ignoreCase);
        }
        return new AnyWordContainsFilter<T>(query, mapper, ignoreCase);
    }

    public static boolean isQuoted(String s) {
        if (StringUtils.isEmpty(s)) {
            return false;
        }
        if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            return true;
        }
        return false;
    }

    public static String unquote(String s) {
        if (isQuoted(s)) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static List<String> split(String q) {
        return split(q, " ");
    }

    public static List<String> split(String q, String sep) {
        return Arrays.asList(q.split(sep)).stream()
                .map(e -> e.replaceAll("[^a-zA-Z]", "").trim()).collect(Collectors.toList());
    }

}
