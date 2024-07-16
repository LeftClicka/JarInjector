package de.leftclicka.configuration;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public enum MainClassPolicy {

    MANIFEST {
        @Override
        public Class<?> find(ClassLoader classLoader, Configuration configuration) {
            try {
                JarFile targetJar = new JarFile(configuration.getJarPath());
                Manifest manifest = targetJar.getManifest();
                Attributes attributes = manifest.getMainAttributes();
                String mainClassName = attributes.getValue(Attributes.Name.MAIN_CLASS);
                targetJar.close();
                return classLoader.loadClass(mainClassName);
            } catch (IOException e) {
                throw new RuntimeException("If this threw something is messed up", e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("If this threw something is messed up", e);
            }
        }
    }, CUSTOM {
        @Override
        public Class<?> find(ClassLoader classLoader, Configuration configuration) {
            try {
                return classLoader.loadClass(configuration.getMainClass());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("No such class: "+configuration.getMainClass(), e);
            }
        }
    }, ANNOTATED {
        @Override
        public Class<?> find(ClassLoader classLoader, Configuration configuration) {
            String annotationClass = configuration.getMainClassAnnotation();
            for (Class<?> clazz : MainClassPolicy.getClasses(configuration.getJarPath())) {
                for (Annotation annotation : clazz.getAnnotations()) {
                    if (annotation.getClass().getName().equals(annotationClass)) {
                        try {
                            classLoader.loadClass(clazz.getName());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            throw new IllegalArgumentException("No class with annotation: "+annotationClass);
        }
    };

    public abstract Class<?> find(ClassLoader classLoader, Configuration configuration);

    private static Class<?>[] getClasses(String jarPath) {
        try {
            JarFile targetJar = new JarFile(jarPath);
            List<Class<?>> classes = new ArrayList<>();
            URLClassLoader dummyClassLoader = new URLClassLoader(new URL[0]);
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            for (ZipEntry entry : targetJar.stream().collect(Collectors.toList())) {
                if (!entry.getName().endsWith(".class")) {
                    continue;
                }
                InputStream stream = targetJar.getInputStream(entry);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read;
                while ((read = stream.read(buffer)) != -1)
                    outputStream.write(buffer, 0, read);
                byte[] clazzData = outputStream.toByteArray();
                classes.add((Class<?>) defineClass.invoke(dummyClassLoader, clazzData, 0, clazzData.length));
                stream.close();
            }
            targetJar.close();
            dummyClassLoader.close();
            return (Class<?>[]) classes.toArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
