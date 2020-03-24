package com.kanomiya.hinaoutlineprocessor.structure;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPNodeTree implements MutableTreeNode
{
    List<HOPNode> nodes;

    public HOPNodeTree()
    {
        nodes = new ArrayList<>();
    }

    @Override
    public void insert(MutableTreeNode child, int index)
    {
        if (child instanceof HOPNode)
            nodes.add(index, (HOPNode) child);

        child.setParent(this);
    }

    @Override
    public void remove(int index)
    {
        TreeNode removed = nodes.remove(index);

        if (removed instanceof MutableTreeNode)
            ((MutableTreeNode) removed).setParent(null);
    }

    @Override
    public void remove(MutableTreeNode node)
    {
        nodes.remove(node);
        node.setParent(null);
    }

    @Override
    public void setUserObject(Object object) {}

    @Override
    public void removeFromParent()
    {

    }

    @Override
    public void setParent(MutableTreeNode newParent)
    {

    }

    @Override
    public TreeNode getChildAt(int childIndex)
    {
        return nodes.get(childIndex);
    }

    @Override
    public int getChildCount()
    {
        return nodes.size();
    }

    @Override
    public TreeNode getParent()
    {
        return null;
    }

    @Override
    public int getIndex(TreeNode node)
    {
        return nodes.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren()
    {
        return true;
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public Enumeration<HOPNode> children()
    {
        return Collections.enumeration(nodes);
    }
}
