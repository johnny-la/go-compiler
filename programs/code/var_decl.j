import java.util.*;

public class Main {
    public static <T> T _get_(ArrayList<T> list, int index, boolean isArray, int maxSize, T defaultValue) {
        _ensureCapacity_(list,isArray,maxSize,defaultValue);
        return list.get(index);
    }

    public static <T> void _set_(ArrayList<T> list, int index, T data, boolean isArray, int maxSize, T defaultValue) {
        _ensureCapacity_(list,isArray,maxSize,defaultValue);
        list.set(index, data);
    }

    public static <T> void _ensureCapacity_(ArrayList<T> list, boolean isArray, int maxSize, T defaultValue) {
        if (isArray) {
            for (int i = list.size(); i < maxSize; i++) {
                try {
                    T value = null;
                    if (isPrimitive(defaultValue))
                        value = defaultValue;
                    else
                        value = (T)defaultValue.getClass().newInstance();
                    list.add(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static <T> boolean isPrimitive(T value) {
        if (value.getClass().equals(Integer.class)
            || value.getClass().equals(Double.class)
            || value.getClass().equals(Character.class)
            || value.getClass().equals(Boolean.class)
            || value.getClass().equals(String.class))
            return true;

        return false;
    }
    public static  temp(int n, int m, int l) {
        String a = "pears";
        int b = 3;
        double c = 3.5;
        char d = 'a';
        boolean e = false;
        String aa = "pears";
        int bb = 3;
        double cc = 3.5;
        boolean ee = true;
        char dd = 'a';
        int x1 = 0;
        int x2 = 0;
        String x3 = "";
        String x4 = "";
        double x5 = 0.0;
        double x6 = 0.0;
        char x7 = 0;
        char x8 = 0;
        boolean x9 = false;
        boolean x10 = false;
        double y1 = 62.4;
        char y2 = 'a';
        String y3 = "string";
        int y4 = 4;
        char z1 = 'c';
        char z2 = 'k';
        String a1 = "";
        int x11 = 0;
        int x22 = 0;
        int y11 = 42;
        int y22 = 43;
        double z11 = 1.3;
        double z22 = 2.8;
        ArrayList<ArrayList<Integer>> x = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<String>> z = new ArrayList<ArrayList<String>>();
        double y = 0.0;
        String j = "";
        y = 0.7;
        j = "corn";

        int k = 0;
        y = 0.7;
        j = "corn";
        k = 13;

        return z11;
    }


}
