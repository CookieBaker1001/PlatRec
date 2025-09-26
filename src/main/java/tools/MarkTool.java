package tools;

import components.CanvasObject;
import components.DrawingPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MarkTool implements Tool {

    Point point = null;
    boolean anyObjectsMarked = false;
    boolean movingObjects = false;
    @Override
    public void mousePressed(MouseEvent e, DrawingPanel panel) {
        point = e.getPoint();
        if (panel.checkPointMark(point)) {
            anyObjectsMarked = true;
        } else {
            anyObjectsMarked = false;
            panel.unmarkAll();
        }
    }
    @Override
    public void mouseDragged(MouseEvent e, DrawingPanel panel) {
        if (anyObjectsMarked) {
            panel.moveMarkedObjects(e.getPoint());
            movingObjects = true;
        }
    }
    @Override
    public void mouseReleased(MouseEvent e, DrawingPanel panel) {
        if (!movingObjects) anyObjectsMarked = panel.markArea(point, e.getPoint());
        if (!anyObjectsMarked) {
            panel.unmarkAll();
            movingObjects = false;
        }
    }
    @Override
    public void mouseMoved(MouseEvent e, DrawingPanel panel) {}
    @Override
    public void keyPressed(KeyEvent e, DrawingPanel panel) {}
    @Override
    public void keyReleased(KeyEvent e, DrawingPanel panel) {}
}
