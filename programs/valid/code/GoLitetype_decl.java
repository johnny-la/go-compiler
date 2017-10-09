import java.util.*;

public class GoLitetype_decl{
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
    public static void apple(int a, int b, int c) {
        
        
        
        
        
        [][7]class AnonymousClass1 {
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
                }
                return false;
            }
        }
        class struct_1 {
            String aliasedStr = "";
            ArrayList<ArrayList<Integer>> arrayOfInts = new ArrayList<ArrayList<Integer>>();
            AnonymousClass1 innerStruct = new AnonymousClass1();
            public boolean equals(Object object) {
                if (object instanceof struct_1 ) {
                    struct_1 cur = ((struct_1) object);
                    if (
                        this.aliasedStr == cur.aliasedStr &&
                        this.arrayOfInts == cur.arrayOfInts &&
                        this.innerStruct == cur.innerStruct
                     ) return true;
                }
                return false;
            }
        }

                class struct_2 {
            int b = 0;
            AnonymousClass1 innerStruct = new AnonymousClass1();
            public boolean equals(Object object) {
                if (object instanceof struct_2 ) {
                    struct_2 cur = ((struct_2) object);
                    if (
                        this.b == cur.b &&
                        this.innerStruct == cur.innerStruct
                     ) return true;
                }
                return false;
            }
        }

        ArrayList<struct_2> arrstr = new ArrayList<struct_2>();
;
                class struct_3 {
            int random = 0;
            public boolean equals(Object object) {
                if (object instanceof struct_3 ) {
                    struct_3 cur = ((struct_3) object);
                    if (
                        this.random == cur.random
                     ) return true;
                }
                return false;
            }
        }

    }


}
