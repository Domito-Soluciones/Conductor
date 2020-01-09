package cl.domito.dmttransfer.activity.utils;

public class StringBuilderUtil {

    private static StringBuilder stringBuilder;

    private StringBuilderUtil() {
    }

    public static synchronized StringBuilder getInstance() {
        if(stringBuilder == null) {
            stringBuilder = new StringBuilder();
        }
        else{
            stringBuilder.setLength(0);
        }
        return stringBuilder;
    }
}
