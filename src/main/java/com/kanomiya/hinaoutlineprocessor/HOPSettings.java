package com.kanomiya.hinaoutlineprocessor;

import com.kanomiya.hinaoutlineprocessor.dialog.SearchDialog;
import com.kanomiya.hinaoutlineprocessor.io.DefaultIOFormats;
import com.kanomiya.hinaoutlineprocessor.structure.HOPDocumentOwner;
import com.kanomiya.hinaoutlineprocessor.structure.HOPNodeMarker;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.kanomiya.hinaoutlineprocessor.HOPUtils.colorCode;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPSettings
{
    public void pushFileHistory(Path newRecord) {
        String absolutePathString = newRecord.toAbsolutePath().toString();

        history.fileHistory.remove(absolutePathString);
        history.fileHistory.add(0, absolutePathString);

        applyFiliHistoryLimit();
    }

    public void applyFiliHistoryLimit() {
        while (history.fileHistorySize < history.fileHistory.size())
            history.fileHistory.remove(history.fileHistory.size() -1);
    }


    public HOPSettings_View view = new HOPSettings_View();
    public HOPSettings_Owner owner = new HOPSettings_Owner();
    public HOPSettings_History history = new HOPSettings_History();
    public HOPSettings_Edit edit = new HOPSettings_Edit();
    public HOPSettings_Search search = new HOPSettings_Search();

    public static class HOPSettings_View {
        public String language = "ja-JP";
        public String theme = "default";

    }

    public static class HOPSettings_Owner {
        public HOPDocumentOwner defaultOwner = HOPDocumentOwner.empty();
        public boolean askAutoInjectOwner = true;

    }

    public static class HOPSettings_History {
        public List<String> fileHistory = new ArrayList<>();
        public int fileHistorySize = 10;
        public boolean restorePreviousOnStartup = false;

        public String lastSelectedMarkerColor = colorCode(HOPNodeMarker.DEFAULT_COLOR_GREEN);

    }

    public static class HOPSettings_Edit {
        public String lastUsedFormatType = DefaultIOFormats.HOPD.getName();
        public boolean emphasizeFocusedPane = true;

    }

    public static class HOPSettings_Search {
        public boolean wrapAround = true;
        public int searchMode = SearchDialog.MODE_NORMAL;
        public boolean directionReverse = false;
        public boolean regexFlagDotAll = false;
        public boolean matchCase = false;
        public String keyword = "";
        public String replacement = "";
        public boolean inSelection = false;
    }

}
