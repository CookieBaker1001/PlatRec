package components;

import tools.MarkTool;
import tools.RectTool;
import tools.Tool;
import util.SaveAndLoad;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static util.Constants.*;

public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener {

    private int panelWidth, panelHeight;

    private final ArrayList<CanvasObject> canvasObjects = new ArrayList<>();
    private Tool currentTool = null;

    private Point lastDragPoint = null;

    private double zoom = 0.55;
    private double offsetX = 37;
    private double offsetY = 124;
    private boolean isPanning = false;
    private Point panStartPoint = new Point(0, 0);

    private boolean objectsSelected = false;
    private int[][] currentAnchorPoints = null;
    private Rectangle sb = null;

    public DrawingPanel(int width, int height) {
        setCanvasSize(width, height);
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

        drawObjects(g2);

        repaint();
        g2.dispose();
    }

    public int getCanvasWidth() {
        return panelWidth;
    }

    public int getCanvasHeight() {
        return panelHeight;
    }

    public ArrayList<CanvasObject> getCanvasObjects() {
        return canvasObjects;
    }

    public void centerCanvas() {
        //System.out.println("Centering canvas...");
        double canvasWidth = panelWidth * zoom;
        double canvasHeight = panelHeight * zoom;
        offsetX = (getWidth() - canvasWidth) / 2;
        offsetY = (getHeight() - canvasHeight) / 2;
        //System.out.println("offsetX: " + offsetX + ", offsetY: " + offsetY);
        repaint();
    }

    public void setCanvasSize(int width, int height) {
        this.panelWidth = width;
        this.panelHeight = height;
        //System.out.println("Canvas size set to: " + panelWidth + "x" + panelHeight);
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        //setSize(panelWidth, panelHeight);
        centerCanvas();
        revalidate();
        repaint();
    }

    public void export(File file) throws IOException {
        try {
            SaveAndLoad.export(file, canvasObjects, panelWidth, panelHeight);
        } catch (IOException e) {
            throw e;
        }
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
            Rectangle objRect = new Rectangle((int) canvasObject.x, (int) canvasObject.y, (int) canvasObject.width, (int) canvasObject.height);
            if (objRect.contains(canvasPoint)) return true;
        }
        return false;
    }

    public void select(boolean selected) {
        this.objectsSelected = selected;
        sb = getSelectionBounds();
        repaint();
    }

    private void drawObjects(Graphics2D g2) {
        for (CanvasObject obj : canvasObjects) {
            g2.setColor(obj.color);
            AffineTransform old = g2.getTransform();
            g2.translate(obj.x + obj.width/2.0, obj.y + obj.height/2.0);
            g2.rotate(Math.toRadians(obj.angle));
            g2.translate(-obj.width/2.0, -obj.height/2.0);
            g2.fillRect(0, 0, (int) obj.width, (int) obj.height);
            g2.setTransform(old);
        }
        sb = getSelectionBounds();
        if (sb != null) {
            AffineTransform old = g2.getTransform();
            g2.setColor(Color.BLACK);
            float[] dash = {4f, 4f};
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dash, 0));
            g2.drawRect(sb.x, sb.y, sb.width, sb.height);
            g2.setTransform(old);
            g2.setColor(Color.WHITE);
            currentAnchorPoints = new int[][]{
                    {sb.x, sb.y}, {sb.x + sb.width, sb.y}, {sb.x + sb.width, sb.y + sb.height}, {sb.x, sb.y + sb.height}
            };
            for (int[] pt : currentAnchorPoints) {
                g2.setStroke(new BasicStroke(3));
                g2.fillRect(pt[0] - ANCHOR_SIZE/2, pt[1] - ANCHOR_SIZE/2, ANCHOR_SIZE, ANCHOR_SIZE);
                g2.setColor(Color.BLACK);
                g2.drawRect(pt[0] - ANCHOR_SIZE/2, pt[1] - ANCHOR_SIZE/2, ANCHOR_SIZE, ANCHOR_SIZE);
                g2.setColor(Color.WHITE);
            }
            g2.setTransform(old);
        }
    }

    public void warpRectangle(int anchor, Point newPoint) {
        //System.out.println("Warping rectangle at anchor " + anchor + " to point " + newPoint);
        if (sb == null || sb.width == 0 || sb.height == 0) return;
        Point canvasPoint = screenToCanvas(newPoint);

        int fixedX = 0, fixedY = 0;
        switch (anchor) {
            case 0 -> { fixedX = sb.x + sb.width; fixedY = sb.y + sb.height; }
            case 1 -> { fixedX = sb.x; fixedY = sb.y + sb.height; }
            case 2 -> { fixedX = sb.x; fixedY = sb.y; }
            case 3 -> { fixedX = sb.x + sb.width; fixedY = sb.y; }
            default -> { return; }
        }

        int minX = Math.min(fixedX, canvasPoint.x);
        int minY = Math.min(fixedY, canvasPoint.y);
        int maxX = Math.max(fixedX, canvasPoint.x);
        int maxY = Math.max(fixedY, canvasPoint.y);

        int newWidth = maxX - minX;
        int newHeight = maxY - minY;
        if (newWidth < 5 || newHeight < 5) return;

        for (CanvasObject obj : canvasObjects) {
            if (obj.selected) {
                double relX = (obj.x - sb.x) / (double) sb.width;
                double relY = (obj.y - sb.y) / (double) sb.height;
                obj.x = (minX + relX * newWidth);
                obj.y = (minY + relY * newHeight);
                obj.width = (obj.width * (newWidth / (double) sb.width));
                obj.height = (obj.height * (newHeight / (double) sb.height));
            }
        }

        switch (anchor) {
            case 0 -> { sb.x = minX; sb.y = minY; }
            case 1 -> { sb.x = maxX; sb.y = minY; }
            case 2 -> { sb.x = maxX; sb.y = maxY; }
            case 3 -> { sb.x = minX; sb.y = maxY; }
        }

        repaint();
    }

    public Rectangle getMarkedObjects() {
        if (!objectsSelected) return null;
        return sb;
    }

    public Rectangle getSelectionBounds() {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        boolean anySelected = false;
        for (CanvasObject obj : canvasObjects) {
            if (!obj.selected) continue;
            anySelected = true;
            minX = (int) Math.min(minX, obj.x);
            minY = (int) Math.min(minY, obj.y);
            maxX = (int) Math.max(maxX, obj.x + obj.width);
            maxY = (int) Math.max(maxY, obj.y + obj.height);
        }
        if (!anySelected) return null;
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private void drawCanvasBoard(Graphics2D g2) {
        g2.setColor(drawingCanvasColor);
        g2.fillRect(0, 0, panelWidth, panelHeight);
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

    public boolean markPoint(Point point) {
        Point canvasPoint = screenToCanvas(point);
        boolean marked = false;
        for (CanvasObject obj : canvasObjects) {
            Rectangle objRect = new Rectangle((int) obj.x, (int) obj.y, (int) obj.width, (int) obj.height);
            if (objRect.contains(canvasPoint)) {
                obj.selected = true;
                marked = true;
            }
        }
        return marked;
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
            Rectangle objRect = new Rectangle((int) obj.x, (int) obj.y, (int) obj.width, (int) obj.height);
            obj.selected = selectionRect.intersects(objRect) || objRect.contains(p1);
            if (obj.selected) marked = true;
        }
        return marked;
    }

    public void unmarkAll() {
        for (CanvasObject obj : canvasObjects) {
            obj.selected = false;
            select(false);
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("Mouse clicked at: " + screenToCanvas(e.getPoint()));
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
