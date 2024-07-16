package de.leftclicka.configuration;

import java.lang.instrument.Instrumentation;

public enum ClassLoaderPolicy {

    RECOMMENDED {
        @Override
        public ClassLoader find(Instrumentation instrumentation, Configuration configuration) {
            return ClassLoader.getSystemClassLoader();
        }
    }, CUSTOM {
        @Override
        public ClassLoader find(Instrumentation instrumentation, Configuration configuration) {
            for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                if (clazz.getName().equals(configuration.getClassLoaderClass())) {
                    return clazz.getClassLoader();
                }
            }
            throw new IllegalArgumentException("No such class: "+configuration.getClassLoaderClass());
        }
    };

    public abstract ClassLoader find(Instrumentation instrumentation, Configuration configuration);

}
