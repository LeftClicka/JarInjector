package de.leftclicka.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Dictates which of the main class' methods should be used as a program entry point.
 * If the main method is static, it will be invoked as such. If it is an instance method, an
 * instance will be created to invoke the main method. For that, the constructor with the lowest
 * amount of parameters will be fetched and invoked with null passed to all parameters.
 * The main method will also be invoked with null passed to all parameters. An exception to this occurs
 * when one of the parameters is of type String[] - in that case, an empty String[] will be passed. This is
 * to avoid passing null to a public static void main(String[] args) method, which usually never happens
 * when that method is invoked by the jvm.
 */
public enum MainMethodPolicy {

    /**
     * Will find a method of a certain name.
     * Note that the configuration's customMainMethod attribute should be set for this to work,
     * as that value will be used as the method name.
     * If more than one method of that name exists the one with the lowest amount of parameters will be used.
     */
    CUSTOM {
        @Override
        public Method find(Class<?> clazz, Configuration config) {
            Method lowestParamMethod = null;
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(config.getMainMethod())) {
                    if (lowestParamMethod == null || m.getParameterCount() < lowestParamMethod.getParameterCount())
                        lowestParamMethod = m;
                }
            }
            if (lowestParamMethod != null)
                return lowestParamMethod;
            throw new IllegalArgumentException("No such method '"+config.getMainMethod()+"' in class "+clazz.getName());
        }
    },
    /**
     * Will find a method whose signature matches main(String[])
     */
    MAIN {
        @Override
        public Method find(Class<?> clazz, Configuration config) {
            try {
                return clazz.getDeclaredMethod("main", String[].class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("No main method in class "+clazz.getName());
            }
        }
    },
    /**
     * Will find a method that is annotated by a certain annotation.
     * Note that the mainMethodAnnotation attribute of the configuration must be set for this
     * to work. That attribute's value should be the fully qualified name of the annotation class.
     * The annotation class cannot itself be a member of the jar that is being injected.
     */
    ANNOTATED {
        @Override
        public Method find(Class<?> clazz, Configuration config) {
            String annotationClass = config.getMainMethodAnnotation();
            for (Method m : clazz.getDeclaredMethods()) {
                for (Annotation annotation : m.getAnnotations()) {
                    if (annotation.getClass().getName().equals(annotationClass))
                        return m;
                }
            }
            throw new IllegalArgumentException("No method with annotation: "+annotationClass);
        }
    };

    public abstract Method find(Class<?> clazz, Configuration config);

}
