module com.nickrobison.fasttuple {
    requires jdk.unsupported;
    requires transitive org.codehaus.janino;
    requires transitive org.codehaus.commons.compiler;

    exports com.nickrobison.tuple;
    exports com.nickrobison.tuple.codegen;
    exports com.nickrobison.tuple.unsafe;
}
