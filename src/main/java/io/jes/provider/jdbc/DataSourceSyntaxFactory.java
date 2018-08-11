package io.jes.provider.jdbc;

public class DataSourceSyntaxFactory {

    private static final String UNSUPPORTED_TYPE = "%s for %s type not supported";

    public static DataSourceSyntax newDataSourceSyntax(DataSourceType type, String schema) {
        switch (type) {
            case POSTGRESQL:
                return new PostgreSQLSyntax(schema);
            default:
                throw new IllegalArgumentException(String.format(UNSUPPORTED_TYPE, DataSourceSyntax.class, type));
        }
    }

}
