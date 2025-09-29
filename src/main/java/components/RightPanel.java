package components;

import tools.MarkTool;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static util.Constants.*;

public class RightPanel {
    public JPanel rightPanel;
    private JFrame frame;
    private DrawingPanel drawingPanel;

    public RightPanel(JFrame frame, DrawingPanel drawingPanel) {
        init(frame, drawingPanel);
    }

    public JPanel getPanel() {
        return rightPanel;
    }

    private void init(JFrame frame, DrawingPanel drawingPanel) {
        this.frame = frame;
        this.drawingPanel = drawingPanel;
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(sidePanelWidth, 0));
        rightPanel.setBackground(sidePanelColor);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton centerBtn = new JButton("Center");
        centerBtn.addActionListener(e -> drawingPanel.centerCanvas());
        centerBtn.setPreferredSize(new Dimension(64, 32));
        centerBtn.setMinimumSize(new Dimension(64, 32));
        centerBtn.setMaximumSize(new Dimension(64, 32));

        JButton sizeBtn = new JButton("Size");
        sizeBtn.addActionListener(e -> {
            JPanel sizePanel = new JPanel(new GridLayout(2, 2, 5, 5));
            SpinnerNumberModel rowModel = new SpinnerNumberModel(drawingPanel.getCanvasWidth(), 32, 2048, 1);
            SpinnerNumberModel colModel = new SpinnerNumberModel(drawingPanel.getCanvasHeight(), 32, 2048, 1);
            JSpinner rowSpinner = new JSpinner(rowModel);
            JSpinner colSpinner = new JSpinner(colModel);
            sizePanel.add(new JLabel("Height:"));
            sizePanel.add(rowSpinner);
            sizePanel.add(new JLabel("Width:"));
            sizePanel.add(colSpinner);
            int result = JOptionPane.showConfirmDialog(frame, sizePanel,
                    "Set Grid Size", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result == 2 || result == -1) return;
            if (result == JOptionPane.OK_OPTION) {
                int newHeight = (Integer) rowSpinner.getValue();
                int newWidth = (Integer) colSpinner.getValue();
                drawingPanel.setCanvasSize(newHeight, newWidth);
            }
        });
        sizeBtn.setPreferredSize(new Dimension(64, 32));
        sizeBtn.setMinimumSize(new Dimension(64, 32));
        sizeBtn.setMaximumSize(new Dimension(64, 32));

        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.add(centerBtn);
        row1.add(Box.createRigidArea(new Dimension(5, 0)));
        row1.add(sizeBtn);
        row1.setBackground(sidePanelColor);
        rightPanel.add(row1);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JButton exportBtn = new JButton("Export");
        exportBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Drawing as PNG");
            fileChooser.setSelectedFile(new File("drawing.png"));
            int userSelection = fileChooser.showSaveDialog(frame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    drawingPanel.export(fileToSave);
                    JOptionPane.showMessageDialog(frame, "Saved to " + fileToSave.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving image: " + ex.getMessage(),
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        exportBtn.setPreferredSize(new Dimension(64, 32));
        exportBtn.setMinimumSize(new Dimension(64, 32));
        exportBtn.setMaximumSize(new Dimension(64, 32));
        rightPanel.add(exportBtn);
    }
}
