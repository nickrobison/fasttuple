package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import com.nickrobison.tuple.TupleSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleExpressionGeneratorAdvancedTest {

    @Test
    void testVoidExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("b", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        
        TupleExpressionGenerator.TupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.a(100L), tuple.b(200L)")
                .schema(schema)
                .returnVoid();
        
        expr.evaluate(tuple);
        
        assertEquals(100L, tuple.getLong(1));
        assertEquals(200L, tuple.getLong(2));
    }

    @Test
    void testObjectExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .addField("y", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 10);
        tuple.setInt(2, 20);
        
        TupleExpressionGenerator.ObjectTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.x() + tuple.y()")
                .schema(schema)
                .returnObject();
        
        Object result = expr.evaluate(tuple);
        assertEquals(30, result);
    }

    @Test
    void testLongExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setLong(1, 50L);
        
        TupleExpressionGenerator.LongTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.value() * 2")
                .schema(schema)
                .returnLong();
        
        assertEquals(100L, expr.evaluate(tuple));
    }

    @Test
    void testIntExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("num", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 25);
        
        TupleExpressionGenerator.IntTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.num() + 5")
                .schema(schema)
                .returnInt();
        
        assertEquals(30, expr.evaluate(tuple));
    }

    @Test
    void testShortExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("s", Short.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setShort(1, (short) 100);
        
        TupleExpressionGenerator.ShortTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.s()")
                .schema(schema)
                .returnShort();
        
        assertEquals((short) 100, expr.evaluate(tuple));
    }

    @Test
    void testCharExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("ch", Character.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setChar(1, 'Q');
        
        TupleExpressionGenerator.CharTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.ch()")
                .schema(schema)
                .returnChar();
        
        assertEquals('Q', expr.evaluate(tuple));
    }

    @Test
    void testByteExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("b", Byte.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setByte(1, (byte) 42);
        
        TupleExpressionGenerator.ByteTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.b()")
                .schema(schema)
                .returnByte();
        
        assertEquals((byte) 42, expr.evaluate(tuple));
    }

    @Test
    void testFloatExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("f", Float.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setFloat(1, 3.5f);
        
        TupleExpressionGenerator.FloatTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.f() * 2")
                .schema(schema)
                .returnFloat();
        
        assertEquals(7.0f, expr.evaluate(tuple), 0.001);
    }

    @Test
    void testDoubleExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("d", Double.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setDouble(1, 1.5);
        
        TupleExpressionGenerator.DoubleTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.d() + 0.5")
                .schema(schema)
                .returnDouble();
        
        assertEquals(2.0, expr.evaluate(tuple), 0.001);
    }

    @Test
    void testBooleanExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 10);
        
        TupleExpressionGenerator.BooleanTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.x() > 5")
                .schema(schema)
                .returnBoolean();
        
        assertTrue(expr.evaluate(tuple));
        
        tuple.setInt(1, 3);
        assertFalse(expr.evaluate(tuple));
    }

    @Test
    void testComplexMultiStatementExpression() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Integer.TYPE)
                .addField("b", Integer.TYPE)
                .addField("c", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        
        TupleExpressionGenerator.IntTupleExpression expr = TupleExpressionGenerator.builder()
                .expression("tuple.a(10), tuple.b(20), tuple.a() + tuple.b()")
                .schema(schema)
                .returnInt();
        
        int result = expr.evaluate(tuple);
        assertEquals(30, result);
        assertEquals(10, tuple.getInt(1));
        assertEquals(20, tuple.getInt(2));
    }
}
