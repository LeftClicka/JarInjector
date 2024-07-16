package de.leftclicka.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class Configuration {

    private String jarPath;
    private ClassLoaderPolicy classLoaderPolicy;
    private String classLoaderClass;
    private MainClassPolicy mainClassPolicy;
    private String customMainClass;
    private String mainClassAnnotation;
    private MainMethodPolicy mainMethodPolicy;
    private String customMainMethod;
    private String mainMethodAnnotation;
    private InjectionMethod injectionMethod;


    public Configuration setJarPath(String path) {
        jarPath = path;
        return this;
    }

    public Configuration setClassLoaderPolicy(ClassLoaderPolicy policy) {
        classLoaderPolicy = policy;
        return this;
    }

    public Configuration setClassLoaderClass(String clazz) {
        classLoaderClass = clazz;
        return this;
    }

    public Configuration setMainClassPolicy(MainClassPolicy policy) {
        mainClassPolicy = policy;
        return this;
    }

    public Configuration setMainClass(String clazz) {
        customMainClass = clazz;
        return this;
    }

    public Configuration setMainClassAnnotation(String annotation) {
        mainClassAnnotation = annotation;
        return this;
    }

    public Configuration setMainMethodPolicy(MainMethodPolicy policy) {
        mainMethodPolicy = policy;
        return this;
    }

    public Configuration setMainMethod(String methodName) {
        customMainMethod = methodName;
        return this;
    }

    public Configuration setMainMethodAnnotation(String annotation) {
        mainMethodAnnotation = annotation;
        return this;
    }

    public Configuration setInjectionMethod(InjectionMethod injectionMethod) {
        this.injectionMethod = injectionMethod;
        return this;
    }

    public String getJarPath() {
        return jarPath;
    }

    public ClassLoaderPolicy getClassLoaderPolicy() {
        return classLoaderPolicy;
    }

    public String getClassLoaderClass() {
        return classLoaderClass;
    }

    public MainClassPolicy getMainClassPolicy() {
        return mainClassPolicy;
    }

    public String getMainClass() {
        return customMainClass;
    }

    public String getMainMethod() {
        return customMainMethod;
    }

    public InjectionMethod getInjectionMethod() {
        return injectionMethod;
    }

    public String getMainClassAnnotation() {
        return mainClassAnnotation;
    }

    public MainMethodPolicy getMainMethodPolicy() {
        return mainMethodPolicy;
    }

    public String getMainMethodAnnotation() {
        return mainMethodAnnotation;
    }

    @Override
    public String toString() {
        return encode();
    }

    public String encode() {
        StringBuilder builder = new StringBuilder();
        try {
            for (Field field : Configuration.class.getDeclaredFields()) {
                //just making sure
                field.setAccessible(true);
                Object value = field.get(this);
                if (value == null)
                    continue;
                builder.append(field.getName()).append(":=").append(value).append(";");
            }
        } catch (ReflectiveOperationException ex) {
            //this should never actually throw
            throw new RuntimeException(ex);
        }
        return builder.toString();
    }


    public static Configuration decode(String data) {
        Map<String, String> values = new HashMap<>();
        for (String kvPair : data.split(";")) {
            String[] entry = kvPair.split(":=");
            values.put(entry[0], entry[1]);
        }
        Configuration configuration = new Configuration();
        try {
            for (Field field : Configuration.class.getDeclaredFields()) {
                //just making sure
                field.setAccessible(true);
                String name = field.getName();
                String valueStr = values.get(name);
                if (valueStr == null)
                    continue;
                Object value;
                if (Enum.class.isAssignableFrom(field.getType())) {
                    //invoke reflectively to get around weird type restrictions
                    Method valueOf = Enum.class.getDeclaredMethod("valueOf", Class.class, String.class);
                    value = valueOf.invoke(null, field.getType(), valueStr);
                } else {
                    value = valueStr;
                }
                field.set(configuration, value);
            }
        } catch (ReflectiveOperationException ex) {
            //also never throws
            throw new RuntimeException(ex);
        }
        return configuration;
    }

    /**
     * A collection of configuration presets
     */
    public static class Presets {

        /**
         * Config for most normal target programs
         */
        public static final Configuration DEFAULT = new Configuration()
                .setInjectionMethod(InjectionMethod.INJECTCLASSPATH)
                .setClassLoaderPolicy(ClassLoaderPolicy.RECOMMENDED)
                .setMainClassPolicy(MainClassPolicy.MANIFEST)
                .setMainMethodPolicy(MainMethodPolicy.MAIN);

        /**
         * Config for 1.7.10 forge with class dumping
         */
        public static final Configuration MINECRAFT_FORGE_1_7_10_1 = new Configuration()
                .setInjectionMethod(InjectionMethod.DUMPCLASSES)
                .setClassLoaderPolicy(ClassLoaderPolicy.CUSTOM)
                .setClassLoaderClass("net.minecraft.client.Minecraft")
                .setMainClassPolicy(MainClassPolicy.ANNOTATED)
                .setMainClassAnnotation("cpw.mods.fml.common.Mod")
                .setMainMethodPolicy(MainMethodPolicy.ANNOTATED)
                .setMainMethodAnnotation("cpw.mods.fml.common.Mod$EventHandler");

        /**
         * Config for 1.7.10 forge with class path injection
         */
        public static final Configuration MINECRAFT_FORGE_1_7_10_2 = new Configuration()
                .setInjectionMethod(InjectionMethod.INJECTCLASSPATH)
                .setClassLoaderPolicy(ClassLoaderPolicy.CUSTOM)
                .setClassLoaderClass("net.minecraft.client.Minecraft")
                .setMainClassPolicy(MainClassPolicy.ANNOTATED)
                .setMainClassAnnotation("cpw.mods.fml.common.Mod")
                .setMainMethodPolicy(MainMethodPolicy.ANNOTATED)
                .setMainMethodAnnotation("cpw.mods.fml.common.Mod$EventHandler");

        private Presets(){}

    }

}
