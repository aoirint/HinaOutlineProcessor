package com.kanomiya.hinaoutlineprocessor;

import com.kanomiya.hinaoutlineprocessor.edit.NodeInsertEdit;
import com.kanomiya.hinaoutlineprocessor.edit.NodeRemoveEdit;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.undo.UndoableEditSupport;

/**
 * Created by Kanomiya in 2017/02.
 */
public class UndoableTreeModel extends DefaultTreeModel {
    private final UndoableEditSupport undoSupport = new UndoableEditSupport(this);

    public UndoableTreeModel(TreeNode root) {
        super(root);
    }

    public UndoableTreeModel(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    public void addUndoableEditListener(UndoableEditListener listener) {
        undoSupport.addUndoableEditListener(listener);
    }
    public void removeUndoableEditListener(UndoableEditListener listener) {
        undoSupport.removeUndoableEditListener(listener);
    }

    public void beginUpdate() {
        undoSupport.beginUpdate();
    }
    public void endUpdate() {
        undoSupport.endUpdate();
    }


    public void insertNodeInto(MutableTreeNode newChild,
                               MutableTreeNode parent, int index){
        undoSupport.postEdit(new NodeInsertEdit(this, newChild, null, -1, parent, index));

        super.insertNodeInto(newChild, parent, index);
    }

    public void super_insertNodeInto(MutableTreeNode newChild,
                               MutableTreeNode parent, int index){
        super.insertNodeInto(newChild, parent, index);
    }

    @Override
    public void removeNodeFromParent(MutableTreeNode node) {
        if (node.getParent() != null)
            undoSupport.postEdit(new NodeRemoveEdit(this, node, (MutableTreeNode) node.getParent(), node.getParent().getIndex(node)));

        super.removeNodeFromParent(node);
    }

    public void super_removeNodeFromParent(MutableTreeNode node) {
        super.removeNodeFromParent(node);
    }

}
