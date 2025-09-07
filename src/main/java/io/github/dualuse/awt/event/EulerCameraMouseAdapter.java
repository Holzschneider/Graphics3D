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
package io.github.dualuse.awt.event;

import io.github.dualuse.awt.Graphics3D;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

public class EulerCameraMouseAdapter extends MouseAdapter {

    Point2D euler, shift, dist;

    public EulerCameraMouseAdapter(Point2D euler, Point2D shift, Point2D dist) {
        this.euler = euler;
        this.shift = shift;
        this.dist = dist;
    }

    public void apply(Graphics3D g3) {
        g3.rotate(dist.getX(), 0, 0, 1);
        g3.translate(0, 0, dist.getY());

        g3.rotate(euler.getY(), 1, 0, 0);
        g3.rotate(euler.getX(), 0, 1, 0);
        g3.translate(shift.getX(), 0, shift.getY());
    }


    MouseEvent last = null;

    public void mouseMoved(MouseEvent e) {
        last = e;
    }

    public void mouseDragged(MouseEvent e) {
        if (last == null) return;

        double Z = 3. / ((Component) e.getSource()).getWidth();

        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == 0)
            if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == 0)
                dist.setLocation(dist.getX(), dist.getY() + (e.getY() - last.getY()) * 0.04);
            else
                shift.setLocation(shift.getX() + (e.getX() - last.getX()) * Z, shift.getY() + (e.getY() - last.getY()) * Z);
        else
            euler.setLocation(euler.getX() + (e.getX() - last.getX()) * Z, euler.getY() + (e.getY() - last.getY()) * Z);

        last = e;

        ((Component) e.getSource()).repaint();
    }

    public void reset() {
        this.euler.setLocation(0, 0);
        this.shift.setLocation(0, 0);
        this.dist.setLocation(0, 0);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getModifiers() == 1)
            dist.setLocation(dist.getX() + e.getWheelRotation() * 0.04, dist.getY());
        else
            dist.setLocation(dist.getX(), dist.getY() + e.getWheelRotation() * 0.04);

        ((Component) e.getSource()).repaint();
    }

}
