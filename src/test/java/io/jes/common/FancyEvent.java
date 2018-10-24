package io.jes.common;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.jes.Event;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class FancyEvent implements Event {

    private final String name;
    private final UUID uuid;

    public FancyEvent(@Nonnull String name, @Nonnull UUID uuid) {
        this.name = Objects.requireNonNull(name);
        this.uuid = Objects.requireNonNull(uuid);
    }

    @Nullable
    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName() + " [");
        sb.append("name: ").append(name);
        sb.append(", uuid: ").append(uuid);
        sb.append(']');
        return sb.toString();
    }
}