package store.jesframework.provider.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static store.jesframework.provider.jdbc.DDLFactory.*;
import static store.jesframework.provider.jdbc.DDLFactory.getAggregateStoreDDL;
import static store.jesframework.provider.jdbc.DDLFactory.getEventStoreDDL;
import static store.jesframework.provider.jdbc.DDLFactory.getLockDDL;
import static store.jesframework.provider.jdbc.DDLFactory.getOffsetsDDL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DDLFactoryTest {

    private static final String POSTGRE_SQL = "PostgreSQL";

    private Connection newConnectionMock(String databaseName, String schema) {
        return newConnectionMock(databaseName, schema, 10);
    }

    @SneakyThrows
    private Connection newConnectionMock(String databaseName, String schema, int version) {
        final Connection connection = mock(Connection.class);
        when(connection.getSchema()).thenReturn(schema);

        final DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(metaData.getDatabaseProductName()).thenReturn(databaseName);
        when(metaData.getDatabaseMajorVersion()).thenReturn(version);
        when(connection.getMetaData()).thenReturn(metaData);
        return connection;
    }

    @Test
    void getEventStoreDDLShouldReturnScriptOnCorrectValue() {
        assertNotNull(getEventStoreDDL(newConnectionMock(POSTGRE_SQL, "FOO"), byte[].class));
        assertNotNull(getEventStoreDDL(newConnectionMock(POSTGRE_SQL, "FOO", 9), byte[].class));
        assertNotNull(getEventStoreDDL(newConnectionMock("H2", "FOO"), String.class));
    }

    @Test
    void getAggregateStoreDDLShouldReturnScriptOnCorrectValue() {
        assertNotNull(getAggregateStoreDDL(newConnectionMock(POSTGRE_SQL, "FOO")));
        assertNotNull(getAggregateStoreDDL(newConnectionMock(POSTGRE_SQL, "FOO", 8)));
    }

    @Test
    void getOffsetDDLShouldReturnStriptOnCorrectValue() {
        assertNotNull(getOffsetsDDL(newConnectionMock("H2", "FOO")));
        assertNotNull(getOffsetsDDL(newConnectionMock(POSTGRE_SQL, "FOO")).getClass());
        assertNotNull(getOffsetsDDL(newConnectionMock(POSTGRE_SQL, "FOO", 7)).getClass());
    }

    @Test
    void getAggregateStoreDDLShouldThrowIllegalArgumentExceptionOnUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> getAggregateStoreDDL(newConnectionMock("FOO", "FOO")));
    }

    @Test
    void getEventStoreDDLShouldThrowIllegalArgumentExceptionOnAnyOtherValue() {
        assertThrows(IllegalArgumentException.class,
                () -> getEventStoreDDL(newConnectionMock("Oracle DB", "BAR"), String.class));
        assertThrows(IllegalArgumentException.class,
                () -> getEventStoreDDL(newConnectionMock("MySQL", "FOO"), byte[].class));
        assertThrows(IllegalArgumentException.class,
                () -> getEventStoreDDL(newConnectionMock("DB2", "BAZ"), String.class));
        assertThrows(IllegalArgumentException.class,
                () -> getEventStoreDDL(newConnectionMock("H2", "BAZ"), byte.class));
    }

    @Test
    void getOffsetsDDLShouldThrowIllegalArgumentExceptionOnUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> getOffsetsDDL(newConnectionMock("FOO", "FOO")));
    }

    @Test
    void getLockDDLShouldThrowIllegalArgumentExceptionOnUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> getLockDDL(newConnectionMock("FOO", "FOO")));
    }

    @Test
    void getLockDDLShouldReturnScriptOnCorrectValue() {
        assertNotNull(getOffsetsDDL(newConnectionMock(POSTGRE_SQL, "FOO")));
        assertNotNull(getOffsetsDDL(newConnectionMock(POSTGRE_SQL, "FOO", 9)));
    }

    @Test
    void readNonExistingLocationShouldResultInIllegalStateException() {
        final Exception exception = assertThrows(IllegalStateException.class, () -> readDDL("foo/bar/baz/boo"));
        // verify message contains needed info
        assertEquals("Can't find script: foo/bar/baz/boo", exception.getMessage());
    }

}