package GUI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;

public class UITheme {
    public static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    public static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    public static final Color SURFACE_COLOR = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(189, 195, 199);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

    // Load an absolute path background image. Returns null if not found.
    public static Image loadBackgroundImageAbsolute(String absolutePath) {
        try {
            if (absolutePath == null) return null;
            File f = new File(absolutePath);
            if (!f.exists()) return null;
            ImageIcon icon = new ImageIcon(absolutePath);
            return icon.getImage();
        } catch (Exception e) {
            System.err.println("UITheme: could not load background image: " + e.getMessage());
            return null;
        }
    }

    // Convenience: style a button minimally
    public static void styleButton(JButton b) {
        b.setBackground(PRIMARY_COLOR);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Load and scale an absolute icon path. Returns null if not found.
    public static ImageIcon loadIconAbsolute(String absolutePath, int w, int h) {
        try {
            if (absolutePath == null) return null;
            File f = new File(absolutePath);
            if (!f.exists()) return null;
            ImageIcon icon = new ImageIcon(absolutePath);
            Image i = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(i);
        } catch (Exception e) {
            return null;
        }
    }

    // Create a small rounded letter/icon image for buttons (fallback when no external icons available)
    public static ImageIcon createLetterIcon(String text, Color bg, Color fg, int size) {
        try {
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            int arc = Math.max(6, size / 6);
            g2.fillRoundRect(0, 0, size, size, arc, arc);
            g2.setColor(fg);
            g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(10, size / 2)));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (size - fm.stringWidth(text)) / 2;
            int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
            g2.dispose();
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    // Style a text field for better spacing and readability
    public static void styleTextField(JTextField tf) {
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        tf.setBackground(SURFACE_COLOR);
        tf.setForeground(TEXT_COLOR);
    }

    // Style a table for improved readability
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_COLOR.darker());
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBackground(SURFACE_COLOR);
        table.getTableHeader().setForeground(TEXT_COLOR);
    }
}
