package tools;

import components.CanvasObject;
import components.DrawingPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static util.Constants.*;

public class MarkTool implements Tool {

    Point point = null;
    boolean anyObjectsMarked = false;
    boolean movingObjects = false;

    boolean resizing = false;
    int pickedResizeAnchor = -1;

    @Override
    public void mousePressed(MouseEvent e, DrawingPanel panel) {
        point = e.getPoint();
//        pickedResizeAnchor = getAnchorAtPoint(panel.getMarkedObjects(), point);
//        if (isOnRotateHandle(selectedObject, canvasPoint)) {
//            resizing = true;
//            return;
//        }
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
        if (e.getPoint().equals(point)) {
            if (panel.markPoint(point)) anyObjectsMarked = true;
            else {
                panel.unmarkAll();
                anyObjectsMarked = false;
            }
        }
    }
    @Override
    public void mouseMoved(MouseEvent e, DrawingPanel panel) {}
    @Override
    public void keyPressed(KeyEvent e, DrawingPanel panel) {}
    @Override
    public void keyReleased(KeyEvent e, DrawingPanel panel) {}

    private int getAnchorAtPoint(CanvasObject obj, Point p) {
        // 0: top-left, 1: top-right, 2: bottom-right, 3: bottom-left
        int[][] anchors = {
                {obj.x, obj.y},
                {obj.x + obj.width, obj.y},
                {obj.x + obj.width, obj.y + obj.height},
                {obj.x, obj.y + obj.height}
        };
        for (int i = 0; i < anchors.length; i++) {
            Rectangle r = new Rectangle(anchors[i][0] - ANCHOR_SIZE / 2, anchors[i][1] - ANCHOR_SIZE / 2, ANCHOR_SIZE, ANCHOR_SIZE);
            if (r.contains(p)) return i;
        }
        return -1;
    }
}
