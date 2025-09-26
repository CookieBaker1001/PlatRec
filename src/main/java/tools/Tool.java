package tools;

import components.DrawingPanel;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface Tool {
    void mousePressed(MouseEvent e, DrawingPanel panel);
    void mouseDragged(MouseEvent e, DrawingPanel panel);
    void mouseReleased(MouseEvent e, DrawingPanel panel);
    void mouseMoved(MouseEvent e, DrawingPanel panel);

    void keyPressed(KeyEvent e, DrawingPanel panel);
    void keyReleased(KeyEvent e, DrawingPanel panel);
}
