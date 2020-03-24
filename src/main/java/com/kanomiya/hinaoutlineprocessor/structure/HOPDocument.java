package com.kanomiya.hinaoutlineprocessor.structure;

import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.io.DateFormatType;
import com.kanomiya.hinaoutlineprocessor.io.HOPDocumentIOFormat;

import javax.swing.undo.UndoManager;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPDocument
{
    public ZonedDateTime createdDate;
    public ZonedDateTime lastModifiedDate;
    public HOPDocumentOwner owner;
    public HOPBounds bounds;
    public HOPNodeTree nodeTree;
    public HOPMode mode;
    public DateFormatType dateFormatType;

    public Path documentPath;
    public HOPDocumentIOFormat format;

    public UndoManager undoManager = new UndoManager();

    public HOPDocument(ZonedDateTime createdDate, ZonedDateTime lastModifiedDate, HOPDocumentOwner owner, HOPBounds bounds, HOPMode mode, DateFormatType dateFormatType, HOPNodeTree nodeTree)
    {
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.owner = owner;
        this.bounds = bounds;
        this.mode = mode;
        this.dateFormatType = dateFormatType;
        this.nodeTree = nodeTree;
    }

    public static HOPDocument createNew(HOPAssets assets)
    {
        return createNew(assets, HOPDocumentOwner.empty());
    }

    public static HOPDocument createNew(HOPAssets assets, HOPDocumentOwner owner)
    {
        HOPNodeTree nodeTree = new HOPNodeTree();

        ZonedDateTime now = ZonedDateTime.now();
        nodeTree.insert(new HOPNode(assets.localize("tree.node.noTitle"), now, now, HOPNodeMarker.getDefaultMarker(true), ""), 0);

        HOPDocument document = new HOPDocument(now, now, owner, HOPBounds.DEFAULT, HOPMode.EDIT, DateFormatType.ZONED_DATE_TIME, nodeTree);

        return document;
    }


}
