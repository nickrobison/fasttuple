package com.nickrobison.tuple;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by cliff on 5/2/14.
 */
public abstract class TupleSchema implements Loader<FastTuple>, Destroyer<FastTuple> {
    protected final String[] fieldNames;
    protected final Class<?>[] fieldTypes;
    protected final Class<?> iface;
    protected Class<?> clazz;
    protected final TuplePool<FastTuple> pool;

    public static Builder builder() {
        return new Builder();
    }

    protected TupleSchema(Builder builder) {
        this.fieldNames = builder.fn.toArray(new String[0]);
        this.fieldTypes = builder.ft.toArray(new Class[0]);
        if (fieldNames.length != fieldTypes.length) {
            throw new IllegalArgumentException("fieldNames and fieldTypes must have equal length");
        }
        for (int i = 0; i < fieldNames.length; i++) {
            if (!fieldTypes[i].isPrimitive() && !fieldTypes[i].equals(Boolean.TYPE)) {
                throw new IllegalArgumentException("Invalid field type combination");
            }
        }
        this.iface = builder.iface;
        if (iface != null && !iface.isInterface()) {
            throw new IllegalArgumentException(iface.getName() + " is not an interface");
        }

        this.pool = new TuplePool<>(builder.poolSize, builder.createWhenExhausted, this, this);

    }

    public static class Builder {
        private final List<String> fn;
        private final List<Class<?>> ft;
        private Class<?> iface;
        private int poolSize;
        private final int threads;
        private boolean createWhenExhausted = false;

        public Builder(Builder builder) {
            fn = new ArrayList<>(builder.fn);
            ft = new ArrayList<>(builder.ft);
            iface = builder.iface;
            poolSize = builder.poolSize;
            threads = builder.threads;
            createWhenExhausted = builder.createWhenExhausted;
        }

        public Builder() {
            fn = new ArrayList<>();
            ft = new ArrayList<>();
            iface = null;
            poolSize = 0;
            threads = 0;
        }

        /**
         * Adds a field name and type to the schema.  Field names end up as both method names and field names
         * in the generated class, therefore they have the same restrictions on allowable characters.  Passing
         * in an illegal name will cause a CompileException during the call to build.
         *
         * @param fieldName - Name of field
         * @param fieldType - {@link Class} type of field
         * @return - {@link Builder}
         */
        public Builder addField(String fieldName, Class<?> fieldType) {
            fn.add(fieldName);
            ft.add(fieldType);
            return this;
        }

        /**
         * The generated FastTuple subclass will implement the passed in interface.  FastTuple's produced
         * from this schema can then be cast to the interface type, for type safe invocation of the desired methods.
         *
         * @param iface - {@link Class} interface to implement
         * @return - {@link Builder}
         */
        public Builder implementInterface(Class<?> iface) {
            this.iface = iface;
            return this;
        }

        public Builder addFieldNames(String... fieldNames) {
            Collections.addAll(fn, fieldNames);
            return this;
        }

        public Builder addFieldNames(Iterable<String> fieldNames) {
            for (String st : fieldNames) {
                fn.add(st);
            }
            return this;
        }

        public Builder addFieldTypes(Class<?>... fieldTypes) {
            Collections.addAll(ft, fieldTypes);
            return this;
        }

        public Builder addFieldTypes(Iterable<Class<?>> fieldTypes) {
            for (Class<?> c : fieldTypes) {
                ft.add(c);
            }
            return this;
        }

        /**
         * Sets the initial size for each thread local tuple pool.  The total number
         * of tuples that will be allocated can be found by multiplying this number
         * by the number of threads that will be checking tuples out of the pool.
         *
         * @param poolSize - The size to generate specified in number of tuples.
         * @return - {@link Builder}
         */
        public Builder poolOfSize(int poolSize) {
            this.poolSize = poolSize;
            return this;
        }

        /**
         * Specifies that the tuple pool should allocate more tuples when it becomes
         * exhausted.  Otherwise, an exhausted pool will throw an IllegalStateException.
         *
         * @return - {@link Builder}
         */
        public Builder expandingPool() {
            this.createWhenExhausted = true;
            return this;
        }

        /**
         * Causes this schema to allocate its memory off of the main java heap.
         *
         * @return - {@link  Builder}
         */
        public DirectTupleSchema.Builder directMemory() {
            return new DirectTupleSchema.Builder(this);
        }

