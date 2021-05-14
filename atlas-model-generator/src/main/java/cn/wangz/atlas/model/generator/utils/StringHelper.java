package cn.wangz.atlas.model.generator.utils;

import java.util.Arrays;

import com.google.common.base.CaseFormat;

public class StringHelper {

    public static String toUpperCamel(String value) {
        if (value.contains("_")) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, value);
        }
        if (value.contains("-")) {
            return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, value);
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static String toLowerCamel(String value) {
        if (value.contains("_")) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value);
        }
        if (value.contains("-")) {
            return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, value);
        }
        return value;
    }

    public static String fixIdentifier(String identifier) {
        return identifier.replace("-", "_")
                .replaceAll("\\s", "_");
    }

    static final String KEYWORDS[] = { "abstract", "assert", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while" };

    public static boolean isJavaKeyword(String keyword) {
        return (Arrays.binarySearch(KEYWORDS, keyword) >= 0);
    }


}
