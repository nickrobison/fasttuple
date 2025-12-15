package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import com.nickrobison.tuple.TupleSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for TupleExpressionGenerator to kill all mutations
 * Focus on error conditions, edge cases, and all return types
 */
class TupleExpressionGeneratorMutationTest {

    @Test
    void testExpressionWithNullSchema() throws Exception {
        assertThrows(Exception.class, () ->
                TupleExpressionGenerator.builder()
                        .expression("tuple.x()")
                        .schema(null)
                        .returnInt()
        );
    }

    @Test
    void testExpressionWithEmptyExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .heapMemory()
                .build();

        assertThrows(Exception.class, () ->
                TupleExpressionGenerator.builder()
                        .expression("")
                        .schema(schema)
                        .returnInt()
        );
    }

    @Test
    void testMalformedExpressionThrowsException() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .heapMemory()
                .build();

        assertThrows(Exception.class, () ->
                TupleExpressionGenerator.builder()
                        .expression("this is not valid java!!!")
                        .schema(schema)
                        .returnInt()
        );
    }

    @Test
    void testInvalidFieldReferenceThrowsException() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .heapMemory()
                .build();

        assertThrows(Exception.class, () ->
                TupleExpressionGenerator.builder()
                        .expression("tuple.nonExistentField()")
                        .schema(schema)
                        .returnInt()
        );
    }

    @Test
    void testVoidExpressionWithMultipleStatements() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Integer.TYPE)
                .addField("b", Integer.TYPE)
                .addField("c", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();

        TupleExpressionGenerator.TupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.a(10), tuple.b(20), tuple.c(30)")
                .schema(schema)
                .returnVoid();

        expr.evaluate(tuple);

        assertEquals(10, tuple.getInt(1));
        assertEquals(20, tuple.getInt(2));
        assertEquals(30, tuple.getInt(3));
    }

    @Test
    void testObjectExpressionWithArithmetic() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .addField("y", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 15);
        tuple.setInt(2, 25);

        TupleExpressionGenerator.ObjectTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.x() * tuple.y()")
                .schema(schema)
                .returnObject();

        Object result = expr.evaluate(tuple);
        assertEquals(375, result);
    }

    @Test
    void testLongExpressionWithComplexLogic() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setLong(1, 100L);

        TupleExpressionGenerator.LongTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.value() * 2 + 50")
                .schema(schema)
                .returnLong();

        assertEquals(250L, expr.evaluate(tuple));
    }

    @Test
    void testIntExpressionWithConditional() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .addField("y", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 10);
        tuple.setInt(2, 5);

        TupleExpressionGenerator.IntTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.x() > tuple.y() ? tuple.x() : tuple.y()")
                .schema(schema)
                .returnInt();

        assertEquals(10, expr.evaluate(tuple));

        tuple.setInt(1, 3);
        assertEquals(5, expr.evaluate(tuple));
    }

    @Test
    void testShortExpressionWithCast() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("s", Short.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setShort(1, (short) 500);

        TupleExpressionGenerator.ShortTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("(short)(tuple.s() / 2)")
                .schema(schema)
                .returnShort();

        assertEquals((short) 250, expr.evaluate(tuple));
    }

    @Test
    void testCharExpressionWithCharOperations() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("ch", Character.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setChar(1, 'A');

        TupleExpressionGenerator.CharTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.ch()")
                .schema(schema)
                .returnChar();

        assertEquals('A', expr.evaluate(tuple));
    }

    @Test
    void testByteExpressionWithByteOperations() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("b", Byte.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setByte(1, (byte) 64);

        TupleExpressionGenerator.ByteTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("(byte)(tuple.b() / 2)")
                .schema(schema)
                .returnByte();

        assertEquals((byte) 32, expr.evaluate(tuple));
    }

    @Test
    void testFloatExpressionWithFloatMath() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("f", Float.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setFloat(1, 2.5f);

        TupleExpressionGenerator.FloatTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.f() * tuple.f()")
                .schema(schema)
                .returnFloat();

        assertEquals(6.25f, expr.evaluate(tuple), 0.001);
    }

    @Test
    void testDoubleExpressionWithPrecision() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("d", Double.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setDouble(1, 3.14159);

        TupleExpressionGenerator.DoubleTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.d() * 2.0")
                .schema(schema)
                .returnDouble();

        assertEquals(6.28318, expr.evaluate(tuple), 0.00001);
    }

    @Test
    void testBooleanExpressionWithComparison() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .addField("y", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 10);
        tuple.setInt(2, 20);

        TupleExpressionGenerator.BooleanTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.x() < tuple.y()")
                .schema(schema)
                .returnBoolean();

        assertTrue(expr.evaluate(tuple));

        tuple.setInt(1, 30);
        assertFalse(expr.evaluate(tuple));
    }

    @Test
    void testBooleanExpressionWithLogicalOps() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Integer.TYPE)
                .addField("b", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 5);
        tuple.setInt(2, 10);

        TupleExpressionGenerator.BooleanTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.a() > 0 && tuple.b() > 0")
                .schema(schema)
                .returnBoolean();

        assertTrue(expr.evaluate(tuple));

        tuple.setInt(1, -5);
        assertFalse(expr.evaluate(tuple));
    }

    @Test
    void testExpressionCreation() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .heapMemory()
                .build();

        TupleExpressionGenerator.IntTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.x() + 1")
                .schema(schema)
                .returnInt();

        assertNotNull(expr);
        
        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 10);
        assertEquals(11, expr.evaluate(tuple));
    }

    @Test
    void testExpressionToString() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .heapMemory()
                .build();

        TupleExpressionGenerator.IntTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.x()")
                .schema(schema)
                .returnInt();

        String str = expr.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }

    @Test
    void testComplexMultiFieldExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Integer.TYPE)
                .addField("b", Integer.TYPE)
                .addField("c", Integer.TYPE)
                .addField("d", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 10);
        tuple.setInt(2, 20);
        tuple.setInt(3, 30);
        tuple.setInt(4, 40);

        TupleExpressionGenerator.IntTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("(tuple.a() + tuple.b()) * (tuple.c() - tuple.d())")
                .schema(schema)
                .returnInt();

        // (10 + 20) * (30 - 40) = 30 * (-10) = -300
        assertEquals(-300, expr.evaluate(tuple));
    }

    @Test
    void testExpressionWithDirectTuple() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .directMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setLong(1, 777L);

        TupleExpressionGenerator.LongTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.value() * 3")
                .schema(schema)
                .returnLong();

        assertEquals(2331L, expr.evaluate(tuple));

        schema.destroyTuple(tuple);
    }
}
