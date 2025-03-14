module com.example.core.common {
    requires java.base;
    requires java.sql;
    exports com.example.core.common.utilities.operations;
    exports com.example.core.common.utilities.completion;
    exports com.example.core.common.utilities.persistence;
}