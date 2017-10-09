import java.util.*;

public class GoLiteaverage{
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
    public static int average(ArrayList<Integer> array, int n) {
        int total = 0;
        {
            int i = 0;
            i=0;
            for (;(i<n);i++) {
                total+=_get_(array,i,true,5,0);

            }
        }

        return (total/n);
    }

    public static void main(String[] args){
        int n = 5;
        ArrayList<Integer> array = new ArrayList<Integer>();
;
        _set_(array,0,10,true,5,0);

        _set_(array,1,20,true,5,0);

        _set_(array,2,30,true,5,0);

        _set_(array,3,40,true,5,0);

        _set_(array,4,50,true,5,0);

        System.out.println("" + "The average is" + " " + average(array,5));
    }


}
