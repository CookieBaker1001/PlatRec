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
        pickedResizeAnchor = getAnchorAtPoint(panel.getMarkedObjects(), panel.screenToCanvas(point));
        System.out.println("Picked anchor: " + pickedResizeAnchor);
        if (pickedResizeAnchor != -1) {
            resizing = true;
            return;
        }
        if (panel.checkPointMark(point)) {
            anyObjectsMarked = true;
            panel.select(true);
        } else {
            anyObjectsMarked = false;
            panel.select(false);
            panel.unmarkAll();
        }
    }
    @Override
    public void mouseDragged(MouseEvent e, DrawingPanel panel) {
        if (resizing) {
            panel.warpRectangle(pickedResizeAnchor, e.getPoint());
            return;
        }
        if (anyObjectsMarked) {
            panel.moveMarkedObjects(e.getPoint());
            movingObjects = true;
        }
    }
    @Override
    public void mouseReleased(MouseEvent e, DrawingPanel panel) {
        resizing = false;
        if (!movingObjects) anyObjectsMarked = panel.markArea(point, e.getPoint());
        if (!anyObjectsMarked) {
            panel.unmarkAll();
            movingObjects = false;
        }
        if (e.getPoint().equals(point)) {
            if (panel.markPoint(point)) anyObjectsMarked = true;
            else {
                panel.select(false);
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

    private int getAnchorAtPoint(Rectangle rect, Point p) {
        if (rect == null) return -1;
        // 0: top-left, 1: top-right, 2: bottom-right, 3: bottom-left
        int[][] anchors = {
                {rect.x, rect.y},
                {rect.x + rect.width, rect.y},
                {rect.x + rect.width, rect.y + rect.height},
                {rect.x, rect.y + rect.height}
        };
        //System.out.println("Anchors: " + java.util.Arrays.deepToString(anchors));
        //System.out.println("Point: " + p);
        for (int i = 0; i < anchors.length; i++) {
            Rectangle r = new Rectangle(anchors[i][0] - ANCHOR_SIZE / 2, anchors[i][1] - ANCHOR_SIZE / 2, ANCHOR_SIZE, ANCHOR_SIZE);
            if (r.contains(p)) return i;
        }
        return -1;
    }
}
