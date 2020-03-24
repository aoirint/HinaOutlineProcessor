package com.kanomiya.hinaoutlineprocessor;

import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.structure.HOPNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPNodeTreeCellRenderer extends JLabel implements TreeCellRenderer
{
    private HOPAssets assets;
    private HOPSettings settings;

    public HOPNodeTreeCellRenderer(HOPAssets assets, HOPSettings settings) {
        this.assets = assets;
        this.settings = settings;
        setOpaque(true);
    }

    public void updateUI() {
        super.updateUI();

        Insets margins = UIManager.getInsets("Tree.rendererMargins");
        if (margins != null)
            setBorder(new EmptyBorder(margins.top, margins.left, margins.bottom, margins.right));

        setName("Tree.cellRenderer");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        if (value instanceof HOPNode)
        {
            HOPNode node = (HOPNode) value;

            setText(node.title);
            setIcon(assets.icons.getMarkerIcon(node.marker));

            setForeground(! selected ? tree.getForeground() : assets.getThemeColor("treeComponent.selectionForeground"));
            setBackground(! selected ? tree.getBackground() : assets.getThemeColor("treeComponent.selectionBackground"));
        }



        return this;
    }
}
