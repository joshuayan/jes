package io.jes.reactors;

import javax.annotation.Nonnull;

public interface Trigger extends AutoCloseable {

    void onChange(@Nonnull Runnable runnable);

}
