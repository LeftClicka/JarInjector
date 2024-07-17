package de.leftclicka;

import de.leftclicka.configuration.Configuration;
import de.leftclicka.configuration.InjectionMethod;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.*;

public class Agent {

    /**
     * Will extract data from the configuration and manage the loading process.
     * Invoked by the target jvm when the agent is loaded
     */
    public static void agentmain(String arg, Instrumentation instrumentation) throws Exception {
        Configuration config = Configuration.decode(arg);
        ClassLoader classLoader = config.getClassLoaderPolicy().find(instrumentation, config);
        InjectionMethod.INJECTCLASSPATH.inject(classLoader, config);
        Class<?> mainClass = config.getMainClassPolicy().find(classLoader, config);
        Method mainMethod = config.getMainMethodPolicy().find(mainClass, config);
        mainMethod.setAccessible(true);
        if (Modifier.isStatic(mainMethod.getModifiers())) {
            mainMethod.invoke(null, getParams(mainMethod));
        } else {
            mainMethod.invoke(makeInstance(mainClass), getParams(mainMethod));
        }
    }

    private static Object makeInstance(Class<?> clazz) throws Exception{
        Constructor<?> lowestParamConstructor = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (lowestParamConstructor == null || constructor.getParameterCount() < lowestParamConstructor.getParameterCount())
                lowestParamConstructor = constructor;
        }
        lowestParamConstructor.setAccessible(true);
        return lowestParamConstructor.newInstance(getParams(lowestParamConstructor));
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
