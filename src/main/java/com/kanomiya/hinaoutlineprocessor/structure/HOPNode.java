package com.kanomiya.hinaoutlineprocessor.structure;

import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;

import javax.swing.text.*;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.undo.UndoManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPNode implements MutableTreeNode, Transferable
{
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(HOPNode.class, "HinaOutlineProcesserNode");

    public String title;
    public ZonedDateTime createdDate;
    public ZonedDateTime lastModifiedDate;
    public HOPNodeMarker marker;
    public String body;

    MutableTreeNode parent;
    List<HOPNode> children = new ArrayList<>();
    public UndoManager undoManager = new UndoManager();

    public HOPNode(String title, ZonedDateTime createdDate, ZonedDateTime lastModifiedDate, HOPNodeMarker marker, String body)
    {
        this.title = title;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.marker = marker;
        this.body = body;
    }

    public static HOPNode createNew(boolean directlyUnderRoot) {
        ZonedDateTime now = ZonedDateTime.now();
        return new HOPNode("", now, now, HOPNodeMarker.getDefaultMarker(directlyUnderRoot), "");
    }
    public static HOPNode createNew(boolean directlyUnderRoot, String title, String text) {
        ZonedDateTime now = ZonedDateTime.now();
        return new HOPNode(title, now, now, HOPNodeMarker.getDefaultMarker(directlyUnderRoot), text);
    }

    public static HOPNode createNoTitle(HOPAssets assets) {
        ZonedDateTime now = ZonedDateTime.now();
        return new HOPNode(assets.localize("tree.node.noTitle"), now, now, new HOPNodeMarker(HOPNodeMarkerType.SQUARE, HOPNodeMarker.DEFAULT_COLOR_GREEN), "");
    }

    /**
     *
     * クローンを作成します。ただし、この「クローン」は元のノードと厳密には等価でないことに注意して下さい。
     *
     * まず、「クローン」は、元のノードのタイムスタンプを無視し、このメソッドが呼ばれた時点の新しいタイムスタンプを持って作成されます。
     * 加えて、「クローン」は、元のノードの親情報を無視し、親のいないノードとして作成されます。
     *
     * このメソッドは、ノードを複製した上で、複製元のノードと並立して扱えるようにする目的で作成されています。
     *
     * @return このノードの「クローン」
     */
    public HOPNode createClone() {
        ZonedDateTime now = ZonedDateTime.now();
        HOPNode clone = new HOPNode(title, now, now, marker.clone(), body);
        clone.children = cloneChildren_Reclusive(this, clone);

        return clone;
    }

    private static List<HOPNode> cloneChildren_Reclusive(HOPNode parent, HOPNode parentClone) {
        List<HOPNode> cloneList = new ArrayList<>();

        for (HOPNode child: Collections.list(parent.children())) {
            HOPNode childClone = child.createClone();
            childClone.setParent(parentClone);

            cloneList.add(childClone);
            cloneList.addAll(cloneChildren_Reclusive(child, childClone));
        }

        return cloneList;
    }

    @Override
    public void insert(MutableTreeNode child, int index)
    {
        if (child instanceof HOPNode)
            children.add(index, (HOPNode) child);

        child.setParent(this);
    }

    @Override
    public void remove(int index)
    {
        TreeNode removed = children.remove(index);

        if (removed instanceof MutableTreeNode)
            ((MutableTreeNode) removed).setParent(null);
    }

    @Override
    public void remove(MutableTreeNode node)
    {
        children.remove(node);
        node.setParent(null);
    }

    @Override
    public void setUserObject(Object object) {}

    @Override
    public void removeFromParent()
    {
        if (parent != null)
        {
            parent.remove(this);
            parent = null;
        }
    }

    @Override
    public void setParent(MutableTreeNode newParent)
    {
        parent = newParent;
    }

    @Override
    public TreeNode getChildAt(int childIndex)
    {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount()
    {
        return children.size();
    }

    @Override
    public TreeNode getParent()
    {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node)
    {
        return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren()
    {
        return true;
    }

    @Override
    public boolean isLeaf()
    {
        return getChildCount() == 0;
    }

    @Override
    public Enumeration<HOPNode> children()
    {
        return Collections.enumeration(children);
    }

    public Enumeration<HOPNode> childrenTraversal()
    {
        Deque<HOPNode> tempList = new ArrayDeque<>();
        tempList.addAll(Collections.list(children()));

        List<HOPNode> result = new ArrayList<>();
        while (!tempList.isEmpty())
        {
            HOPNode next = tempList.poll();
            result.add(next);

            tempList.addAll(Collections.list(next.children()));
        }

        return Collections.enumeration(result);
    }


    @Override
    public String toString()
    {
        return title;
    }



    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return new DataFlavor[] { DATA_FLAVOR };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return flavor.getRepresentationClass() == HOPNode.class;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        return this;
    }
}
