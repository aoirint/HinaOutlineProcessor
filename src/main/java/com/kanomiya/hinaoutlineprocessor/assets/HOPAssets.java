package com.kanomiya.hinaoutlineprocessor.assets;

import com.kanomiya.hinaoutlineprocessor.HOPSettings;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPAssets
{
    public static final Object HOP_I18N_ID = new Object();
    public static final Object HOP_ITEM_SOURCE_ID = new Object();
    public static final Object HOP_I18N_ARGUMENTS_ID = new Object();

    public static final Path CONFIG_FILE = Paths.get("config.json");

    private final HOPSettings settings;

    public final HOPI18n i18n;
    public final HOPIcons icons;

    public final HOPMenus menus;
    public final HOPThemes themes;

    public HOPAssets(HOPSettings settings,
                     HOPI18n i18n,
                     HOPIcons icons,
                     HOPMenus menus,
                     HOPThemes themes)
    {
        this.settings = settings;
        this.i18n = i18n;
        this.icons = icons;
        this.menus = menus;
        this.themes = themes;
    }

    public String localize(String id, Object... args) {

        return i18n.localize(settings.view.language, id, args);
    }

    public void localizeComponent(JComponent component) {
        if (component.getClientProperty(HOP_I18N_ID) != null)
        {
            String id = String.valueOf(component.getClientProperty(HOP_I18N_ID));

            Object[] args;
            if (component.getClientProperty(HOP_I18N_ARGUMENTS_ID) instanceof Object[])
                args = (Object[]) component.getClientProperty(HOP_I18N_ARGUMENTS_ID);
            else if (component instanceof AbstractButton && ((AbstractButton) component).getMnemonic() != 0)
                args = new Object[] { String.valueOf(Character.toChars(((AbstractButton) component).getMnemonic())[0]).toUpperCase() };
            else
                args = new Object[0];

            String localized = localize(id, args);

            if (component instanceof AbstractButton)
                ((AbstractButton) component).setText(localized);
            else if (component instanceof JLabel)
                ((JLabel) component).setText(localized);
        }
    }

    public Color getThemeColor(String key) {
        return themes.find(settings.view.theme).getColor(key);
    }


}
