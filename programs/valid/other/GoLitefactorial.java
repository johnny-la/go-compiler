import java.util.*;

public class GoLitefactorial{
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
    public static void fib_helper(int n, ArrayList<Integer> memo) {
        int i = 2;
        while ((i<=n)) {
            memo.add((_get_(memo,(i-1),false,0,0)+_get_(memo,(i-2),false,0,0)));

            continue;
        }
        return;
    }

    public static int fib(int n) {
        {
            if ((n<0)) {
                System.out.println("" + "Error: fibonacci cannot accept a negative number");
                return (-1);
            } else {
                {
                    if ((n>=0)) {
                        ArrayList<Integer> memo = new ArrayList<Integer>();
;
                        memo.add(0);

                        memo.add(1);

                        fib_helper(n,memo);
                        return _get_(memo,n,false,0,0);
                    } else {
                        return (-1);
                    }
                }
            }
        }
    }

    public static void main(String[] args){
        System.out.print("" + fib(3));
    }


}
