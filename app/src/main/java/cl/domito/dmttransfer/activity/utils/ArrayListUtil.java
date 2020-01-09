package cl.domito.dmttransfer.activity.utils;

import java.util.ArrayList;

public class ArrayListUtil {

    private static ArrayList list;

    private ArrayListUtil() {
    }

    public static synchronized ArrayList getInstance() {
        if(list == null) {
            list = new ArrayList();
        }
        else{
            list.clear();
        }
        return list;
    }
}
