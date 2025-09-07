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

public class AxisAngle4d {
    public double x;
    public double y;
    public double z;
    public double angle;

    public AxisAngle4d() {}

    public AxisAngle4d(double x, double y, double z, double angle) {
        this.x = x; this.y = y; this.z = z; this.angle = angle;
    }
}