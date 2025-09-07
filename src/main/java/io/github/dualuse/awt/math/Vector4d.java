/*
 * This file is part of Graphics3D.
 *
 * Graphics3D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Graphics3D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Graphics3D.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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