package com.kanomiya.hinaoutlineprocessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kanomiya in 2017/02.
 */
public class ColorButton extends JButton implements ActionListener
{
    static final int BOX_WIDTH = 28;
    static final int BOX_HEIGHT = 14;


    private static Map<Color, ImageIcon> icon_caches = new HashMap<>();

    private Color color;

    public ColorButton() {
        this(Color.BLACK);
    }

    public ColorButton(Color defaultColor) {
        this("", defaultColor);
    }

    public ColorButton(String text, Color defaultColor) {
        super(text);

        addActionListener(this);
        setColor(defaultColor);
    }

    public void setColor(Color newColor) {
        color = newColor;

        setIcon(getColorBoxIcon(newColor));
    }

    public Color getColor() {
        return color;
    }

    public Color dialog() {
        Color newColor = JColorChooser.showDialog(this, getText(), color);
        if (newColor != null) setColor(newColor);

        return newColor;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        dialog();
    }


    public static ImageIcon getColorBoxIcon(Color color) {
        if (icon_caches.containsKey(color)) return icon_caches.get(color);

        BufferedImage image = new BufferedImage(BOX_WIDTH, BOX_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();

        g.setColor(color);
        g.fillRect(0,0, BOX_WIDTH, BOX_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawRect(0,0, BOX_WIDTH, BOX_HEIGHT);

        ImageIcon icon = new ImageIcon(image);
        icon_caches.put(color, icon);

        return icon;
    }

}
