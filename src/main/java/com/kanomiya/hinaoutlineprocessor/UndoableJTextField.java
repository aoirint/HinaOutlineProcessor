package com.kanomiya.hinaoutlineprocessor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kanomiya in 2017/02.
 */
public class UndoableJTextField extends JTextField {
    private static final List<Integer> RECORD_KEYS = Arrays.asList(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
            KeyEvent.VK_KP_UP, KeyEvent.VK_KP_DOWN, KeyEvent.VK_KP_LEFT, KeyEvent.VK_KP_RIGHT);

    private boolean isRecording = false;
    private boolean hasUnpushedChange = false;

    private final UndoableEditSupport undoSupport = new UndoableEditSupport(this);
    private final UndoableEditListener undoableEditListener = e -> {
        if (! isRecording) {
            beginUpdate();
        }

        undoSupport.postEdit(e.getEdit());
        hasUnpushedChange = true;
    };


    public UndoableJTextField() {
        super("");
        init();
    }

    public UndoableJTextField(String text) {
        super(text);
        init();
    }

    public UndoableJTextField(int columns) {
        super(columns);
        init();
    }

    public UndoableJTextField(String text, int columns) {
        super(text, columns);
        init();
    }

    public UndoableJTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        init();
    }

    private void init() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (hasUnpushedChange && RECORD_KEYS.contains(keyCode)) {
                    endUpdate();
                }
            }
        });

        getDocument().addUndoableEditListener(undoableEditListener);
    }

    public void addUndoableEditListener(UndoableEditListener listener) {
        undoSupport.addUndoableEditListener(listener);
    }
    public void removeUndoableEditListener(UndoableEditListener listener) {
        undoSupport.removeUndoableEditListener(listener);
    }

    public void beginUpdate() {
        undoSupport.beginUpdate();
        isRecording = true;
    }
    public void endUpdate() {
        undoSupport.endUpdate();
        isRecording = false;
        hasUnpushedChange = false;
    }

    public void setDocument(Document doc) {
        if (getDocument() != null)
            getDocument().removeUndoableEditListener(undoableEditListener);

        super.setDocument(doc);

        if (doc != null)
            doc.addUndoableEditListener(undoableEditListener);
    }

    public boolean hasUnpushedChange() {
        return hasUnpushedChange;
    }

}
