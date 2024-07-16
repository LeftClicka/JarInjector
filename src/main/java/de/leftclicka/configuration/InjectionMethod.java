package de.leftclicka.configuration;

import java.lang.reflect.Method;
import java.net.*;

/**
 * Dictates the injection method that should be used.
 */
public enum InjectionMethod {

    /**
     * Will extract the class data from the target jar and manually load those classes on the class loader.
     * The target jar can be deleted after injection.
     * The program will crash if injected classes try to access resources from the target jar at runtime.
     */
    DUMPCLASSES {
        @Override
        public void inject(ClassLoader classLoader, Configuration configuration) {
        }
    },
    /**
     * Will append the target jar to the class loader.
     * Note that this will only work if the class loader is a url class loader.
     * The target jar cannot be deleted until the target program exits.
     */
    INJECTCLASSPATH {
        @Override
        public void inject(ClassLoader classLoader, Configuration configuration) {
            if (classLoader instanceof URLClassLoader) {
                try {
                    Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    addUrl.setAccessible(true);
                    URL url = new URI(configuration.getJarPath()).toURL();
                    addUrl.invoke(classLoader, url);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("If this threw something is messed up", e);
                } catch (MalformedURLException e) {
                    throw new RuntimeException("If this threw something is messed up", e);
                } catch (URISyntaxException e) {
                    throw new RuntimeException("If this threw something is messed up", e);
                }
            } else throw new IllegalStateException("Classpath injection is only available if the used class loader is a url class loader.");
        }
    };

    public abstract void inject(ClassLoader classLoader, Configuration configuration);

}
