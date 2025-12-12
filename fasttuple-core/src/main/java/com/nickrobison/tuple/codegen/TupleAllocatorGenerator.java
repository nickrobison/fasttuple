package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import org.codehaus.commons.compiler.InternalCompilerException;
import org.codehaus.commons.compiler.Location;
import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Java;
import org.codehaus.janino.SimpleCompiler;

import java.util.Collections;

import static com.nickrobison.tuple.codegen.CodegenUtil.emptyParams;

/**
 * Created by cliff on 5/14/14.
 */
public class TupleAllocatorGenerator {
    private static final String packageName = "com.nickrobison.tuple";

    public interface TupleAllocator {
        FastTuple allocate();
    }

    private final Class<?> allocatorClass;
    private final SimpleCompilerWrapper sc = new SimpleCompilerWrapper();

    private static class SimpleCompilerWrapper extends SimpleCompiler {
        public Java.Type classToTypePublic(Location location, Class<?> clazz) {
            return classToType(location, clazz);
        }
    }

    public TupleAllocatorGenerator(Class<?> tupleClass) throws Exception {
        String className = tupleClass.getName() + "Allocator";
        sc.setParentClassLoader(tupleClass.getClassLoader());
        Java.CompilationUnit cu = new Java.CompilationUnit(null);
        Location loc = new Location(null, (short) 0, (short) 0);
        cu.setPackageDeclaration(new Java.PackageDeclaration(loc, packageName));
        cu.addPackageMemberTypeDeclaration(makeClassDefinition(loc, tupleClass, className));
        sc.cook(cu);
        try {
            allocatorClass = sc.getClassLoader().loadClass(packageName + "." + className);
        } catch (ClassNotFoundException ex) {
            throw new InternalCompilerException(
                "SNO: Generated compilation unit does not declare class '" + packageName + "." + className + "'",
                ex
            );
        }
    }

    public TupleAllocator createAllocator() throws Exception {
        return (TupleAllocator) allocatorClass.getConstructor().newInstance();
    }

    private Java.PackageMemberClassDeclaration makeClassDefinition(Location loc, Class<?> tupleClass, String className) {
        Java.PackageMemberClassDeclaration cd = new Java.PackageMemberClassDeclaration(
                loc,
                null,
                new Java.AccessModifier[]{new Java.AccessModifier("public", loc)},
                className,
                null,
                null,
                new Java.Type[]{
                        sc.classToTypePublic(loc, TupleAllocator.class)
                });

        cd.addDeclaredMethod(new Java.MethodDeclarator(
                loc,
                null,
                new Java.AccessModifier[]{new Java.AccessModifier("public", loc)},
                null,
                sc.classToTypePublic(loc, FastTuple.class),
                "allocate",
                emptyParams(loc),
                new Java.Type[0],
                null,
                Collections.singletonList(
                        new Java.ReturnStatement(loc,
                                new Java.NewClassInstance(
                                        loc,
                                        null,
                                        new Java.ReferenceType(loc, new Java.NormalAnnotation[]{}, tupleClass.getCanonicalName().split("\\."), new Java.TypeArgument[0]),
                                        new Java.Rvalue[0]))
                )
        ));

        return cd;
    }
}
