import components.DrawingPanel;
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

        DrawingPanel drawingPanel = new DrawingPanel();
        drawingPanel.setBorder(null);
        frame.add(drawingPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(sidePanelWidth, frame.getHeight()));
        leftPanel.setBackground(sidePanelColor);

        JButton markBtn = new JButton("Mark");
        markBtn.addActionListener(e -> drawingPanel.setCurrentTool(new MarkTool()));
        markBtn.setPreferredSize(new Dimension(64, 32));
        markBtn.setMinimumSize(new Dimension(64, 32));
        markBtn.setMaximumSize(new Dimension(64, 32));

        JButton rectBtn = new JButton("Rect");
        rectBtn.addActionListener(e -> drawingPanel.setCurrentTool(new RectTool()));
        rectBtn.setPreferredSize(new Dimension(64, 32));
        rectBtn.setMinimumSize(new Dimension(64, 32));
        rectBtn.setMaximumSize(new Dimension(64, 32));

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.add(markBtn);
        row1.add(Box.createRigidArea(new Dimension(0, 5)));
        row1.add(rectBtn);
        row1.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(row1, BorderLayout.NORTH);

        frame.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(sidePanelWidth, frame.getHeight()));
        rightPanel.setBackground(sidePanelColor);
        frame.add(rightPanel, BorderLayout.EAST);

        frame.setVisible(true);
        //drawingPanel.requestFocusInWindow();
    }
}
