package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import com.nickrobison.tuple.TupleSchema;
import org.codehaus.commons.compiler.InternalCompilerException;
import org.codehaus.commons.compiler.Location;
import org.codehaus.janino.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.nickrobison.tuple.codegen.CodegenUtil.*;

/**
 * Created by cliff on 5/12/14.
 */
public class TupleExpressionGenerator extends SimpleCompiler {
    public interface TupleExpression {
        void evaluate(FastTuple tuple);
    }

    public interface ObjectTupleExpression {
        Object evaluate(FastTuple tuple);
    }

    public interface LongTupleExpression {
        long evaluate(FastTuple tuple);
    }

    public interface IntTupleExpression {
        int evaluate(FastTuple tuple);
    }

    public interface ShortTupleExpression {
        short evaluate(FastTuple tuple);
    }

    public interface CharTupleExpression {
        char evaluate(FastTuple tuple);
    }

    public interface ByteTupleExpression {
        byte evaluate(FastTuple tuple);
    }

    public interface FloatTupleExpression {
        float evaluate(FastTuple tuple);
    }

    public interface DoubleTupleExpression {
        double evaluate(FastTuple tuple);
    }

    public interface BooleanTupleExpression {
        boolean evaluate(FastTuple tuple);
    }

    private static final String packageName = "com.nickrobison.tuple";
    private static final AtomicLong counter = new AtomicLong(0);
    private final String expression;
    private final TupleSchema schema;
    private Class<?> evaluatorClass;
    private Object evaluator;
    private final Class<?> iface;
    private final Class<?> returnType;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String expression = null;
        private TupleSchema schema = null;

        public Builder() {}

        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        public Builder schema(TupleSchema schema) {
            this.schema = schema;
            return this;
        }

        public TupleExpression returnVoid() throws Exception {
            return (TupleExpression) new TupleExpressionGenerator(schema, expression, TupleExpression.class, Void.TYPE).evaluator();
        }

        public ObjectTupleExpression returnObject() throws Exception {
            return (ObjectTupleExpression) new TupleExpressionGenerator(schema, expression, ObjectTupleExpression.class, Object.class).evaluator();
        }

        public LongTupleExpression returnLong() throws Exception {
            return (LongTupleExpression) new TupleExpressionGenerator(schema, expression, LongTupleExpression.class, Long.TYPE).evaluator();
        }

        public IntTupleExpression returnInt() throws Exception {
            return (IntTupleExpression) new TupleExpressionGenerator(schema, expression, IntTupleExpression.class, Integer.TYPE).evaluator();
        }

        public ShortTupleExpression returnShort() throws Exception {
            return (ShortTupleExpression) new TupleExpressionGenerator(schema, expression, ShortTupleExpression.class, Short.TYPE).evaluator();
        }

        public CharTupleExpression returnChar() throws Exception {
            return (CharTupleExpression) new TupleExpressionGenerator(schema, expression, CharTupleExpression.class, Character.TYPE).evaluator();
        }

        public ByteTupleExpression returnByte() throws Exception {
            return (ByteTupleExpression) new TupleExpressionGenerator(schema, expression, ByteTupleExpression.class, Byte.TYPE).evaluator();
        }

        public FloatTupleExpression returnFloat() throws Exception {
            return (FloatTupleExpression) new TupleExpressionGenerator(schema, expression, FloatTupleExpression.class, Float.TYPE).evaluator();
        }

        public DoubleTupleExpression returnDouble() throws Exception {
            return (DoubleTupleExpression) new TupleExpressionGenerator(schema, expression, DoubleTupleExpression.class, Double.TYPE).evaluator();
        }

