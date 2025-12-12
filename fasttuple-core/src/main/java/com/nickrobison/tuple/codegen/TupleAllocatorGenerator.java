package com.nickrobison.tuple.codegen;

import java.util.Collections;
import java.util.Objects;

import com.nickrobison.tuple.FastTuple;

import org.codehaus.commons.compiler.InternalCompilerException;
import org.codehaus.commons.compiler.Location;
import org.codehaus.janino.Java;
import org.codehaus.janino.SimpleCompiler;

import static com.nickrobison.tuple.codegen.CodegenUtil.emptyParams;

/**
 * Created by cliff on 5/14/14.
 */
public class TupleAllocatorGenerator extends SimpleCompiler {
    private static final String packageName = "com.nickrobison.tuple";

    public interface TupleAllocator {
        FastTuple allocate();
    }

    private final Class<?> allocatorClass;

    public TupleAllocatorGenerator(Class<?> tupleClass) throws Exception {
        String className = tupleClass.getName() + "Allocator";
        setParentClassLoader(tupleClass.getClassLoader());
        Java.CompilationUnit cu = new Java.CompilationUnit(null);
        Location loc = new Location(null, (short) 0, (short) 0);
        cu.setPackageDeclaration(new Java.PackageDeclaration(loc, packageName));
        cu.addPackageMemberTypeDeclaration(makeClassDefinition(loc, tupleClass, className));
        cook(cu);
        try {
            allocatorClass = getClassLoader().loadClass(packageName + "." + className);
        } catch (ClassNotFoundException ex) {
            throw new InternalCompilerException(
                    "SNO: Generated compilation unit does not declare class '" + packageName + "." + className + "'",
                    ex);
        }
    }

    public TupleAllocator createAllocator() throws Exception {
        return (TupleAllocator) allocatorClass.getConstructor().newInstance();
    }

    private Java.PackageMemberClassDeclaration makeClassDefinition(Location loc, Class<?> tupleClass,
            String className) {
        Java.PackageMemberClassDeclaration cd = new Java.PackageMemberClassDeclaration(loc, null,
                new Java.AccessModifier[] { new Java.AccessModifier("public", loc) }, className, null, null,
                new Java.Type[] { classToType(loc, TupleAllocator.class) });

        cd.addDeclaredMethod(new Java.MethodDeclarator(loc, null,
                new Java.AccessModifier[] { new Java.AccessModifier("public", loc) }, null,
                classToType(loc, FastTuple.class), "allocate", emptyParams(loc), new Java.Type[0], null,
                Collections.singletonList(new Java.ReturnStatement(loc,
                        new Java.NewClassInstance(loc, null,
                                new Java.ReferenceType(loc, new Java.NormalAnnotation[] {},
                                        tupleClass.getCanonicalName().split("\\."), new Java.TypeArgument[0]),
                                new Java.Rvalue[0])))));

        return cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TupleAllocatorGenerator))
            return false;
        TupleAllocatorGenerator that = (TupleAllocatorGenerator) o;
        return Objects.equals(allocatorClass, that.allocatorClass);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allocatorClass);
    }
}
