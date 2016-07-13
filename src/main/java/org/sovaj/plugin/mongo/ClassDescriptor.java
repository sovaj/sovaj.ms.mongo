package org.sovaj.plugin.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 *
 * @author Mickael Dubois
 */
public class ClassDescriptor {

    private static void describe(Class<?> clazz, String pad, String leadin, final List<String> classesToStop, final List<String> packagesToStop) {
        if (clazz == null) {
            return;
        }
        String type
                = clazz.isInterface() ? "interface"
                        : clazz.isArray() ? "array"
                                : clazz.isPrimitive() ? "primitive"
                                        : clazz.isEnum() ? "enum"
                                                : "class";
        System.out.printf("%s%s%s %s ( %s )%n",
                pad, leadin, type, clazz.getSimpleName(), clazz.getName());
        for (Class<?> interfaze : clazz.getInterfaces()) {
            if (!isPackageIn(interfaze, packagesToStop)) {
                describe(interfaze, pad + "   ", "implements ", classesToStop, packagesToStop);
            }
        }
        describe(clazz.getComponentType(), pad + "   ", "elements are ", classesToStop, packagesToStop);

        Type types = clazz.getGenericSuperclass();

        if (clazz.getSuperclass() != null
                && !isPackageIn(clazz.getSuperclass(), packagesToStop)
                && !classesToStop.contains(clazz.getSuperclass().getSimpleName())) {
            describe(clazz.getSuperclass(), pad + "   ", "extends ", classesToStop, packagesToStop);
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (type.equals("class")
                    && !isPackageIn(clazz, packagesToStop)
                    && !classesToStop.contains(clazz.getSimpleName())) {
                describe(field.getType(), pad + "   ", "encapsulate " + field.getName() + " ", classesToStop, packagesToStop);
                if (field.getGenericType() instanceof ParameterizedTypeImpl) {
                    Class parametrizedClass = (Class) ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0];
                    describe(parametrizedClass, pad + "   ", "of type ", classesToStop, packagesToStop);
                }
            }
        }
    }

    private static boolean isPackageIn(Class<?> interfaze, final List<String> packagesToStop) {
        return packagesToStop.contains(interfaze.getPackage().getName());
    }

    static void describe(Class<?> clazz, final String[] pStopAtClassName, final String[] pStopAtPackage) {
        if (pStopAtClassName == null) {
            throw new IllegalArgumentException("pStopAtClassName should not be null");
        }
        if (pStopAtPackage == null) {
            throw new IllegalArgumentException("pStopAtPackage should not be null");
        }
        List<String> lStopAtClassName = Arrays.asList(pStopAtClassName);
        List<String> lStopAtPackage = new ArrayList<>(Arrays.asList(pStopAtPackage));
        lStopAtPackage.add("java.lang");
        lStopAtPackage.add("java.io");
        lStopAtPackage.add("java.util");
        describe(clazz, "", "", lStopAtClassName, lStopAtPackage);
    }

    public static void main(String[] args) {
//        describe(EventTypeRule.class, new String[]{"BusinessObject"}, new String[0]);
    }
}
