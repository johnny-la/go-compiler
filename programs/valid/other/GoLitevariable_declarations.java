import java.util.*;

public class GoLitevariable_declarations{
    public static <T> T _get_(ArrayList<T> list, int index, boolean isArray, int maxSize, T defaultValue) {
        _ensureCapacity_(list,isArray,maxSize,defaultValue);
        return list.get(index);
    }

    public static <T> void _set_(ArrayList<T> list, int index, T data, boolean isArray, int maxSize, T defaultValue) {
        _ensureCapacity_(list,isArray,maxSize,defaultValue);
        list.set(index, data);
    }

    @SuppressWarnings("unchecked")
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
    static ArrayList<Integer> x = new ArrayList<Integer>();
;
    static ArrayList<ArrayList<Integer>> y = new ArrayList<ArrayList<Integer>>();
;
    static ArrayList<ArrayList<ArrayList<Integer>>> z = new ArrayList<ArrayList<ArrayList<Integer>>>();
;
    static int int1 = 0;
;
    static int y1 = 42;
    static int y2 = 43;
    static int z1 = 1;
    static int z2 = 2;
    static int r2 = 0;
;
    static int r1 = 0;
    static int s1 = 42;
    static int s2 = 43;
    static int t1 = 1;
    static int t2 = 2;
            static class float128 {
        double x = 0.0;
        double y = 0.0;
        public boolean equals(Object object) {
            if (object instanceof float128 ) {
                float128 cur = ((float128) object);
                if (
                    this.x == cur.x &&
                    this.y == cur.y
                 ) return true;
            }
            return false;
        }
    }
        public static void foo(int a) {
        return;
    }


}
