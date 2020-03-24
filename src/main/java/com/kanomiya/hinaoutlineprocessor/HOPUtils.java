package com.kanomiya.hinaoutlineprocessor;

import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPUtils
{
    public static final Font BIG_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    public static String colorCode(Color color) {
        return "#" + Integer.toHexString(color.getRGB() & 0xffffff);
    }

    public static Set<Component> findComponents(Container container) {
        HashSet<Component> set = new HashSet<>();
        set.add(container);

        for (Component awt: container.getComponents()) {
            set.add(awt);

            if (awt instanceof Container)
               set.addAll(findComponents((Container) awt));

            if (awt instanceof JComponent) {
                JComponent swing = (JComponent) awt;
                if (swing.getComponentPopupMenu() != null)
                    set.addAll(findComponents(swing.getComponentPopupMenu()));
            }
        }

        if (container instanceof JComponent) {
            JComponent swing = (JComponent) container;
            if (swing.getComponentPopupMenu() != null)
                set.addAll(findComponents(swing.getComponentPopupMenu()));
        }

        if (container instanceof MenuElement) {
            for (MenuElement element: ((MenuElement) container).getSubElements()) {
                if (element instanceof Component) {
                    set.add((Component) element);

                    if (element instanceof Container)
                        set.addAll(findComponents((Container) element));
                }
            }
        }

        return set;
    }

    public static <T> T[] array(T... items) {
        return items;
    }


    public static JComponent i18n(JComponent component, String id) {
        component.putClientProperty(HOPAssets.HOP_I18N_ID, id);
        return component;
    }

    public static JComponent titledBorder(String title, JComponent component) {
        component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title));
        return component;
    }

    public static JLabel i18nLabel(String id) {
        JLabel label = new JLabel();
        label.setOpaque(false);
        i18n(label, id);
        return label;
    }

    public static JButton i18nButton(String id, String command, ActionListener listener) {
        JButton button = new JButton();
        i18n(button, id);
        button.setActionCommand(command);
        button.addActionListener(listener);
        return button;
    }

    public static JPanel panelGroup(Component[][] components) {
        return panelGroup(true, true, components);
    }

    public static JPanel panelGroup(boolean containerGaps, boolean gaps, Component[][] components) {
        JPanel host = new JPanel();
        host.setOpaque(false);
        doGrouping(host, containerGaps, gaps, components);
        return host;
    }

    public static void doGrouping(Container host, Component[][] components) {
        doGrouping(host, true, true, components);
    }

    public static void doGrouping(Container host, boolean containerGaps, boolean gaps, Component[][] components) {
        GroupLayout layout = new GroupLayout(host);
        layout.setAutoCreateContainerGaps(containerGaps);
        layout.setAutoCreateGaps(gaps);

        host.setLayout(layout);
        doGrouping(layout, components);
    }

    public static void doGrouping(GroupLayout layout, Component[][] components) {
        int w = components[0].length;
        int h = components.length;

        {
            GroupLayout.SequentialGroup hg = layout.createSequentialGroup();

            for (int x=0; x<w; x++) {
                GroupLayout.ParallelGroup pg = layout.createParallelGroup();

                for (int y=0; y<h; y++) {
                    if (components[y][x] == null)
                        components[y][x] = new JPanel();

                    pg.addComponent(components[y][x]);
                }

                hg.addGroup(pg);
            }

            layout.setHorizontalGroup(hg);
        }


        {
            GroupLayout.SequentialGroup vg = layout.createSequentialGroup();

            for (int y=0; y<h; y++) {
                GroupLayout.ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

                for (int x=0; x<w; x++) {
                    pg.addComponent(components[y][x]);
                }

                vg.addGroup(pg);
            }

            layout.setVerticalGroup(vg);
        }
    }


}