        public BooleanTupleExpression returnBoolean() throws Exception {
            return (BooleanTupleExpression) new TupleExpressionGenerator(schema, expression, BooleanTupleExpression.class, Boolean.TYPE).evaluator();
        }

    }

    private TupleExpressionGenerator(TupleSchema schema, String expression, Class<?> iface, Class<?> returnType) throws Exception {
        this.schema = schema;
        this.expression = expression;
        this.iface = iface;
        this.returnType = returnType;
        setParentClassLoader(schema.getClassLoader());
        generateEvaluatorClass();
    }

    private void generateEvaluatorClass() throws Exception {
        Scanner scanner = new Scanner(null, new StringReader(expression));
        Parser parser = new Parser(scanner);
        Location loc = parser.location();
        String className = "TupleExpression" + counter.incrementAndGet();
        Java.CompilationUnit cu = new Java.CompilationUnit(null);
        cu.setPackageDeclaration(new Java.PackageDeclaration(loc, packageName));
        Java.PackageMemberClassDeclaration cd = new Java.PackageMemberClassDeclaration(loc,
                null,
                new Java.AccessModifier[]{new Java.AccessModifier(PUBLIC, loc)},
                className,
                null,
                null,
                new Java.Type[] {
                        classToType(loc, iface)
                }
        );
        cu.addPackageMemberTypeDeclaration(cd);
        cd.addDeclaredMethod(generateFrontendMethod(loc));
        cd.addDeclaredMethod(generateBackendMethod(parser));
        cook(cu);
        try {
            this.evaluatorClass = getClassLoader().loadClass(packageName + "." + className);
        } catch (ClassNotFoundException ex) {
            throw new InternalCompilerException(
                "SNO: Generated compilation unit does not declare class '" + packageName + "." + className + "'",
                ex
            );
        }
        this.evaluator = evaluatorClass.getConstructor().newInstance();
    }

    private Java.MethodDeclarator generateFrontendMethod(Location loc) throws Exception {
        return new Java.MethodDeclarator(loc,
                null,
                new Java.AccessModifier[]{new Java.AccessModifier(PUBLIC, loc)},
                null,
                classToType(loc, returnType),
                "evaluate",
                generateArgs(loc, FastTuple.class),
                new Java.Type[0],
                null,
                Collections.singletonList(
                        maybeGenerateReturn(loc,
                                new Java.MethodInvocation(
                                        loc,
                                        null,
                                        "doEval",
                                        new Java.Rvalue[]{
                                                new Java.Cast(
                                                        loc,
                                                        new Java.ReferenceType(loc, new Java.NormalAnnotation[]{}, schema.tupleClass().getCanonicalName().split("\\."), null),
                                                        new Java.AmbiguousName(loc, new String[]{"tuple"})
                                                )
                                        }
                                )
                        )
                )
        );
    }

    private Java.MethodDeclarator generateBackendMethod(Parser parser) throws Exception {
        Location loc = parser.location();
        List<Java.BlockStatement> statements = new ArrayList<>();
        Java.Rvalue[] exprs = parser.parseExpressionList();
        for (int i=0; i<exprs.length; i++) {
            if (i == exprs.length - 1) {
                statements.add(maybeGenerateReturn(loc, exprs[i]));
            } else {
                statements.add(new Java.ExpressionStatement(exprs[i]));
            }
        }

        return new Java.MethodDeclarator(loc,
                null,
                new Java.AccessModifier[]{new Java.AccessModifier(PUBLIC, loc)},
                null,
                classToType(loc, returnType),
                "doEval",
                generateArgs(loc, schema.tupleClass()),
                new Java.Type[0],
                null,
                statements
        );
    }

    private Java.BlockStatement maybeGenerateReturn(Location loc, Java.Rvalue statement) throws Exception {
        if (returnType.equals(Void.TYPE)) {
            return new Java.ExpressionStatement(statement);
        } else {
            return new Java.ReturnStatement(loc, statement);
        }
    }

    private Java.FunctionDeclarator.FormalParameters generateArgs(Location loc, Class<?> c) {
        return new Java.FunctionDeclarator.FormalParameters(
                loc,
                new Java.FunctionDeclarator.FormalParameter[] {
                        new Java.FunctionDeclarator.FormalParameter(
                                loc,
                                new Java.AccessModifier[]{new Java.AccessModifier(PUBLIC, loc)},
                                classToType(loc, c),
                                "tuple"
                        )
                },
                false
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TupleExpressionGenerator)) return false;
        if (!super.equals(o)) return false;
        TupleExpressionGenerator that = (TupleExpressionGenerator) o;
        return Objects.equals(expression, that.expression) && Objects.equals(schema, that.schema) && Objects.equals(evaluatorClass, that.evaluatorClass) && Objects.equals(evaluator, that.evaluator) && Objects.equals(iface, that.iface) && Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression, schema, evaluatorClass, evaluator, iface, returnType);
    }

    public Object evaluator() {
        return evaluator;
    }
}
