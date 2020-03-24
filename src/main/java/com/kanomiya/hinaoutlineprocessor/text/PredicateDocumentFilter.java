package com.kanomiya.hinaoutlineprocessor.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.util.function.Predicate;

/**
 * Created by Kanomiya in 2017/02.
 */
public class PredicateDocumentFilter extends AbstractDocumentFilter
{
    private final Predicate<String> predicate;

    public PredicateDocumentFilter(Predicate<String> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean verify(String string)
    {
        return predicate.test(string);
    }

}
