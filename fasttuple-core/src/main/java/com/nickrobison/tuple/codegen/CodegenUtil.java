package com.nickrobison.tuple.codegen;

import com.google.common.collect.Lists;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.Location;
import org.codehaus.janino.Java;
import org.codehaus.janino.SimpleCompiler;

/**
 * Created by cliff on 5/14/14.
 */
public final class CodegenUtil {

    public static Java.ConstructorDeclarator nullConstructor(Location loc) {
        return new Java.ConstructorDeclarator(
                loc,
                null,
                new Java.AccessModifier[]{new Java.AccessModifier("public", loc)},
                new Java.FunctionDeclarator.FormalParameters(
                        loc,
                        new Java.FunctionDeclarator.FormalParameter[0],
                        false
                ),
                new Java.Type[0],
                null,
                Lists.<Java.BlockStatement>newArrayList()
        );
    }

    public static Java.FunctionDeclarator.FormalParameters emptyParams(Location loc) {
        return new Java.FunctionDeclarator.FormalParameters(loc, new Java.FunctionDeclarator.FormalParameter[0], false);
    }

//    public static Class<?> compileToClass(Java.CompilationUnit cu) throws CompileException {
//        final SimpleCompiler simpleCompiler = new SimpleCompiler();
//        simpleCompiler.cook(cu);
//
//        final ClassLoader cl = simpleCompiler.getClassLoader();
//
////        ClassLoader cl = simpleCompiler.compileToClassLoader(compilationUnit);
//
//                // Find the generated class by name.
//                try {
//                    return cl.loadClass(cu.fileName);
//                } catch (ClassNotFoundException ex) {
//                    throw new RuntimeException((
//                        "SNO: Generated compilation unit does not declare class '"
//                        + cu.fileName
//                        + "'"
//                    ), ex);
//                }
//    }
}
