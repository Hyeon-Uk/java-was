package codesquad.framework.coffee;


import codesquad.framework.coffee.annotation.Barista;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Named;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CoffeeShop {
    private final Map<Class<?>, List<Object>> container = new HashMap<>();

    public CoffeeShop() throws Exception {
        this("");
    }

    public CoffeeShop(String basePackage) throws Exception {
        List<Class<?>> components = scanComponents(basePackage);
        makeDependency(components);
    }

    private List<Class<?>> scanComponents(String basePackage) {
        try {
            List<Class<?>> clazzes = new LinkedList<>();
            String path = basePackage.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.getFile());
                scanDirectory(clazzes, file, basePackage);
            }
            return clazzes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan and register components", e);
        }
    }

    private void scanDirectory(List<Class<?>> clazzes, File directory, String packageName) throws Exception {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    scanDirectory(clazzes, file, packageName + (packageName.isEmpty() ? "" : ".") + file.getName());
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + (packageName.isEmpty() ? "" : ".") + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Coffee.class)) {
                        clazzes.add(clazz);
                    }
                }
            }
        }
    }

    private void registerBean(Class<?> beanClass) throws Exception {
        Constructor<?> constructor = getAppropriateConstructor(beanClass);
        Object[] args = resolveDependencies(constructor);
        Object instance = constructor.newInstance(args);
        container.computeIfAbsent(beanClass, k -> new ArrayList<>()).add(instance);

//        // Register for interfaces and superclasses
//        for (Class<?> iface : beanClass.getInterfaces()) {
//            container.computeIfAbsent(iface, k -> new ArrayList<>()).add(instance);
//        }
//        Class<?> superClass = beanClass.getSuperclass();
//        while (superClass != null && superClass != Object.class) {
//            container.computeIfAbsent(superClass, k -> new ArrayList<>()).add(instance);
//            superClass = superClass.getSuperclass();
//        }
    }

    private void getClassTypeRecursive(Set<Class<?>> result, Class<?> clazz) {
        if (clazz == null || Object.class.equals(clazz)) return;

        result.add(clazz);

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !Object.class.equals(superclass)) {
            getClassTypeRecursive(result, superclass);
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> itf : interfaces) {
            getClassTypeRecursive(result, itf);
        }
    }

    private void makeDependency(List<Class<?>> classes) throws Exception {
        //setup
        Map<Class<?>, Integer> seqMap = new HashMap<>();
        int seq = 0;
        Set<Class<?>> requiredTypes = new HashSet<>();
        for (Class<?> clazz : classes) {
            getClassTypeRecursive(requiredTypes, clazz);
        }
        List<Class<?>> required = requiredTypes.stream().toList();

        List<Integer>[] graph = new List[requiredTypes.size()];
        int[] indeg = new int[requiredTypes.size()];
        for (Class<?> clazz : requiredTypes) {
            graph[seq] = new ArrayList<>();
            seqMap.put(clazz, seq++);
        }

        //make graph
        for (Class<?> clazz : classes) {
            Constructor<?> appropriateConstructor = getAppropriateConstructor(clazz);
            Class<?>[] paramTypes = resolveDependenciesTypes(appropriateConstructor);
            Arrays.stream(paramTypes)
                    .forEach(param -> {
                        int paramSeq = seqMap.get(param);
                        int nowSeq = seqMap.get(clazz);

                        graph[paramSeq].add(nowSeq);
                        indeg[nowSeq]++;
                    });
        }

        //resolveDependency
        Queue<Class<?>> q = new LinkedList<>();
        for (Class<?> clazz : classes) {
            int s = seqMap.get(clazz);
            if (indeg[s] == 0) {
                q.offer(clazz);
            }
        }

        while (!q.isEmpty()) {
            Class<?> clazz = q.poll();
            if(!container.containsKey(clazz)) {
                registerBean(clazz);
            }
            Set<Class<?>> types = new HashSet<>();
            getClassTypeRecursive(types, clazz);
            types.stream()
                    .forEach(t -> {
                        int s = seqMap.get(t);
                        for (int next : graph[s]) {
                            indeg[next]--;
                            if (indeg[next] == 0) {
                                q.offer(required.get(next));
                            }
                        }
                    });
        }
    }

    private Constructor<?> getAppropriateConstructor(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        List<Constructor<?>> autowiredConstructors = Arrays.stream(constructors)
                .filter(c -> c.isAnnotationPresent(Barista.class))
                .toList();

        if (autowiredConstructors.size() > 1) {
            throw new IllegalStateException("Multiple @Autowired constructors found for " + beanClass.getName());
        }

        return autowiredConstructors.isEmpty() ? constructors[0] : autowiredConstructors.get(0);
    }

    private Class<?>[] resolveDependenciesTypes(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        return Arrays.stream(parameters)
                .map(Parameter::getType)
                .toArray(Class<?>[]::new);
    }

    private Object[] resolveDependencies(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();
            String paramName = getParameterName(parameters[i]);
            args[i] = getBean(paramType, paramName);
            if (args[i] == null) {
                throw new IllegalStateException("No bean found for " + paramType.getName());
            }
        }
        return args;
    }

    private String getParameterName(Parameter parameter) {
        Named named = parameter.getAnnotation(Named.class);
        return named != null ? named.value() : "";
    }

    public <T> T getBean(Class<T> beanClass) {
        List<T> beans = getAllBeansOfType(beanClass);
        if (beans.isEmpty()) {
            throw new IllegalStateException("No bean found of type " + beanClass.getName());
        }
        if (beans.size() > 1) {
            throw new IllegalStateException("Multiple beans found of type " + beanClass.getName());
        }
        return beans.stream().toList().get(0);
    }

    private <T> T getBean(Class<T> beanClass, String name) {
        List<T> beans = getAllBeansOfType(beanClass);
        if (beans.isEmpty()) {
            throw new IllegalStateException("No bean found of type " + beanClass.getName());
        }
        if (beans.size() == 1) {
            return beans.stream().toList().get(0);
        }
        if (!name.isEmpty()) {
            for (T bean : beans) {
                if (bean.getClass().getAnnotation(Coffee.class).name().equalsIgnoreCase(name)) {
                    return bean;
                }
            }
        }
        throw new IllegalStateException("Multiple beans found of type " + beanClass.getName() + " and no matching name found");
    }

    private <T> List<T> getAllBeansOfType(Class<T> beanClass) {
        return container.entrySet().stream()
                .filter(entry -> beanClass.isAssignableFrom(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .map(beanClass::cast)
                .toList();
    }
}