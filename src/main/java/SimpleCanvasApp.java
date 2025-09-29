import components.DrawingPanel;
import components.LeftPanel;
import components.RightPanel;
import tools.MarkTool;
import tools.RectTool;

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

        DrawingPanel drawingPanel = new DrawingPanel(1000, 700);
        drawingPanel.setBorder(null);

        // Wrap the DrawingPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(drawingPanel);
        scrollPane.setBorder(null);

        //frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(drawingPanel, BorderLayout.CENTER);

        LeftPanel leftPanel = new LeftPanel(drawingPanel);
        frame.add(leftPanel.getPanel(), BorderLayout.WEST);

        RightPanel rightPanel = new RightPanel(frame, drawingPanel);
        frame.add(rightPanel.getPanel(), BorderLayout.EAST);

        frame.setVisible(true);
        //drawingPanel.requestFocusInWindow();
        drawingPanel.centerCanvas();
    }
}
