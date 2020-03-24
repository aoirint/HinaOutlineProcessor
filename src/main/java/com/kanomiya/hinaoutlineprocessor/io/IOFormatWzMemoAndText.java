package com.kanomiya.hinaoutlineprocessor.io;

import com.kanomiya.hinaoutlineprocessor.structure.*;
import com.kanomiya.hinaoutlineprocessor.structure.address.HOPAddressComparator;
import com.kanomiya.hinaoutlineprocessor.util.Pair;
import com.kanomiya.hinaoutlineprocessor.util.StringUtils;

import javax.swing.tree.MutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.kanomiya.hinaoutlineprocessor.structure.address.HOPAddressHelper.parseNodeAddress;

/**
 * Created by Kanomiya in 2017/02.
 */
public class IOFormatWzMemoAndText extends HOPDocumentIOFormat {
    /* エスケープは止めにする。レイアウト崩れも性質の一つということで。

    仕様では半角スペースが例示されてる。
    public static final Pattern BOL_ESCAPED_PERIOD = Pattern.compile("^\\\\\\.", Pattern.MULTILINE);  // \. -> .  |  (\\\\)(\\.)
    public static final Pattern BOL_ESCAPED_BACKSLASH = Pattern.compile("^\\\\\\\\", Pattern.MULTILINE); // \\ -> \  |  (\\\\)(\\\\)
    public static final Pattern BOL_UNESCAPED_PERIOD = Pattern.compile("^\\\\.", Pattern.MULTILINE);
    public static final Pattern BOL_UNESCAPED_BACKSLASH = Pattern.compile("^\\\\(?!\\\\)", Pattern.MULTILINE);
    */
    public static final Pattern FIRST_PERIOD = Pattern.compile("[\\r\\n]*\\..*", Pattern.DOTALL);

    @Override
    public String getName() {
        return "wzmemo";
    }

    @Override
    public String getRecommendExtension() {
        return "txt";
    }

    @Override
    public String getDescription()
    {
        return "階層付きテキストドキュメント(." + getRecommendExtension() + ")";
    }

    @Override
    public String format(HOPDocument document) throws FormatException {
        List<Pair<Integer[], HOPNode>> nodeList = new ArrayList<>();

        for (HOPNode nodeObj: Collections.list(document.nodeTree.children())) {
            nodeList.add(Pair.of(parseNodeAddress(nodeObj), nodeObj));

            for (HOPNode childObj: Collections.list(nodeObj.childrenTraversal())) {
                nodeList.add(Pair.of(parseNodeAddress(childObj), childObj));
            }
        }

        HOPAddressComparator comparator = new HOPAddressComparator();
        nodeList.sort((a, b) -> comparator.compare(a.left, b.left));

        StringBuilder builder = new StringBuilder();
        final String lineSeparator = System.lineSeparator();

        for (Pair<Integer[], HOPNode> pair: nodeList) {
            HOPNode nodeObj = pair.right;

            for (int i=0, len=pair.left.length; i<len; i++)
                builder.append('.');

            builder.append(nodeObj.title + lineSeparator);
            if (nodeObj.body.length() > 0)
                builder.append(nodeObj.body + lineSeparator);
        }

        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() -1);

        return builder.toString();
    }

    @Override
    public HOPDocument parse(String text) throws FormatException {
        final String lineSeparator = StringUtils.lineSeparator(text);

        if (! FIRST_PERIOD.matcher(text).matches()) { // WzMemoでもなんでもないやい（最初の通常文字はピリオド）
            HOPNodeTree nodeTree = new HOPNodeTree();
            HOPNode first = HOPNode.createNew(true, "", text);

            nodeTree.insert(first, 0);
            return new HOPDocument(null, null, HOPDocumentOwner.empty(), HOPBounds.DEFAULT, HOPMode.EDIT, DateFormatType.ZONED_DATE_TIME, nodeTree);
        }


        HOPNodeTree nodeTree = new HOPNodeTree();

        List<String> textList = new ArrayList<>();
        { // ノードごとに分割開始（文頭のピリオドはそのまま）
            StringBuilder pool = new StringBuilder();

            String[] lines = text.split(lineSeparator);

            for (String line: lines)
            {
                if (line.startsWith("."))
                {
                    if (pool.length() != 0)
                        textList.add(pool.deleteCharAt(pool.length() -1).toString()); // 文末の余分なEOLを除去

                    pool = new StringBuilder();
                }

                pool.append(line).append(lineSeparator); // 改行をOSデフォルトで復元
            }
            if (pool.length() != 0)
                textList.add(pool.deleteCharAt(pool.length() -1).toString()); // 文末の余分なEOLを除去
        }

        MutableTreeNode prevObj = nodeTree;
        int prevDepth = -1; // ノードがルートと同じ階層になることはない
        for (int i=0, len=textList.size(); i<len; i++) {
            String nodeText = textList.get(i);

            int depth = 0;
            if (nodeText.length() > 0)
                while (nodeText.charAt(depth +1) == '.') // ノードの階層を調べる
                    depth ++;

            nodeText = nodeText.substring(depth +1);

            // エスケープの解除
            // BOL_ESCAPED_PERIOD.matcher(nodeText).replaceAll(".");
            // BOL_ESCAPED_BACKSLASH.matcher(nodeText).replaceAll("\\");

            String title;
            String body;

            if (nodeText.contains(lineSeparator)) { // タイトル行と本文の分割
                String[] array = nodeText.split(lineSeparator, 2);

                title = array[0].trim();
                body = array[1].trim();
            }
            else {
                title = nodeText;
                body = "";

            }

            HOPNodeMarker marker = depth == 0 ?
                    new HOPNodeMarker(HOPNodeMarkerType.SQUARE, HOPNodeMarker.DEFAULT_COLOR_GREEN) :
                    new HOPNodeMarker(HOPNodeMarkerType.DIAMOND, HOPNodeMarker.DEFAULT_COLOR_BLUE);

            HOPNode nodeObj = new HOPNode(title, null, null, marker, body);
            MutableTreeNode parentObj;

            {
                MutableTreeNode tempObj = prevObj;

                for (int j=depth; j<=prevDepth; j++)  // 前回より浅いか同じ階層のとき、遡る
                    tempObj = (MutableTreeNode) tempObj.getParent();

                parentObj = tempObj;
            }

            parentObj.insert(nodeObj, parentObj.getChildCount());

            prevDepth = depth;
            prevObj = nodeObj;
        }

        return new HOPDocument(null, null, HOPDocumentOwner.empty(), HOPBounds.DEFAULT, HOPMode.EDIT, DateFormatType.ZONED_DATE_TIME, nodeTree);
    }
}
