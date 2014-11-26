package novoda.android.typewriter.introspection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import novoda.android.typewriter.annotation.Mapper;
import novoda.android.typewriter.util.StringUtil;

public class RichClass<T> {

    private final Class<T> klass;

    private final List<Method> methods;

    private final Map<String, String> fieldMapper;

    private final Map<String, Method> methodMapper;

    public RichClass(Class<T> klass) {
        this.klass = klass;
        methods = Arrays.asList(klass.getMethods());

        fieldMapper = new HashMap<String, String>();
        for (Field f : klass.getDeclaredFields()) {
            Mapper s = f.getAnnotation(Mapper.class);
            if (s != null) {
                fieldMapper.put(s.value(), f.getName());
            }
        }

        methodMapper = new HashMap<String, Method>();
        for (Method m : methods) {
            Mapper mm = m.getAnnotation(Mapper.class);
            if (mm != null) {
                methodMapper.put(mm.value(), m);
            }
        }
    }

    public Method setter(String what) {
        if (methodMapper.containsKey(what)) {
            return methodMapper.get(what);
        }
        String methodName = StringUtil.asCamelifySetMethod(what);
        if (fieldMapper.containsKey(what)) {
            methodName = StringUtil.asCamelifySetMethod(fieldMapper.get(what));
        }

        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        throw new RichClassException("can not find method " + what + " " + fieldMapper);
    }

    public boolean hasMethod(String method) {
        try {
            return setter(method) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public T newInstance() {
        try {
            return klass.newInstance();
        } catch (Exception e) {
            throw new RichClassException(e);
        }
    }

    public static class RichClassException extends RuntimeException {
        public RichClassException(String reason) {
            super(reason);
        }

        public RichClassException(Exception reason) {
            super(reason);
        }
    }
}
