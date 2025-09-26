package components;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import static util.Constants.*;

public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener {

    ArrayList<CanvasObject> canvasObjects = new ArrayList<>();

    public DrawingPanel() {
        setBackground(drawingPanelColor);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(Color.red);
        for (CanvasObject obj : canvasObjects) {
            g2.fillRect(obj.x, obj.y, obj.width, obj.height);
        }

        repaint();
        g2.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    int x, y;

    @Override
    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        CanvasObject canvasObject = new CanvasObject();
        canvasObject.x = Math.min(x, e.getX());
        canvasObject.y = Math.min(y, e.getY());
        canvasObject.width = Math.abs(e.getX() - x);
        canvasObject.height = Math.abs(e.getY() - y);
        canvasObjects.add(canvasObject);
        x = -1;
        y = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}
