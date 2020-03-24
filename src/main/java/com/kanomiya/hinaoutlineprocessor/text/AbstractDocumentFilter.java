package com.kanomiya.hinaoutlineprocessor.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.util.function.Predicate;

/**
 * Created by Kanomiya in 2017/02.
 */
public abstract class AbstractDocumentFilter extends DocumentFilter
{

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException
    {
        replace(fb, offset, length, "", null);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attrs) throws BadLocationException
    {
        if (string != null)
            replace(fb, offset, 0, string, attrs);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException
    {
        Document document = fb.getDocument();
        int oldLength = document.getLength();
        String oldValue = document.getText(0, oldLength);

        String newValue = oldValue.substring(0, offset) + (string == null ? "" : string) + oldValue.substring(offset +length, oldLength);
        if (! verify(newValue))
            throw new BadLocationException(newValue, offset);

        fb.replace(offset, length, string, attrs);
    }

    public abstract boolean verify(String string);

}
