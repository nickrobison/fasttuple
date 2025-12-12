package com.nickrobison.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional comprehensive tests to maximize mutation killing
 */
class ComprehensiveMutationTest {

    @Test
    void testSizeOfUtilityForAllTypes() {
        assertEquals(1, SizeOf.sizeOf(Byte.TYPE));
        assertEquals(2, SizeOf.sizeOf(Character.TYPE));
        assertEquals(2, SizeOf.sizeOf(Short.TYPE));
        assertEquals(4, SizeOf.sizeOf(Integer.TYPE));
        assertEquals(4, SizeOf.sizeOf(Float.TYPE));
        assertEquals(8, SizeOf.sizeOf(Long.TYPE));
        assertEquals(8, SizeOf.sizeOf(Double.TYPE));
    }

    @Test
    void testSizeOfReturnsCorrectSizes() {
        // Verify all primitive types return correct sizes
        assertTrue(SizeOf.sizeOf(Byte.TYPE) > 0);
        assertTrue(SizeOf.sizeOf(Character.TYPE) > 0);
        assertTrue(SizeOf.sizeOf(Short.TYPE) > 0);
        assertTrue(SizeOf.sizeOf(Integer.TYPE) > 0);
        assertTrue(SizeOf.sizeOf(Float.TYPE) > 0);
        assertTrue(SizeOf.sizeOf(Long.TYPE) > 0);
        assertTrue(SizeOf.sizeOf(Double.TYPE) > 0);
        
        // Verify size relationships
        assertTrue(SizeOf.sizeOf(Long.TYPE) >= SizeOf.sizeOf(Integer.TYPE));
        assertTrue(SizeOf.sizeOf(Integer.TYPE) >= SizeOf.sizeOf(Short.TYPE));
        assertTrue(SizeOf.sizeOf(Short.TYPE) >= SizeOf.sizeOf(Byte.TYPE));
    }

    @Test
    void testHeapTupleSchemaArrayOperations() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .heapMemory()
                .build();

