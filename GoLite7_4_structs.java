import java.util.*;

public class GoLite7_4_structs{
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
    public static void main(String[] args){
                class AnonymousClass1 {
            int x = 0;
            int y = 0;
            int z = 0;
            public boolean equals(Object object) {
                if (object instanceof AnonymousClass1 ) {
                    AnonymousClass1 cur = ((AnonymousClass1) object);
                    if (
                        this.x == cur.x &&
                        this.y == cur.y &&
                        this.z == cur.z
                     ) return true;
                }
                return false;
            }
        }
        AnonymousClass1 p = new AnonymousClass1();
;
        (p).x = 1;

        (p).y = 2;

        (p).z = 3;

        int z1 = 0;
;
        int y1 = 0;
        int x1 = 0;
        x1 = (p).x;

        y1 = (p).y;

        z1 = (p).z;

                class AnonymousClass2 {
            AnonymousClass1 n = new AnonymousClass1();
            public boolean equals(Object object) {
                if (object instanceof AnonymousClass2 ) {
                    AnonymousClass2 cur = ((AnonymousClass2) object);
                    if (
                        this.n == cur.n
                     ) return true;
                }
                return false;
            }
        }
        AnonymousClass2 q = new AnonymousClass2();
;
        ((q).n).x = 1;

        ((q).n).y = 2;

        ((q).n).z = 3;

        int z2 = 0;
;
        int y2 = 0;
        int x2 = 0;
        x2 = ((q).n).x;

        y2 = ((q).n).y;

        z2 = ((q).n).z;

                ArrayList<AnonymousClass1> t = new ArrayList<AnonymousClass1>();
;
        (_get_(t,0,true,3,new AnonymousClass1())).x = 1;

        (_get_(t,0,true,3,new AnonymousClass1())).y = 2;

        (_get_(t,0,true,3,new AnonymousClass1())).z = 3;

    }


}
