package io.github.dualuse.awt.math;

public class Vector4d {
    public double x, y, z, w;

    public Vector4d() { this(0,0,0,0); }
    public Vector4d(double x, double y, double z, double w) {
        this.x = x; this.y = y; this.z = z; this.w = w;
    }

    public Vector4d set(double x, double y, double z, double w) {
        this.x = x; this.y = y; this.z = z; this.w = w; return this;
    }

    public Vector4d set(Vector4d v) {
        return set(v.x, v.y, v.z, v.w);
    }

    public void scale(double s) {
        this.x *= s; this.y *= s; this.z *= s; this.w *= s;
    }
}