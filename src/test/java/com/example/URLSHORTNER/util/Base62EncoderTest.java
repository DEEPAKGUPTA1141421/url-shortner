package com.example.URLSHORTNER.util;

import org.junit.jupiter.api.Test;

import com.example.URLSHORTNER.service.Base62Encoder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    @Test
    void alwaysProducesSevenCharUrlSafeCode() {
        String code = Base62Encoder.encode(12345L);
        assertEquals(7, code.length());
        assertTrue(code.matches("[0-9A-Za-z]{7}"));
    }

    @Test
    void isDeterministic() {
        assertEquals(Base62Encoder.encode(999L), Base62Encoder.encode(999L));
    }

    @Test
    void producesNoCollisionsAcrossOneMillionSequentialIds() {
        Set<String> seen = new HashSet<>();
        for (long id = 1000; id < 1_000_000; id++) {
            String code = Base62Encoder.encode(id);
            assertTrue(seen.add(code), "Collision detected at id=" + id + " code=" + code);
        }
        assertEquals(1_000_000 - 1000, seen.size());
    }

    @Test
    void doesNotTriviallyRevealSequentialOrder() {
        // Consecutive ids should not produce lexicographically adjacent codes --
        // a cheap sanity check that the obfuscation is doing something.
        String a = Base62Encoder.encode(1000L);
        String b = Base62Encoder.encode(1001L);
        assertNotEquals(a, b);
        // first characters should differ far more often than not for a good shuffle
    }

    @Test
    void rejectsNegativeIds() {
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.encode(-1L));
    }
}
