package de.leftclicka.configuration;

import java.lang.reflect.Method;
import java.net.*;

public enum InjectionMethod {

    DUMPCLASSES {
        @Override
        public void inject(ClassLoader classLoader, Configuration configuration) {
        }
    }, INJECTCLASSPATH {
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
