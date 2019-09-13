package store.jesframework.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnknownTypeResolvedTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    void unknownTypeResolvedShouldProtectItsInvariants() {
        assertThrows(NullPointerException.class, () -> new UnknownTypeResolved(null, new Object()));
        assertThrows(NullPointerException.class, () -> new UnknownTypeResolved("", null));

        assertEquals(new UnknownTypeResolved("", ""), new UnknownTypeResolved("", ""));
    }

}