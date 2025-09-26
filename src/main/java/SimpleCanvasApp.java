import components.DrawingPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static util.Constants.*;

public class SimpleCanvasApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleCanvasApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("PlatRec - v1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.getContentPane().setBackground(mainBGcolor);
        frame.setLayout(new BorderLayout(10, 10));
        ImageIcon appIcon = new ImageIcon(Objects.requireNonNull(SimpleCanvasApp.class.getResource("/icon.png")));
        frame.setIconImage(appIcon.getImage());

        DrawingPanel drawingPanel = new DrawingPanel();
        drawingPanel.setBorder(null);
        frame.add(drawingPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(sidePanelWidth, frame.getHeight()));
        leftPanel.setBackground(sidePanelColor);
        frame.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(sidePanelWidth, frame.getHeight()));
        rightPanel.setBackground(sidePanelColor);
        frame.add(rightPanel, BorderLayout.EAST);

        frame.setVisible(true);
        //drawingPanel.requestFocusInWindow();
    }
}
