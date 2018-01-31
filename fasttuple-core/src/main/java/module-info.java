module com.nickrobison.fasttuple.core {
    requires jdk.unsupported;
    requires com.google.common;
    requires commons.compiler;
    requires janino;

    exports com.nickrobison.tuple;
    exports com.nickrobison.tuple.codegen;
    exports com.nickrobison.tuple.unsafe;
}
