package com.kanomiya.hinaoutlineprocessor.util;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Created by Kanomiya in 2017/02.
 */
public class TreeUtils
{
    public static void moveUpwardSelected(JTree treeComponent) {
        if (treeComponent.getSelectionPath() != null)
            moveUpward(treeComponent, treeComponent.getSelectionPath());
    }

    public static void moveUpward(JTree treeComponent, TreePath target) {
        DefaultTreeModel treeModel = (DefaultTreeModel) treeComponent.getModel();

        MutableTreeNode targetNode = (MutableTreeNode) target.getLastPathComponent();
        MutableTreeNode parentNode = (MutableTreeNode) targetNode.getParent();
        int index = parentNode.getIndex(targetNode);

        if (0 < index) {
            treeModel.removeNodeFromParent(targetNode);
            treeModel.insertNodeInto(targetNode, parentNode, index -1);

            treeComponent.setSelectionPath(new TreePath(treeModel.getPathToRoot(targetNode)));
            treeComponent.requestFocus();
        }
    }

    public static void moveDownwardSelected(JTree treeComponent) {
        if (treeComponent.getSelectionPath() != null)
           moveDownward(treeComponent, treeComponent.getSelectionPath());
    }

    public static void moveDownward(JTree treeComponent, TreePath target) {
        DefaultTreeModel treeModel = (DefaultTreeModel) treeComponent.getModel();

        MutableTreeNode targetNode = (MutableTreeNode) target.getLastPathComponent();
        MutableTreeNode parent = (MutableTreeNode) targetNode.getParent();
        int index = parent.getIndex(targetNode);

        if (index < parent.getChildCount() -1) {
            treeModel.removeNodeFromParent(targetNode);
            treeModel.insertNodeInto(targetNode, parent, index +1);

            treeComponent.setSelectionPath(new TreePath(treeModel.getPathToRoot(targetNode)));
            treeComponent.requestFocus();
        }
    }


}
