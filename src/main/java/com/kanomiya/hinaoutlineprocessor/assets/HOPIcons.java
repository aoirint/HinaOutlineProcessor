package com.kanomiya.hinaoutlineprocessor.assets;

import com.kanomiya.hinaoutlineprocessor.structure.HOPNodeMarker;
import com.kanomiya.hinaoutlineprocessor.structure.HOPNodeMarkerType;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPIcons {
    private final Map<HOPNodeMarkerType, BufferedImage> markerImages;
    private final Map<String, ImageIcon> flagIcons;

    public final BufferedImage appIcon;
    private final Map<HOPNodeMarker, ImageIcon> markerIconCaches = new HashMap<>();
    private final Map<String, ImageIcon> iconCaches = new HashMap<>();

    public HOPIcons(BufferedImage appIcon, Map<HOPNodeMarkerType, BufferedImage> markerImages, Map<String, ImageIcon> flagIcons) {
        this.appIcon = appIcon;
        this.markerImages = markerImages;
        this.flagIcons = flagIcons;
    }

    public ImageIcon getMarkerIcon(HOPNodeMarker marker) {
        if (markerIconCaches.containsKey(marker)) return markerIconCaches.get(marker);

        float[] markerHSBs = Color.RGBtoHSB(marker.color.getRed(), marker.color.getGreen(), marker.color.getBlue(), null);

        BufferedImage image = markerImages.get(marker.type);

        int[] pixels = image.getRGB(0, 0, 16, 16, null, 0, 16);

        for (int i=0, len=pixels.length; i<len; i++)
        {
            int org_a = pixels[i] >> 24;
            int org_r = (pixels[i] >> 16) & 0xff;
            int org_g = (pixels[i] >> 8) & 0xff;
            int org_b = pixels[i] & 0xff;

            float[] orgHSBs = Color.RGBtoHSB(org_r, org_g, org_b, null);

            Color post = Color.getHSBColor(markerHSBs[0], markerHSBs[1], orgHSBs[2]);

            pixels[i] = (org_a << 24) | (post.getRGB() & 0xffffff);
        }

        BufferedImage result = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0,0,16,16, pixels,0,16);

        ImageIcon icon = new ImageIcon(result);
        markerIconCaches.put(marker, icon);
        iconCaches.put(marker.toString(), icon);

        return icon;
    }

    public ImageIcon getFlagIcon(String localeCode) {
        return flagIcons.get(localeCode);
    }

    public ImageIcon parseIconString(String string) {
        if (iconCaches.containsKey(string)) return iconCaches.get(string);

        if (string.startsWith("tree.node.marker"))
            return getMarkerIcon(HOPNodeMarker.parse(string.split(" ", 2)[1]));

        return null;
    }

}
