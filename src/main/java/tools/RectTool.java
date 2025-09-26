package tools;

import components.DrawingPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class RectTool implements Tool {
    Point startPoint = null;
    @Override
    public void mousePressed(MouseEvent e, DrawingPanel panel) {
        startPoint = e.getPoint();
    }
    @Override
    public void mouseDragged(MouseEvent e, DrawingPanel panel) {}
    @Override
    public void mouseReleased(MouseEvent e, DrawingPanel panel) {
        panel.drawRectangle(startPoint, e.getPoint());
    }
    @Override
    public void mouseMoved(MouseEvent e, DrawingPanel panel) {}
    @Override
    public void keyPressed(KeyEvent e, DrawingPanel panel) {}
    @Override
    public void keyReleased(KeyEvent e, DrawingPanel panel) {}
}
