package io.github.dualuse.awt.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector4fTest {

    private static void assertVecEquals(float x, float y, float z, float w, Vector4f v, float eps) {
        assertEquals(x, v.x, eps, "x");
        assertEquals(y, v.y, eps, "y");
        assertEquals(z, v.z, eps, "z");
        assertEquals(w, v.w, eps, "w");
    }

    @Test void defaultConstructorInitializesToZero() {
        Vector4f v = new Vector4f();
        assertVecEquals(0f, 0f, 0f, 0f, v, 0f);
    }

    @Test void valueConstructorSetsFields() {
        Vector4f v = new Vector4f(1f, 2f, 3f, 4f);
        assertVecEquals(1f, 2f, 3f, 4f, v, 0f);
    }

    @Test void setValuesMutatesAndReturnsThis() {
        Vector4f v = new Vector4f();
        Vector4f r = v.set(5f, 6f, 7f, 8f);
        assertSame(v, r);
        assertVecEquals(5f, 6f, 7f, 8f, v, 0f);
    }

    @Test void setFromVectorCopiesValues() {
        Vector4f a = new Vector4f(1f, 2f, 3f, 4f);
        Vector4f b = new Vector4f();
        b.set(a);
        assertVecEquals(1f, 2f, 3f, 4f, b, 0f);
        // ensure independence after copy
        a.set(9f, 8f, 7f, 6f);
        assertVecEquals(1f, 2f, 3f, 4f, b, 0f);
    }

    @Test void scaleMultipliesEachComponent() {
        Vector4f v = new Vector4f(1f, -2f, 0.5f, 4f);
        v.scale(2f);
        assertVecEquals(2f, -4f, 1f, 8f, v, 1e-6f);
    }
}
