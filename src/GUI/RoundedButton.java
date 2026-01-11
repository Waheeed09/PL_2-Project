package GUI;

import java.awt.*;
import javax.swing.*;

public class RoundedButton extends JButton {
    private boolean isHovered = false;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(UITheme.FONT_BODY);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        
        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }

    public RoundedButton(String text, Icon icon) {
        this(text);
        setIcon(icon);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = getBackground();
        if (!isEnabled()) {
            bg = bg.darker();
        } else if (isHovered) {
            bg = bg.brighter();
        }
        
        int arc = 14;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        
        // Add subtle border
        g2.setColor(bg.darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = Math.max(d.height, 40);
        d.width = Math.max(d.width, 100);
        return d;
    }
}
