package util;

import components.CanvasObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static javax.swing.text.StyleConstants.getBackground;

public class SaveAndLoad {
    // Placeholder for save and load functionality
    public static void saveToFile(String filename) {
        // Implement saving logic here
    }

    public static void loadFromFile(String filename) {
        // Implement loading logic here
    }

    public static void export(File file, ArrayList<CanvasObject> canvasObjects, int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Set up a clean transform (no zoom/pan)
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // Draw the canvas board and objects at 1:1 scale
        for (CanvasObject obj : canvasObjects) {
            g2.setColor(obj.color);
            g2.fillRect(obj.x, obj.y, obj.width, obj.height);
        }

        g2.dispose();
        ImageIO.write(image, "png", file);
    }
}
