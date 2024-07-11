package codesquad.framework.coffee;


import codesquad.framework.coffee.annotation.Barista;
import codesquad.framework.coffee.annotation.Coffee;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

public class CoffeeShop {
    private final Map<Class<?>, Object> container = new HashMap<>();
    private final Set<Class<? extends Annotation>> components = Set.of(Coffee.class);

    public CoffeeShop() {
        this("");
    }

    public CoffeeShop(String basePackage) {
        scanAndRegister(basePackage);
    }

    private void scanAndRegister(String basePackage) {
        try {
            String path = basePackage.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.getFile());
                scanDirectory(file, basePackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanDirectory(File directory, String packageName) {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                String packagePath = packageName.isEmpty() ? "" : packageName + ".";
                if (file.isDirectory()) {
                    scanDirectory(file, packagePath + file.getName());
                } else if (file.getName().endsWith(".class")) {
                    String className = packagePath + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (isComponent(clazz) && !container.containsKey(clazz)) {
                            registerBean(clazz);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean isComponent(Class<?> clazz) {
        return components.stream().anyMatch(clazz::isAnnotationPresent);
    }

    private void registerBean(Class<?> beanClass) throws Exception {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        Constructor<?> constructorToUse;

        if (constructors.length == 1) {
            constructorToUse = constructors[0];
        } else {
            constructorToUse = Arrays.stream(constructors)
                    .filter(c -> c.isAnnotationPresent(Barista.class))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No @Autowired constructor found for " + beanClass.getName()));
        }

        Object[] args = resolveDependencies(constructorToUse);
        Object instance = constructorToUse.newInstance(args);

        container.put(beanClass, instance);

        // Register for interfaces
        for (Class<?> iface : beanClass.getInterfaces()) {
            container.putIfAbsent(iface, instance);
        }
    }

    private Object[] resolveDependencies(Constructor<?> constructor) throws Exception {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();
            args[i] = getBean(paramType);
            if (args[i] == null) {
                registerBean(paramType);
                args[i] = getBean(paramType);
                System.out.println(paramType + " " + System.identityHashCode(args[i]));
            }
        }
        return args;
    }

    private Object getDependency(Class<?> dependencyType) {
        return container.entrySet().stream()
                .filter(entry -> dependencyType.isAssignableFrom(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public <T> T getBean(Class<T> beanClass) {
        return beanClass.cast(container.get(beanClass));
    }
}