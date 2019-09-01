package io.jes.provider.benchmarks;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import io.jes.Event;
import io.jes.provider.InMemoryStoreProvider;
import io.jes.provider.JdbcStoreProvider;
import io.jes.provider.JpaStoreProvider;
import io.jes.provider.StoreProvider;
import lombok.SneakyThrows;

import static io.jes.internal.Events.SampleEvent;
import static io.jes.internal.FancyStuff.newEntityManagerFactory;
import static io.jes.internal.FancyStuff.newPostgresDataSource;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.of;

@Fork(1)
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@SuppressWarnings("DefaultAnnotationParam")
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
public class ProvidersReadBenchmark {

    @State(Scope.Benchmark)
    public static class Providers {

        @SuppressWarnings("FieldCanBeLocal")
        private int totalEventsToRead = 10000;

        private StoreProvider inMemoryProvider;
        private StoreProvider jdbcProviderBytesEncoding;
        private StoreProvider jdbcProviderStringEncoding;
        private StoreProvider jpaProviderBytesEncoding;
        private StoreProvider jpaProviderStringEncoding;

        @Setup(Level.Trial)
        public void setUp() {
            inMemoryProvider = new InMemoryStoreProvider();
            jdbcProviderBytesEncoding = new JdbcStoreProvider<>(newPostgresDataSource("sample1"), byte[].class);
            jdbcProviderStringEncoding = new JdbcStoreProvider<>(newPostgresDataSource("sample2"), String.class);
            jpaProviderBytesEncoding = new JpaStoreProvider<>(newEntityManagerFactory(byte[].class), byte[].class);
            jpaProviderStringEncoding = new JpaStoreProvider<>(newEntityManagerFactory(String.class), String.class);

            Event[] events = range(0, totalEventsToRead).mapToObj(i -> new SampleEvent("" + i, UUID.randomUUID()))
                    .parallel().collect(Collectors.toList()).toArray(new Event[] {});

            inMemoryProvider.write(events);
            jdbcProviderBytesEncoding.write(events);
            jdbcProviderStringEncoding.write(events);
            jpaProviderBytesEncoding.write(events);
            jpaProviderStringEncoding.write(events);
        }

        @SneakyThrows
        @TearDown(Level.Trial)
        public void tearDown() {
            of(jdbcProviderBytesEncoding, jdbcProviderStringEncoding, jpaProviderBytesEncoding, jpaProviderStringEncoding)
                    .map(AutoCloseable.class::cast)
                    .forEach(closeable -> {
                        try {
                            closeable.close();
                        } catch (Exception ignored) {}
                    });
        }

    }

    @Benchmark
    @SneakyThrows
    @SuppressWarnings("unused")
    public void inMemoryProviderReads10000events(Blackhole blackhole, Providers providers) {
        readEventsSequentially(blackhole, providers.inMemoryProvider);
    }

    @Benchmark
    @SneakyThrows
    @SuppressWarnings("unused")
    public void jdbcProviderBytesEncodingReads10000events(Blackhole blackhole, Providers providers) {
        readEventsSequentially(blackhole, providers.jdbcProviderBytesEncoding);
    }

    @Benchmark
    @SneakyThrows
    @SuppressWarnings("unused")
    public void jdbcProviderStringEncodingReads10000events(Blackhole blackhole, Providers providers) {
        readEventsSequentially(blackhole, providers.jdbcProviderStringEncoding);
    }

    @Benchmark
    @SneakyThrows
    @SuppressWarnings("unused")
    public void jpaProviderBytesEncodingReads10000events(Blackhole blackhole, Providers providers) {
        readEventsSequentially(blackhole, providers.jpaProviderBytesEncoding);
    }

    @Benchmark
    @SneakyThrows
    @SuppressWarnings("unused")
    public void jpaProviderStringEncodingReads10000events(Blackhole blackhole, Providers providers) {
        readEventsSequentially(blackhole, providers.jpaProviderStringEncoding);
    }

    private void readEventsSequentially(Blackhole blackhole, StoreProvider provider) {
        try (Stream<Event> stream = provider.readFrom(0)) {
            stream.forEach(blackhole::consume);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        final Options options = new OptionsBuilder().include(ProvidersReadBenchmark.class.getSimpleName())
                .detectJvmArgs().jvmArgsAppend("-Xmx2048m")
                .build();
        new Runner(options).run();
    }

}