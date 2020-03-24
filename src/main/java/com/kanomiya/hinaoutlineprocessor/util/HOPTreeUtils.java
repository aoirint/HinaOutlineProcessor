package com.kanomiya.hinaoutlineprocessor.util;

import com.kanomiya.hinaoutlineprocessor.structure.HOPNode;
import com.kanomiya.hinaoutlineprocessor.structure.HOPNodeMarkerType;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPTreeUtils
{

    public static void changeMarkerShapeSelected(JTree treeComponent, HOPNodeMarkerType markerType) {
        if (treeComponent.getSelectionPath() != null && treeComponent.getSelectionPath().getLastPathComponent() instanceof HOPNode)
            changeMarkerShape(treeComponent, (HOPNode) treeComponent.getSelectionPath().getLastPathComponent(), markerType);
    }

    public static void changeMarkerShape(JTree treeComponent, HOPNode targetNode, HOPNodeMarkerType markerType) {

        targetNode.marker.type = markerType;
        treeComponent.repaint();
    }

    public static void changeMarkerColorSelected(JTree treeComponent, Color markerColor) {
        if (treeComponent.getSelectionPath() != null && treeComponent.getSelectionPath().getLastPathComponent() instanceof HOPNode)
            changeMarkerColor(treeComponent, (HOPNode) treeComponent.getSelectionPath().getLastPathComponent(), markerColor);
    }

    public static void changeMarkerColor(JTree treeComponent, HOPNode targetNode, Color markerColor) {

        targetNode.marker.color = markerColor;
        treeComponent.repaint();
    }

}
