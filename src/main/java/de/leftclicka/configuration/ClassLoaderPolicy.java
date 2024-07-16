package de.leftclicka.configuration;

import java.lang.instrument.Instrumentation;

/**
 * Dictates how a class loader to load the jar on should be determined.
 */
public enum ClassLoaderPolicy {

    /**
     * Will supply a recommended default class loader. Right now this is always the system class loader.
     */
    RECOMMENDED {
        @Override
        public ClassLoader find(Instrumentation instrumentation, Configuration configuration) {
            return ClassLoader.getSystemClassLoader();
        }
    },
    /**
     * Will use the class loader of a specified class that is already loaded.
     * If this policy is used the classLoaderClass attribute of the configuration object used must be set.
     * That attribute will determine the class whose class loader will be used.
     * Note that if that class was loaded by the bootstrap class loader this will not work as the
     * bootstrap class loader does not exist as a java object.
     */
    CUSTOM {
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
