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
    static class AnonymousClass1 {
        double a = 0.0;
        double b = 0.0;
        double c = 0.0;
        public boolean equals(Object object) {
            if (object instanceof AnonymousClass1 ) {
                AnonymousClass1 cur = ((AnonymousClass1) object);
                if (
                    this.a == cur.a &&
                    this.b == cur.b &&
                    this.c == cur.c
                 ) return true;
            } else {
                return false;
            }
        }
    }
    static class struct_1 {
        String str = "";
        AnonymousClass1 innerStruct = new AnonymousClass1();
        public boolean equals(Object object) {
            if (object instanceof struct_1 ) {
                struct_1 cur = ((struct_1) object);
                if (
                    this.str == cur.str &&
                    this.innerStruct == cur.innerStruct
                 ) return true;
            } else {
                return false;
            }
        }
    }
    public static void _int(int a, int b, int c) {
        
        struct_1 x = new struct_1();
        struct_1 y = new struct_1();
        ArrayList<struct_1> z = new ArrayList<struct_1>();
        z.add(y);

        (str).x = "apple";

        (a).(innerStruct).x = 5.3;

        (b).(innerStruct).x = 3.7;

        (c).(innerStruct).x = 4.7;

    }


}
