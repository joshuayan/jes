package store.jesframework.util;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

import static store.jesframework.util.JdbcUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcUtilsTest {

    @Test
    void getSchemaShouldThrowNullPoinerExceptionIfConnectionIsNull() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> getSchemaName(null));
    }

    @Test
    @SneakyThrows
    void getSchemaShouldReturnSchemaNameFromConnection() {
        final Connection connection = mock(Connection.class);
        when(connection.getSchema()).thenReturn("foo");
        assertEquals("foo", getSchemaName(connection));
        verify(connection, times(1)).getSchema();
    }

    @Test
    @SneakyThrows
    void getSchemaShouldReturnSchemaNameFromPropertiesIfConnectionReturnsNull() {
        final Connection connection = mock(Connection.class);
        assertEquals("es", getSchemaName(connection));
        verify(connection, times(1)).getSchema();
    }

    @Test
    void getDatabaseNameShouldThrowNullPoinerExceptionIfConnectionIsNull() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> getDatabaseName(null));
    }

    @Test
    @SneakyThrows
    void getSchemaShouldReturnDatabaseNameFromConnection() {
        final Connection connection = mock(Connection.class);
        final DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("product");
        assertEquals("product", getDatabaseName(connection));
        verify(connection, times(1)).getMetaData();
        verify(metaData, times(1)).getDatabaseProductName();
    }

    @Test
    void unwrapJdbcTypeShouldThrowIllegalArgumentExceptionIfTypeIsNullOrUnsupported() {
        assertThrows(IllegalArgumentException.class, () -> unwrapJdbcType(null));
        assertThrows(IllegalArgumentException.class, () -> unwrapJdbcType(new Object()));
    }

    @Test
    void unwrapJdbcTypeShouldResolveRegisteredTypes() {
        assertDoesNotThrow(() -> unwrapJdbcType("Foo"));
        assertDoesNotThrow(() -> unwrapJdbcType(new byte[] {}));
        assertDoesNotThrow(() -> unwrapJdbcType(mock(Clob.class)));
        assertDoesNotThrow(() -> unwrapJdbcType(mock(Blob.class)));
    }

    @Test
    @SneakyThrows
    void createConnectionShouldSetDefaultSchema() {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        final Connection result = createConnection(dataSource);

        assertNotNull(result);
        verify(connection, times(1)).setSchema("es");
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getSqlTypeByClassAndDatabaseNameShouldThrowNullPointerExceptionOnAnyNull() {
        assertThrows(NullPointerException.class, () -> getSqlTypeByClassAndDatabaseName(null, "H2"));
        assertThrows(NullPointerException.class, () -> getSqlTypeByClassAndDatabaseName(String.class, null));
    }

    @Test
    void getSqlTypeByClassAndDatabaseNameShouldThrowIllegalArgumentExceptionOnUnknownTypeOrDBName() {
        assertThrows(IllegalArgumentException.class, () -> getSqlTypeByClassAndDatabaseName(boolean.class, "H2"));
        assertThrows(IllegalArgumentException.class, () -> getSqlTypeByClassAndDatabaseName(String.class, "Oracle"));
    }

    @Test
    void getSqlTypeByClassAndDatabaseNameShouldReturnCorrectValuesOnValidInput() {
        assertEquals("TEXT", getSqlTypeByClassAndDatabaseName(String.class, "H2"));
        assertEquals("BYTEA", getSqlTypeByClassAndDatabaseName(byte[].class, "PostgreSQL"));
        assertEquals("BLOB", getSqlTypeByClassAndDatabaseName(byte[].class, "H2"));
    }

}