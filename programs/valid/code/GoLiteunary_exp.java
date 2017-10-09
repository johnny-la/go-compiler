import java.util.*;

public class GoLiteunary_exp{
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
    public static void unaryOperationsIntegers() {
        int a = (+1);
        int b = (-2);
        boolean c = !(false);
        int d = ~(4);
        int multiplePlus = (+(+(+(+1))));
        int multipleMinus = (-(-(-(-(-1)))));
        int mixedPlusMinus = (+(-(+(-(-(+4))))));
        boolean mixedMultipleExclam = !(!(!(!(!(!(!(!(false))))))));
        int mixedMultipleCaret = ~(~(~(~(~(5)))));
        int complicatedExpression1 = ((+2)-((-5)*~(~(~(~(~(~((~(6)*(((-5)*(-9))+((-8)%~(2))))))))))));
        boolean complicatedExpression2 = !(!(!(!(!(((~(~(~(~((~(~(5))^5)))))>>222)==(+1634)))))));
    }


}