        /**
         * Causes this schema to allocate its memory on heap, and fully reachable by GC.
         *
         * @return - {@link Builder}
         */
        public HeapTupleSchema.Builder heapMemory() {
            return new HeapTupleSchema.Builder(this);
        }

    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("(");
        for (int i = 0; i < fieldNames.length; i++) {
            str.append("'");
            str.append(fieldNames[i]);
            str.append("':");
            str.append(fieldTypes[i].getName());
            if (i < fieldNames.length - 1) {
                str.append(",");
            }
        }
        str.append(")");
        return str.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TupleSchema) {
            TupleSchema o = (TupleSchema) other;
            return Arrays.equals(fieldNames, o.fieldNames) &&
                    Arrays.equals(fieldTypes, o.fieldTypes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{fieldNames, fieldTypes});
    }

    public Class<?> tupleClass() {
        return clazz;
    }

    public String[] getFieldNames() {
        return fieldNames.clone();
    }

    public Class<?>[] getFieldTypes() {
        return fieldTypes.clone();
    }

    /**
     * Retrieves the index of a field by its name. The index is 1-based, meaning the first field is index 1.
     *
     * @param name the name of the field to find
     * @return the 1-based index of the field
     * @throws IllegalArgumentException if the field name is not found in the schema
     */
    public int getFieldIndex(String name) {
        final String[] cloned = fieldNames.clone();
        for (int i = 0; i < cloned.length; i++) {
            if (cloned[i].equals(name)) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException("Field " + name + " not found");
    }

    protected abstract void generateClass() throws Exception;

    /**
     * Allocates a new tuple, completely separate from any pooling.
     *
     * @return - {@link FastTuple}
     * @throws Exception - Throws an exception if unable to allocate tuple
     */
    public abstract FastTuple createTuple() throws Exception;

    /**
     * Allocates a new typed tuple, completely separate from any pooling.
     * Use care to call {@link Builder#implementInterface(Class)} before using this method
     *
     * @param clazz - {@link Class} implemented by the Tuple
     * @param <T>   - {@link T} type parameter
     * @return - {@link FastTuple} case to type {@link T}
     * @throws Exception - Throws an exception if unable to allocate tuple or cast to the specified type
     */
    public abstract <T> T createTypedTuple(Class<T> clazz) throws Exception;

    /**
     * Allocates an array of tuples. This method will try to ensure that tuples get allocated
     * in adjacent memory, however with the heap based allocation this is not guaranteed.
     *
     * @param size the number of tuples in the array.
     * @return - Array of {@link FastTuple}
     * @throws Exception - Throws if unable to allocate tuple array
     */
    public abstract FastTuple[] createTupleArray(int size) throws Exception;

    /**
     * * Allocates an array of tuples. This method will try to ensure that tuples get allocated
     * in adjacent memory, however with the heap based allocation this is not guaranteed.
     *
     * @param clazz - {@link Class} implemented by the Tuple
     * @param size  - the number of tuples in the array
     * @param <T>   - {@link T} type parameter
     * @return - Array of {@link FastTuple} cast to type {@link T}
     * @throws Exception - Throws is unable to allocate tuple array or cast to the specified type
     */
    public abstract <T> T[] createTypedTupleArray(Class<T> clazz, int size) throws Exception;

    /**
     * Deallocates memory for a tuple.
     *
     * @param tuple - {@link FastTuple} to deallocate
     */
    public abstract void destroyTuple(FastTuple tuple);

    /**
     * * Deallocates memory for a typed tuple.
     *
     * @param tuple - {@link FastTuple} cast to type {@link T}
     * @param <T>   - {@link T} underlying class implemented by tuples
     */
    public abstract <T> void destroyTypedTuple(T tuple);

    /**
     * Deallocates memory for an array of tuples.  Assumes that they were allocated as an array.
     *
     * @param ary - Array of {@link FastTuple} to deallocate
     */
    public abstract void destroyTupleArray(FastTuple[] ary);

    /**
     * Deallocates memory for an array of typed tuples.  Assumes that they were allocated as an array.
     *
     * @param ary - Array of typed {@link FastTuple} to deallocate
     * @param <T> - {@link T} underlying class implemented by tuples
     */
    public abstract <T> void destroyTypedTupleArray(T[] ary);

    @Override
    public void destroyArray(FastTuple[] ary) {
        destroyTupleArray(ary);
    }

    @Override
    public FastTuple[] createArray(int size) throws Exception {
        return createTupleArray(size);
    }

    /**
     * Returns the tuple pool for this schema.  Each individual thread accessing this method
     * will see a different pool.
     *
     * @return - {@link TuplePool} of {@link FastTuple}
     */
    public TuplePool<FastTuple> pool() {
        return pool;
    }

    public ClassLoader getClassLoader() {
        return clazz.getClassLoader();
    }
}
