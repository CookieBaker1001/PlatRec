package components;

import tools.MarkTool;
import tools.RectTool;
import tools.Tool;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import static util.Constants.*;

public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener {

    private final ArrayList<CanvasObject> canvasObjects = new ArrayList<>();
    private Tool currentTool = null;

    private Point lastDragPoint = null;

    private double zoom = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private boolean isPanning = false;
    private Point panStartPoint = new Point(0, 0);

    public DrawingPanel() {
        setBackground(drawingPanelColor);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListenerHelper();
        currentTool = new MarkTool();
    }

    public void setCurrentTool(Tool tool) {
        this.currentTool = tool;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.translate(offsetX, offsetY);
        g2.scale(zoom, zoom);
        drawCanvasBoard(g2);

        g2.setColor(Color.red);

        drawObjects(g2);

        repaint();
        g2.dispose();
    }

    public boolean checkPointMark(Point point) {
        boolean result = false;
        for (CanvasObject canvasObject : canvasObjects) {
            if (canvasObject.selected) {
                result = true;
                break;
            }
        }
        if (!result) return false;
        Point canvasPoint = screenToCanvas(point);
        for (CanvasObject canvasObject : canvasObjects) {
            Rectangle objRect = new Rectangle(canvasObject.x, canvasObject.y, canvasObject.width, canvasObject.height);
            if (objRect.contains(canvasPoint)) return true;
        }
        return false;
    }

    private void drawObjects(Graphics2D g2) {
        for (CanvasObject obj : canvasObjects) {
            g2.fillRect(obj.x, obj.y, obj.width, obj.height);
            if (obj.selected) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(obj.x, obj.y, obj.width, obj.height);
                g2.setColor(Color.red);
            }
        }
    }

    private void drawCanvasBoard(Graphics2D g2) {
        g2.setColor(drawingCanvasColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    public void drawRectangle(Point p1, Point p2) {
        Point startPoint = screenToCanvas(p1);
        Point endPoint = screenToCanvas(p2);
        CanvasObject canvasObject = new CanvasObject();
        canvasObject.x = Math.min(startPoint.x, endPoint.x);
        canvasObject.y = Math.min(startPoint.y, endPoint.y);
        canvasObject.width = Math.abs(endPoint.x - startPoint.x);
        canvasObject.height = Math.abs(endPoint.y - startPoint.y);
        canvasObjects.add(canvasObject);
    }

    public boolean markArea(Point p1, Point p2) {
        Point startPoint = screenToCanvas(p1);
        Point endPoint = screenToCanvas(p2);
        Rectangle selectionRect = new Rectangle(
                Math.min(startPoint.x, endPoint.x),
                Math.min(startPoint.y, endPoint.y),
                Math.abs(endPoint.x - startPoint.x),
                Math.abs(endPoint.y - startPoint.y)
        );
        boolean marked = false;
        for (CanvasObject obj : canvasObjects) {
            Rectangle objRect = new Rectangle(obj.x, obj.y, obj.width, obj.height);
            obj.selected = selectionRect.intersects(objRect) || objRect.contains(p1);
            if (obj.selected) marked = true;
        }
        return marked;
    }

    public void unmarkAll() {
        for (CanvasObject obj : canvasObjects) {
            obj.selected = false;
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            lastDragPoint = screenToCanvas(e.getPoint());
        }
        if (SwingUtilities.isMiddleMouseButton(e)) {
            isPanning = true;
            panStartPoint = e.getPoint();
        } else {
            currentTool.mousePressed(e, this);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastDragPoint = null;
        if (SwingUtilities.isMiddleMouseButton(e)) {
            isPanning = false;
            panStartPoint = null;
        } else {
            currentTool.mouseReleased(e, this);
        }
    }

    public Point screenToCanvas(Point p) {
        int canvasX = (int) ((p.x - offsetX) / zoom);
        int canvasY = (int) ((p.y - offsetY) / zoom);
        return new Point(canvasX, canvasY);
    }

    private void addMouseWheelListenerHelper() {
        addMouseWheelListener(e -> {
            Point mousePoint = e.getPoint();
            double zoomCenterX = (mousePoint.x - offsetX) / zoom;
            double zoomCenterY = (mousePoint.y - offsetY) / zoom;
            double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
            zoom *= zoomFactor;
            zoom = Math.max(0.1, Math.min(zoom, 10.0));
            offsetX = mousePoint.x - zoomCenterX * zoom;
            offsetY = mousePoint.y - zoomCenterY * zoom;
            repaint();
            requestFocusInWindow();
        });
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isPanning) {
            int dx = e.getX() - panStartPoint.x;
            int dy = e.getY() - panStartPoint.y;
            offsetX += dx;
            offsetY += dy;
            panStartPoint = e.getPoint();
        } else {
            currentTool.mouseDragged(e, this);
        }
    }

    public void moveMarkedObjects(Point newPoint) {
        if (lastDragPoint != null) {
            Point current = screenToCanvas(newPoint);
            int dx = current.x - lastDragPoint.x;
            int dy = current.y - lastDragPoint.y;
            for (CanvasObject obj : canvasObjects) {
                if (obj.selected) {
                    obj.x += dx;
                    obj.y += dy;
                }
            }
            lastDragPoint = current;
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}
