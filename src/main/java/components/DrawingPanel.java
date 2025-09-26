package components;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import static util.Constants.*;

public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener {

    private final ArrayList<CanvasObject> canvasObjects = new ArrayList<>();
    private final Point originOfClick = new Point(-1, -1);

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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.translate(offsetX, offsetY);
        g2.scale(zoom, zoom);
        drawCanvasBoard(g2);

        g2.setColor(Color.red);
        for (CanvasObject obj : canvasObjects) {
            g2.fillRect(obj.x, obj.y, obj.width, obj.height);
        }

        repaint();
        g2.dispose();
    }

    private void drawCanvasBoard(Graphics2D g2) {
        g2.setColor(drawingCanvasColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            isPanning = true;
            panStartPoint = e.getPoint();
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            Point p = screenToCanvas(e.getPoint());
            originOfClick.x = p.x;
            originOfClick.y = p.y;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            isPanning = false;
            panStartPoint = null;
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            Point p = screenToCanvas(e.getPoint());
            CanvasObject canvasObject = new CanvasObject();
            canvasObject.x = Math.min(originOfClick.x, p.x);
            canvasObject.y = Math.min(originOfClick.y, p.y);
            canvasObject.width = Math.abs(p.x - originOfClick.x);
            canvasObject.height = Math.abs(p.y - originOfClick.y);
            canvasObjects.add(canvasObject);
            originOfClick.x = -1;
            originOfClick.y = -1;
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
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}
