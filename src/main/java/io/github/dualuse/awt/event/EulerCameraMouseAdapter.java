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
