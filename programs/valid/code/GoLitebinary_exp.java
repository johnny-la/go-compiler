import java.util.*;

public class GoLitebinary_exp{
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
    public static void binaryOperationsIntegers(int a, int b, int c, char j, char k, String str1, String str2) {
        int addition = (a+b);
        char runeAdd = (j+k);
        String strConcatenation = (str1+str2);
        int subtraction = (a-b);
        int multiplication = (a*b);
        int division = (a/b);
        int remainder = (a%b);
        int piping = (a|b);
        int careting = (a^b);
        int ampersand = (a&b);
        int ampersand_caret = (a& ~(b));
        int shift_left = (a<<b);
        int shift_right = (a>>b);
        boolean bool_val1 = (a==b);
        boolean bool_val2 = (a!=b);
        boolean bool_val3 = (a<b);
        boolean bool_val4 = (a<=b);
        boolean bool_val5 = (a>b);
        boolean bool_val6 = (a>=b);
        boolean bool_val7 = (bool_val6||bool_val5);
        boolean bool_val8 = (bool_val4&&bool_val3);
        int d = ((a*b)+2);
        int e = (a^(2/b));
        int l = ((a+b)-c);
        int m = ((a*b)/c);
        boolean n = ((a==b)!=false);
        int f = (a*(b+2));
        boolean g = ((bool_val1&&bool_val2)||bool_val3);
        boolean h = ((a<=b)&&false);
        boolean i = ((a!=b)||true);
        boolean o = ((true&&false)||true);
        int complicated_expression1 = ((addition*(multiplication/remainder))^((((shift_right%2)%5)& ~(6))>>(((55<<2)*piping)|42354)));
        boolean complicated_expression2 = ((bool_val1&&(bool_val8==bool_val6))||((bool_val1&&bool_val7)&&(2<3)));
    }


}
