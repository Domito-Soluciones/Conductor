package cl.domito.dmttransfer.activity.utils;

public class StringBuilderServiceUtil {

    private static StringBuilder stringBuilder;

    private StringBuilderServiceUtil() {
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
