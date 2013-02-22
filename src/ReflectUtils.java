import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {
        private static final long serialVersionUID = 1L;
        {
            put(void.class, Void.class);
            put(byte.class, Byte.class);
            put(short.class, Short.class);
            put(int.class, Integer.class);
            put(long.class, Long.class);
            put(float.class, Float.class);
            put(double.class, Double.class);
            put(boolean.class, Boolean.class);
            put(char.class, Character.class);
        }
    });

    public static Class<?> getWrapperType(Class<?> primitiveType) {
        return PRIMITIVE_TO_WRAPPER.get(primitiveType);
    }

}
