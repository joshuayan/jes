package io.jes.provider.jdbc;

import javax.annotation.Nonnull;

import static java.lang.String.format;

public class DDLFactory {

    private DDLFactory() {}

    private static final String UNSUPPORTED_TYPE = "%s for %s type not supported";

    public static DDLProducer newDataSourceSyntax(@Nonnull String databaseName, @Nonnull String schema) {
        if ("PostgreSQL".equals(databaseName)) {
            return new PostgresDDL(schema);
        } else {
            throw new IllegalArgumentException(format(UNSUPPORTED_TYPE, DDLProducer.class, databaseName));
        }
    }

}