package components;

import tools.MarkTool;
import tools.RectTool;

import javax.swing.*;
import java.awt.*;

import static util.Constants.sidePanelColor;
import static util.Constants.sidePanelWidth;

public class LeftPanel {

    public JPanel leftPanel;
    private DrawingPanel drawingPanel;

    public LeftPanel(DrawingPanel drawingPanel) {
        init(drawingPanel);
    }

    public JPanel getPanel() {
        return leftPanel;
    }

    private void init(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(sidePanelWidth, 0));
        leftPanel.setBackground(sidePanelColor);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Mark button
        JButton markBtn = new JButton("Mark");
        markBtn.addActionListener(e -> drawingPanel.setCurrentTool(new MarkTool()));
        markBtn.setPreferredSize(new Dimension(64, 32));
        markBtn.setMinimumSize(new Dimension(64, 32));
        markBtn.setMaximumSize(new Dimension(64, 32));
        // Rectangle button
        JButton rectBtn = new JButton("Rect");
        rectBtn.addActionListener(e -> drawingPanel.setCurrentTool(new RectTool()));
        rectBtn.setPreferredSize(new Dimension(64, 32));
        rectBtn.setMinimumSize(new Dimension(64, 32));
        rectBtn.setMaximumSize(new Dimension(64, 32));
        // Group mark and rectangle buttons in a row
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.add(markBtn);
        row1.add(Box.createRigidArea(new Dimension(5, 0)));
        row1.add(rectBtn);
        row1.setBackground(sidePanelColor);
        leftPanel.add(row1);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
}
