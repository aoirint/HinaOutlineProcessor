package com.kanomiya.hinaoutlineprocessor.structure;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Created by Kanomiya in 2017/02.
 */
public interface SimpleDocumentListener extends DocumentListener
{
    void documentUpdate(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e)
    {
        documentUpdate(e);
    }

    @Override
    default void removeUpdate(DocumentEvent e)
    {
        documentUpdate(e);
    }

    @Override
    default void changedUpdate(DocumentEvent e)
    {
        documentUpdate(e);
    }
}
