package de.leftclicka.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public enum MainMethodPolicy {

    CUSTOM {
        @Override
        public Method find(Class<?> clazz, Configuration config) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(config.getMainMethod()))
                    return m;
            }
            throw new IllegalArgumentException("No such method '"+config.getMainMethod()+"' in class "+clazz.getName());
        }
    }, MAIN {
        @Override
        public Method find(Class<?> clazz, Configuration config) {
            try {
                return clazz.getDeclaredMethod("main", String[].class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("No main method in class "+clazz.getName());
            }
        }
    }, ANNOTATED {
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
