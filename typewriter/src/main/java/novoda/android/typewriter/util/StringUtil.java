package novoda.android.typewriter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static final String SET = "set";
    public static final String GET = "get";

    public static String camelify(String original) {
        if (original == null)
            return "";
        StringBuilder builder = new StringBuilder();
        Pattern p = Pattern.compile("[a-zA-Z0-9]+");
        Matcher m = p.matcher(original);
        String word;
        while (m.find()) {
            word = m.group();
            builder.append(capitalize(word));
        }
        return builder.toString();
    }

    private static String capitalize(String string) {
        if (string.length() > 1)
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        return "";
    }

    public static String snakify(String original) {
        if (original == null || original.length() == 0)
            return "";

        StringBuilder builder = new StringBuilder();
        Pattern p = Pattern.compile("[A-Z][a-z0-9]+");
        Matcher m = p.matcher(original);
        String word;
        while (m.find()) {
            word = m.group();
            builder.append(word.toLowerCase());
            if (!m.hitEnd()) {
                builder.append('_');
            }
        }
        if (builder.length() == 0) {
            return original.toLowerCase();
        }
        return builder.toString();
    }

    public static String asCamelifyGetMethod(String original) {
        return GET + camelify(original);
    }

    public static String asCamelifySetMethod(String original) {
        return SET + camelify(original);
    }
}
