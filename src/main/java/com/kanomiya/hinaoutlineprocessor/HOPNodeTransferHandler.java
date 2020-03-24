package com.kanomiya.hinaoutlineprocessor;

import com.kanomiya.hinaoutlineprocessor.structure.HOPNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPNodeTransferHandler extends TransferHandler
{
    @Override
    public int getSourceActions(JComponent c)
    {
        return COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c)
    {
        JTree tree = (JTree) c;
        TreePath selected = tree.getSelectionPath();

        if (selected != null && selected.getLastPathComponent() instanceof HOPNode)
            return (HOPNode) selected.getLastPathComponent();

        return null;
    }

    @Override
    public boolean canImport(TransferSupport support)
    {
        // JTree.DropLocation loc = (JTree.DropLocation) support.getDropLocation();
        return support.isDataFlavorSupported(HOPNode.DATA_FLAVOR);
    }

    @Override
    public boolean importData(TransferSupport support)
    {
        Transferable transferable = support.getTransferable();

        JTree tree = (JTree) support.getComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        TreePath destPath = null;
        int childIndex = -1;
        boolean isCopy = false;

        if (support.isDrop()) { // D&D

            JTree.DropLocation loc = (JTree.DropLocation) support.getDropLocation();
            destPath = loc.getPath() == null ? new TreePath(model.getRoot()) : loc.getPath();
            childIndex = loc.getChildIndex();
            isCopy = support.getDropAction() == COPY;

        }
        else if (canImport(support)) { // ペースト
            if (tree.getSelectionPath() != null) {
                destPath = tree.getSelectionPath();
                isCopy = true; // 貼り付け処理の場合は強制的にコピー（getDropActionは例外吐く）
            }
        }

        if (destPath != null && destPath.getLastPathComponent() instanceof MutableTreeNode) {
            try {
                HOPNode src  = (HOPNode) transferable.getTransferData(HOPNode.DATA_FLAVOR);
                MutableTreeNode dest = (MutableTreeNode) destPath.getLastPathComponent();

                if (isCopy) {
                    src = src.createClone(); // 親との接続が切れたコピー
                }
                else {
                    if (new TreePath(model.getPathToRoot(src)).isDescendant(destPath))
                        return false; // 自分の親筋または自分自身を自分に移動させることはできない（ループ禁止）

                    model.removeNodeFromParent(src);
                }

                model.insertNodeInto(src, dest, childIndex == -1 ? dest.getChildCount() : Math.min(childIndex, dest.getChildCount()));
                tree.setSelectionPath(new TreePath(model.getPathToRoot(dest)));

            } catch (UnsupportedFlavorException | IOException e)
            {
                e.printStackTrace();
            }

            return true;
        }


        return false;
    }
}
