package com.kanomiya.hinaoutlineprocessor.assets.langdoc;

import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by Kanomiya in 2017/02.
 */
public class LanguageListCellRenderer extends JLabel implements ListCellRenderer<Language>
{
    private final HOPAssets assets;

    public LanguageListCellRenderer(HOPAssets assets) {
        this.assets = assets;
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Language> list, Language value, int index, boolean isSelected, boolean cellHasFocus)
    {
        setText(value.name);

        if (! value.flagFileName.isEmpty())
            setIcon(assets.icons.getFlagIcon(value.flagFileName));

        setForeground(! isSelected ? list.getForeground() : list.getSelectionForeground());
        setBackground(! isSelected ? list.getBackground() : list.getSelectionBackground());

        setEnabled(list.isEnabled());
        setFont(list.getFont());

        Border border = null;
        if (cellHasFocus) {
            if (isSelected) {
                border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("List.focusCellHighlightBorder");
            }
        } else {
            border = UIManager.getBorder("List.cellNoFocusBorder");
        }
        setBorder(border);
        return this;
    }
}
