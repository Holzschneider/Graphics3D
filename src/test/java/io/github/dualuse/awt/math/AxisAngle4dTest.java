package io.github.dualuse.awt.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AxisAngle4dTest {

    @Test void defaultConstructorInitializesZeros() {
        AxisAngle4d aa = new AxisAngle4d();
        assertEquals(0.0, aa.x);
        assertEquals(0.0, aa.y);
        assertEquals(0.0, aa.z);
        assertEquals(0.0, aa.angle);
    }

    @Test void valueConstructorSetsFields() {
        AxisAngle4d aa = new AxisAngle4d(1.0, -2.0, 3.0, Math.PI);
        assertEquals(1.0, aa.x);
        assertEquals(-2.0, aa.y);
        assertEquals(3.0, aa.z);
        assertEquals(Math.PI, aa.angle);
    }
}
