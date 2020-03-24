package com.kanomiya.hinaoutlineprocessor.edit;

import com.kanomiya.hinaoutlineprocessor.UndoableTreeModel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by Kanomiya in 2017/02.
 */
public class NodeRemoveEdit extends AbstractUndoableEdit
{
    UndoableTreeModel model;
    MutableTreeNode removed;
    MutableTreeNode parent;
    int index;

    public NodeRemoveEdit(UndoableTreeModel model, MutableTreeNode removed, MutableTreeNode parent, int index)
    {
        super();
        this.model = model;
        this.removed = removed;
        this.parent = parent;
        this.index = index;
    }

    @Override
    public void die()
    {
        super.die();
        model = null;
        removed = null;
        parent = null;
        index = -1;
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        model.super_insertNodeInto(removed, parent, index);
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        model.super_removeNodeFromParent(removed);
    }
}
