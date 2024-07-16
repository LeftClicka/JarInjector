package de.leftclicka;

import de.leftclicka.configuration.Configuration;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.*;

public class Agent {

    public static void agentmain(String arg, Instrumentation instrumentation) throws Throwable {
        Configuration config = Configuration.decode(arg);
        ClassLoader classLoader = config.getClassLoaderPolicy().find(instrumentation, config);
        config.getInjectionMethod().inject(classLoader, config);
        Class<?> mainClass = config.getMainClassPolicy().find(classLoader, config);
        Method mainMethod = config.getMainMethodPolicy().find(mainClass, config);
        mainMethod.setAccessible(true);
        if (Modifier.isStatic(mainMethod.getModifiers())) {
            mainMethod.invoke(null, getParams(mainMethod));
        } else {
            mainMethod.invoke(makeInstance(mainClass), getParams(mainMethod));
        }
    }

    private static Object makeInstance(Class<?> clazz) {
        Constructor<?> lowestParamConstructor = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (lowestParamConstructor == null || constructor.getParameterCount() < lowestParamConstructor.getParameterCount())
                lowestParamConstructor = constructor;
        }
        lowestParamConstructor.setAccessible(true);
        try {
            return lowestParamConstructor.newInstance(getParams(lowestParamConstructor));
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object[] getParams(Executable method) {
        Parameter[] params = method.getParameters();
        Object[] out = new Object[method.getParameterCount()];
        for (int i = 0; i < out.length; i++) {
            Class<?> paramClass = params[i].getType();
            if (paramClass.equals(String[].class)) {
                //this check only exists to not pass null to public static void main(String[] args)
                out[i] = new String[0];
            } else out[i] = null;
        }
        return out;
    }

}
