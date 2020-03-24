package com.kanomiya.hinaoutlineprocessor;

import com.kanomiya.hinaoutlineprocessor.structure.*;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.Collections;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPDocumentUtils
{

    public static int countAllCharacter(HOPDocument document) {
        int result = 0;

        for (HOPNode node: Collections.list(document.nodeTree.children())) {
            result += countCharacterUnder(node);
        }

        return result;
    }

    public static int countCharacterUnder(HOPNode from) {
        int result = from.body.length();

        for (HOPNode node: Collections.list(from.childrenTraversal()))
            result += node.body.length();

        return result;
    }


}
