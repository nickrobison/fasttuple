package com.nickrobison.tuple.codegen;

import org.codehaus.commons.compiler.Location;
import org.codehaus.janino.Java;

import java.util.Collections;

/**
 * Created by cliff on 5/14/14.
 */
public final class CodegenUtil {

    private CodegenUtil() {
        // Not used
    }

    public static final String PUBLIC = "public";

    public static Java.ConstructorDeclarator nullConstructor(Location loc) {
        return new Java.ConstructorDeclarator(
                loc,
                null,
                new Java.AccessModifier[]{new Java.AccessModifier(PUBLIC, loc)},
                new Java.FunctionDeclarator.FormalParameters(
                        loc,
                        new Java.FunctionDeclarator.FormalParameter[0],
                        false
                ),
                new Java.Type[0],
                null,
                Collections.emptyList()
        );
    }

    public static Java.FunctionDeclarator.FormalParameters emptyParams(Location loc) {
        return new Java.FunctionDeclarator.FormalParameters(loc, new Java.FunctionDeclarator.FormalParameter[0], false);
    }
}

final class TypeMapping {

    private TypeMapping() {
    }

    static Java.Primitive toPrimitive(Class<?> type) {
        if (type.equals(Long.TYPE)) return Java.Primitive.LONG;
        if (type.equals(Integer.TYPE)) return Java.Primitive.INT;
        if (type.equals(Short.TYPE)) return Java.Primitive.SHORT;
        if (type.equals(Character.TYPE)) return Java.Primitive.CHAR;
        if (type.equals(Byte.TYPE)) return Java.Primitive.BYTE;
        if (type.equals(Float.TYPE)) return Java.Primitive.FLOAT;
        if (type.equals(Double.TYPE)) return Java.Primitive.DOUBLE;
        return Java.Primitive.VOID;
    }

    static String toBoxedName(Class<?> type) {
        if (type.equals(Long.TYPE)) return "Long";
        if (type.equals(Integer.TYPE)) return "Integer";
        if (type.equals(Short.TYPE)) return "Short";
        if (type.equals(Character.TYPE)) return "Character";
        if (type.equals(Byte.TYPE)) return "Byte";
        if (type.equals(Float.TYPE)) return "Float";
        if (type.equals(Double.TYPE)) return "Double";
        throw new IllegalArgumentException(String.format("Unsupported type: %s", type.getSimpleName()));
    }

    static String toAccessorName(Class<?> type) {
        if (type.equals(Byte.TYPE)) return "Byte";
        if (type.equals(Character.TYPE)) return "Char";
        if (type.equals(Short.TYPE)) return "Short";
        if (type.equals(Integer.TYPE)) return "Int";
        if (type.equals(Float.TYPE)) return "Float";
        if (type.equals(Double.TYPE)) return "Double";
        if (type.equals(Long.TYPE)) return "Long";
        throw new IllegalArgumentException(String.format("Unsupported type: %s", type.getSimpleName()));
    }
}
