package store.jesframework.serializer;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import store.jesframework.serializer.api.SerializationOption;
import store.jesframework.serializer.api.Upcaster;

import static store.jesframework.serializer.SerializerFactory.ParsedOptions;
import static store.jesframework.serializer.SerializerFactory.newAggregateSerializer;
import static store.jesframework.serializer.SerializerFactory.newBinarySerializer;
import static store.jesframework.serializer.SerializerFactory.newEventSerializer;
import static store.jesframework.serializer.SerializerFactory.newStringSerializer;

class SerializerFactoryTest {

    @Test
    void newBinarySerializerShouldReturnNonNullSerializer() {
        Assertions.assertNotNull(newBinarySerializer(ParsedOptions.parse()));
    }

    @Test
    void newStringSerializerShouldReturnNonNullSerializer() {
        Assertions.assertNotNull(newStringSerializer(ParsedOptions.parse()));
    }

    @Test
    void newEventSerializerShouldReturnNonNullSerializerWhenStringClassPassed() {
        Assertions.assertNotNull(newEventSerializer(String.class).getClass());
    }

    @Test
    void newEventSerializerShouldReturnNonNullSerializerWhenByteClassPassed() {
        Assertions.assertNotNull(newEventSerializer(byte[].class).getClass());
    }

    @Test
    void newEventSerializerShouldThrowIllegalArgumentExceptionOnUnknownSerializationType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> newEventSerializer(Void.class));
    }

    @Test
    void newAggregateSerializerShouldReturnKryoImplAsDefaultWhenByteClassPassed() {
        Assertions.assertEquals(KryoSerializer.class, newAggregateSerializer(byte[].class).getClass());
    }

    @Test
    void newAggregateSerializerShouldReturnJacksonImplAsDefaultWhenStringClassPassed() {
        Assertions.assertEquals(JacksonSerializer.class, newAggregateSerializer(String.class).getClass());
    }

    @Test
    void newAggregateSerializerShouldThrowIllegalArgumentExceptionOnUnknownSerializationType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> newAggregateSerializer(Stream.class));
    }

    @Test
    void wrongUpcastersShouldNotFailInitialization() {
        final SerializationOption byteUpcaster = new Upcaster<byte[]>() {
            @Nonnull
            @Override
            public byte[] upcast(@Nonnull byte[] raw) {
                return new byte[0];
            }

            @Nonnull
            @Override
            public String eventTypeName() {
                return "";
            }
        };

        final SerializationOption stringUpcaster = new Upcaster<String>() {

            @Nonnull
            @Override
            public String upcast(@Nonnull String raw) {
                return "";
            }

            @Nonnull
            @Override
            public String eventTypeName() {
                return "";
            }
        };
        Assertions.assertDoesNotThrow(() -> ParsedOptions.<String>parse(byteUpcaster, stringUpcaster));
    }

}