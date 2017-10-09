import java.util.*;

public class GoLitenesting{
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
    public static void foo(int a, int b, int c) {
        int x = 1;
        int y = 2;
        
        class AnonymousClass1 {
            double x = 0.0;
            double y = 0.0;
            public boolean equals(Object object) {
                if (object instanceof AnonymousClass1 ) {
                    AnonymousClass1 cur = ((AnonymousClass1) object);
                    if (
                        this.x == cur.x &&
                        this.y == cur.y
                     ) return true;
                }
                return false;
            }
        }
        class z {
            AnonymousClass1 temp = new AnonymousClass1();
            public boolean equals(Object object) {
                if (object instanceof z ) {
                    z cur = ((z) object);
                    if (
                        this.temp == cur.temp
                     ) return true;
                }
                return false;
            }
        }

        foo(100,200,300);
        x=(3+17);

    }

        static class point {
        double x = 0.0;
        double y = 0.0;
        public boolean equals(Object object) {
            if (object instanceof point ) {
                point cur = ((point) object);
                if (
                    this.x == cur.x &&
                    this.y == cur.y
                 ) return true;
            }
            return false;
        }
    }

}
