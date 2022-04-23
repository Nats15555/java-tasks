package ru.mail.polis.homework.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Необходимо реализовать метод reflectiveToString, который для произвольного объекта
 * возвращает его строковое описание в формате:
 *
 * {field_1: value_1, field_2: value_2, ..., field_n: value_n}
 *
 * где field_i - имя поля
 *     value_i - его строковое представление (String.valueOf),
 *               за исключением массивов, для которых value формируется как:
 *               [element_1, element_2, ..., element_m]
 *                 где element_i - строковое представление элемента (String.valueOf)
 *                 элементы должны идти в том же порядке, что и в массиве.
 *
 * Все null'ы следует представлять строкой "null".
 *
 * Порядок полей
 * Сначала следует перечислить в алфавитном порядке поля, объявленные непосредственно в классе объекта,
 * потом в алфавитном порядке поля объявленные в родительском классе и так далее по иерархии наследования.
 * Примеры можно посмотреть в тестах.
 *
 * Какие поля выводить
 * Необходимо включать только нестатические поля. Также нужно пропускать поля, помеченные аннотацией @SkipField
 *
 * Упрощения
 * Чтобы не усложнять задание, предполагаем, что нет циклических ссылок, inner классов, и transient полей
 *
 * Реализация
 * В пакете ru.mail.polis.homework.reflection можно редактировать только этот файл
 * или добавлять новые (не рекомендуется, т.к. решение вполне умещается тут в несколько методов).
 * Редактировать остальные файлы нельзя.
 *
 * Баллы
 * В задании 3 уровня сложности, для каждого свой набор тестов:
 *   Easy - простой класс, нет наследования, массивов, статических полей, аннотации SkipField (4 балла)
 *   Easy + Medium - нет наследования, массивов, но есть статические поля и поля с аннотацией SkipField (6 баллов)
 *   Easy + Medium + Hard - нужно реализовать все требования задания (10 баллов)
 *
 * Итого, по заданию можно набрать 10 баллов
 * Баллы могут снижаться за неэффективный или неаккуратный код
 */
public class ReflectionToStringHelper {

    public static String reflectiveToString(Object object) {
        if (object == null) {
            return "null";
        }

        StringBuilder objectToString = new StringBuilder();
        objectToString.append("{");

        Class<?> objectClass = object.getClass();
        while (objectClass != null) {
            Arrays.stream(objectClass.getDeclaredFields())
                    .sorted(Comparator.comparing(Field::getName))
                    .forEach(field -> {
                        String currentField = parseField(object, field);

                        if (currentField != null) {
                            objectToString.append(currentField).append(", ");
                        }
                    });

            objectClass = objectClass.getSuperclass();
        }

        if (objectToString.length() == 1) {
            return objectToString.append("}").toString();
        }

        return objectToString.delete(objectToString.length() - 2, objectToString.length()).append("}").toString();
    }

    private static String parseField(Object object, Field field) {
        if (Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(SkipField.class)) {
            return null;
        }

        StringBuilder currentField = new StringBuilder();
        currentField.append(field.getName()).append(": ");

        if (!field.canAccess(object)) {
            field.setAccessible(true);
        }

        Object value = null;
        try {
            value = field.get(object);
        } catch (IllegalAccessException ignored) {
        }

        return (value == null || !value.getClass().isArray()) ? currentField.append(value).toString()
                : currentField.append(parseArray(value)).toString();
    }

    private static String parseArray(Object value) {
        if (Array.getLength(value) == 0) {
            return "[]";
        }

        StringBuilder arrayToString = new StringBuilder();
        arrayToString.append("[");
        for (int i = 0; i < Array.getLength(value); i++) {
            arrayToString.append(Array.get(value, i));
            arrayToString.append(", ");
        }
        arrayToString.delete(arrayToString.length() - 2, arrayToString.length());
        arrayToString.append("]");

        return arrayToString.toString();
    }

}