        // Test array creation
        FastTuple[] array1 = schema.createTupleArray(5);
        assertEquals(5, array1.length);
        for (FastTuple tuple : array1) {
            assertNotNull(tuple);
        }
    }

    @Test
    void testDirectTupleSchemaRecordArrayOperations() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .addField("y", Integer.TYPE)
                .directMemory()
                .build();

        long recordArray = schema.createRecordArray(10);
        assertTrue(recordArray != 0);

        // Write and read values
        for (int i = 0; i < 10; i++) {
            long offset = recordArray + (long) i * schema.getByteSize();
            schema.setInt(offset, 0, i * 10);
            schema.setInt(offset, 1, i * 20);
        }

        for (int i = 0; i < 10; i++) {
            long offset = recordArray + (long) i * schema.getByteSize();
            assertEquals(i * 10, schema.getInt(offset, 0));
            assertEquals(i * 20, schema.getInt(offset, 1));
        }

        schema.destroy(recordArray);
    }

    @Test
    void testDirectTupleSchemaWithPadding() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("a", Byte.TYPE)
                .addField("b", Long.TYPE)
                .directMemory()
                .padToWordSize(32)
                .build();

        assertEquals(32, schema.getByteSize());

        int[] layout = schema.getLayout();
        assertNotNull(layout);
        assertTrue(layout.length > 0);

        // Verify layout is cloned (defensive copy)
        int[] layout2 = schema.getLayout();
        assertNotSame(layout, layout2);
        assertArrayEquals(layout, layout2);
    }

    @Test
    void testTuplePoolExhaustion() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .poolOfSize(2)
                .directMemory()
                .build();

        TuplePool<FastTuple> pool = schema.pool();

        FastTuple t1 = pool.checkout();
        FastTuple t2 = pool.checkout();

        assertNotNull(t1);
        assertNotNull(t2);

        // Pool should be exhausted (non-expanding)
        assertThrows(IllegalStateException.class, pool::checkout);

        pool.release(t1);

        // Now we can checkout again
        FastTuple t3 = pool.checkout();
        assertNotNull(t3);

        pool.release(t2);
        pool.release(t3);
        pool.close();
    }

    @Test
    void testTuplePoolExpansion() {
        TuplePool<Integer> pool = new TuplePool<>(2, true,
                size -> {
                    Integer[] arr = new Integer[size];
                    for (int i = 0; i < size; i++) {
                        arr[i] = i;
                    }
                    return arr;
                },
                arr -> {});

        assertEquals(0, pool.getSize());

        // Checkout initial pool
        Integer i1 = pool.checkout();
        assertNotNull(i1);
        Integer i2 = pool.checkout();
        assertNotNull(i2);

        assertEquals(2, pool.getSize());

        // Pool should expand
        Integer i3 = pool.checkout();
        assertNotNull(i3);
        assertTrue(pool.getSize() > 2);

        pool.close();
    }

    @Test
    void testTuplePoolCloseThrowsOnCheckout() {
        TuplePool<String> pool = new TuplePool<>(2, false,
                size -> new String[size],
                arr -> {});

        pool.close();

        assertThrows(IllegalStateException.class, pool::checkout);
    }

    @Test
    void testDirectTupleDestroyOperations() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .directMemory()
                .build();

        // Test destroy with FastTuple
        FastTuple tuple = schema.createTuple();
        tuple.setLong(1, 123L);
        schema.destroy(tuple);

        // Test destroy with address
        long address = schema.createRecord();
        schema.setLong(address, 0, 456L);
        schema.destroy(address);

        // Test destroyTuple
        FastTuple tuple2 = schema.createTuple();
        schema.destroyTuple(tuple2);
    }

    @Test
    void testTupleSchemaBuilderWithInterface() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .implementInterface(TestInterface.class)
                .heapMemory()
                .build();

        Class<?> tupleClass = schema.tupleClass();
        assertTrue(TestInterface.class.isAssignableFrom(tupleClass));

        TestInterface typed = schema.createTypedTuple(TestInterface.class);
        typed.value(888L);
        assertEquals(888L, typed.value());
    }

    @Test
    void testBuilderValidatesFieldCounts() throws Exception {
        TupleSchema.Builder builder = TupleSchema.builder();
        builder.addField("field1", Long.TYPE);
        builder.addField("field2", Integer.TYPE);

        // Valid build should succeed
        TupleSchema schema = builder.heapMemory().build();
        assertNotNull(schema);
        assertEquals(2, schema.getFieldNames().length);
    }

    @Test
    void testBuilderValidatesFieldNamesAndTypes() {
        TupleSchema.Builder builder = TupleSchema.builder();
        builder.addField("a", Long.TYPE);
        builder.addField("b", Integer.TYPE);

        // Valid build
        assertDoesNotThrow(() -> builder.heapMemory().build());
    }

    @Test
    void testInterfaceImplementation() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .implementInterface(TestInterface.class)
                .heapMemory()
                .build();

        Class<?> tupleClass = schema.tupleClass();
        assertTrue(TestInterface.class.isAssignableFrom(tupleClass));
        
        TestInterface tuple = schema.createTypedTuple(TestInterface.class);
        tuple.value(123L);
        assertEquals(123L, tuple.value());
    }

    @Test
    void testTypedTupleArrayDestroy() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("value", Integer.TYPE)
                .implementInterface(SimpleInterface.class)
                .heapMemory()
                .build();

        SimpleInterface[] tuples = schema.createTypedTupleArray(SimpleInterface.class, 3);
        for (int i = 0; i < tuples.length; i++) {
            tuples[i].value(i * 5);
        }

        schema.destroyTypedTupleArray(tuples);
    }

    @Test
    void testDirectTupleWithAddress() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("data", Long.TYPE)
                .directMemory()
                .build();

        long address = schema.createRecord();
        schema.setLong(address, 0, 999L);

        FastTuple tuple = schema.createTuple(address);
        assertEquals(999L, tuple.getLong(1));

        tuple.setLong(1, 111L);
        assertEquals(111L, schema.getLong(address, 0));

        schema.destroy(address);
    }

    @Test
    void testAllPrimitiveTypeDirectMemory() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("aByte", Byte.TYPE)
                .addField("aChar", Character.TYPE)
                .addField("aShort", Short.TYPE)
                .addField("anInt", Integer.TYPE)
                .addField("aFloat", Float.TYPE)
                .addField("aLong", Long.TYPE)
                .addField("aDouble", Double.TYPE)
                .directMemory()
                .build();

        long record = schema.createRecord();

        schema.setByte(record, 0, (byte) 1);
        schema.setChar(record, 1, 'Z');
        schema.setShort(record, 2, (short) 100);
        schema.setInt(record, 3, 1000);
        schema.setFloat(record, 4, 1.5f);
        schema.setLong(record, 5, 10000L);
        schema.setDouble(record, 6, 2.5);

        assertEquals((byte) 1, schema.getByte(record, 0));
        assertEquals('Z', schema.getChar(record, 1));
        assertEquals((short) 100, schema.getShort(record, 2));
        assertEquals(1000, schema.getInt(record, 3));
        assertEquals(1.5f, schema.getFloat(record, 4), 0.001);
        assertEquals(10000L, schema.getLong(record, 5));
        assertEquals(2.5, schema.getDouble(record, 6), 0.001);

        schema.destroy(record);
    }

    public interface TestInterface {
        long value();
        void value(long v);
    }

    public interface AnotherInterface {
    }

    public interface SimpleInterface {
        int value();
        void value(int v);
    }
}
