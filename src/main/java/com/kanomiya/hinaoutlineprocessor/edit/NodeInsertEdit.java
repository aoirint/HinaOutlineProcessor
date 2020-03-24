package com.kanomiya.hinaoutlineprocessor.edit;

import com.kanomiya.hinaoutlineprocessor.UndoableTreeModel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Created by Kanomiya in 2017/02.
 */
public class NodeInsertEdit extends AbstractUndoableEdit
{
    UndoableTreeModel model;
    MutableTreeNode inserted;
    MutableTreeNode oldParent, newParent;
    int oldIndex, newIndex;

    public NodeInsertEdit(UndoableTreeModel model, MutableTreeNode inserted, MutableTreeNode oldParent, int oldIndex, MutableTreeNode newParent, int newIndex)
    {
        super();
        this.model = model;
        this.inserted = inserted;
        this.oldParent = oldParent;
        this.oldIndex = oldIndex;
        this.newParent = newParent;
        this.newIndex = newIndex;
    }

    @Override
    public void die()
    {
        super.die();
        model = null;
        inserted = null;
        oldParent = newParent = null;
        oldIndex = newIndex = -1;
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        model.super_removeNodeFromParent(inserted);
        if (oldParent != null && oldIndex != -1)
            model.super_insertNodeInto(inserted, oldParent, oldIndex);
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        model.super_removeNodeFromParent(inserted);
        model.super_insertNodeInto(inserted, newParent, newIndex);
    }
}
