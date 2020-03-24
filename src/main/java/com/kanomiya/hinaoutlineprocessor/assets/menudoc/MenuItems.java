package com.kanomiya.hinaoutlineprocessor.assets.menudoc;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Kanomiya in 2017/02.
 */
public enum MenuItems {
    FILE("file"),
    FILE_CREATE_NEW("file.createNew"),
    FILE_OPEN("file.open"),
    FILE_REOPEN("file.reopen", WhenEnabled.WHEN_FILE_LOADED),
    FILE_SAVE("file.save", WhenEnabled.WHEN_EDIT_MODE),
    FILE_SAVEAS("file.saveas", WhenEnabled.WHEN_EDIT_MODE),
    FILE_CLOSE("file.close", WhenEnabled.WHEN_FILE_LOADED),
    FILE_RECENTFILES("file.recentFiles", WhenEnabled.ALWAYS, WhenEnabled.ALWAYS, ChildrenControl.RECENT_FILES),
    FILE_SETTINGS("file.settings"),
    FILE_EXIT("file.exit"),

    DOCUMENT("document", WhenEnabled.WHEN_DOCUMENT_LOADED),
    DOCUMENT_SETTINGS("document.settings", WhenEnabled.WHEN_EDIT_MODE, WhenEnabled.WHEN_EDIT_MODE),
    DOCUMENT_OWNER_SETTINGS("document.owner.settings", WhenEnabled.WHEN_DOCUMENT_LOADED),
    DOCUMENT_MODE_VIEW("document.mode.view", WhenEnabled.UNLESS_VIEW_MODE),
    DOCUMENT_MODE_EDIT("document.mode.edit", WhenEnabled.UNLESS_EDIT_MODE),

    EDIT("edit", WhenEnabled.WHEN_EDIT_MODE, WhenEnabled.WHEN_EDIT_MODE),
    EDIT_UNDO("edit.undo", WhenEnabled.WHEN_UNDOABLE),
    EDIT_REDO("edit.redo", WhenEnabled.WHEN_REDOABLE),
    EDIT_FOCUS_TREE("edit.focus.tree", WhenEnabled.UNLESS_TREE_PANE_FOCUSED),
    EDIT_FOCUS_TITLE("edit.focus.title", WhenEnabled.UNLESS_TITLE_PANE_FOCUSED),
    EDIT_FOCUS_BODY("edit.focus.body", WhenEnabled.UNLESS_BODY_PANE_FOCUSED),

    TREE("tree", array(WhenEnabled.WHEN_EDIT_MODE), array(WhenEnabled.WHEN_EDIT_MODE)),
    TREE_APPEND_CHILD_NODE("tree.appendChildNode", WhenEnabled.WHEN_NODE_SELECTED),
    TREE_APPEND_SIBLING_NODE("tree.appendSiblingNode", WhenEnabled.WHEN_NODE_SELECTED),
    TREE_REMOVE_NODE("tree.removeNode", WhenEnabled.UNLESS_SELECTED_IS_LAST_ONE),
    TREE_CLONE_NODE("tree.cloneNode", WhenEnabled.WHEN_NODE_SELECTED),
    TREE_MOVE_UPWARD("tree.moveUpward", WhenEnabled.WHEN_ABOVE_SELECTED_EXISTS),
    TREE_MOVE_DOWNWARD("tree.moveDownward", WhenEnabled.WHEN_BELOW_SELECTED_EXISTS),
    TREE_NODE_MARKER_CHANGE_SHAPE("tree.node.marker.changeShape", WhenEnabled.WHEN_NODE_SELECTED),
    TREE_NODE_MARKER_CHANGE_COLOR("tree.node.marker.changeColor", WhenEnabled.WHEN_NODE_SELECTED),

    TEXT("text"),
    TEXT_CUT("text.cut", array(WhenEnabled.WHEN_EDIT_MODE), array(WhenEnabled.WHEN_EDIT_MODE)),
    TEXT_COPY("text.copy"),
    TEXT_PASTE("text.paste", array(WhenEnabled.WHEN_EDIT_MODE), array(WhenEnabled.WHEN_EDIT_MODE)),
    TEXT_FIND("text.search.find"),
    TEXT_REPLACE("text.search.replace", array(WhenEnabled.WHEN_EDIT_MODE), array(WhenEnabled.WHEN_EDIT_MODE)),

    ANALYZE("analyze", WhenEnabled.WHEN_DOCUMENT_LOADED),
    ANALYZE_CHARACTER_COUNT("analyze.characterCount", WhenEnabled.WHEN_DOCUMENT_LOADED),
    ANALYZE_CHARACTER_COUNT_ALL("analyze.characterCount.all", WhenEnabled.WHEN_DOCUMENT_LOADED),
    ANALYZE_CHARACTER_COUNT_UNDER_SELECTED("analyze.characterCount.underSelected", WhenEnabled.WHEN_NODE_SELECTED),

    ;

    private final String command;
    public final WhenEnabled[] whenEnabled;
    public final WhenEnabled[] whenVisible;
    public final ChildrenControl childrenControl;

    MenuItems(String command) {
        this(command, WhenEnabled.ALWAYS);
    }

    MenuItems(String command, WhenEnabled whenEnabled) {
        this(command, array(whenEnabled));
    }

    MenuItems(String command, WhenEnabled[] whenEnabled) {
        this(command, whenEnabled, array(WhenEnabled.ALWAYS));
    }

    MenuItems(String command, WhenEnabled whenEnabled, WhenEnabled whenVisible) {
        this(command, array(whenEnabled), array(whenVisible));
    }

    MenuItems(String command, WhenEnabled[] whenEnabled, WhenEnabled[] whenVisible) {
        this(command, whenEnabled, whenVisible, ChildrenControl.NONE);
    }

    MenuItems(String command, WhenEnabled whenEnabled, WhenEnabled whenVisible, ChildrenControl childrenControl) {
        this(command, array(whenEnabled), array(whenVisible), childrenControl);
    }

    MenuItems(String command, WhenEnabled[] whenEnabled, WhenEnabled[] whenVisible, ChildrenControl childrenControl) {
        this.command = command;
        this.whenEnabled = whenEnabled;
        this.whenVisible = whenVisible;
        this.childrenControl = childrenControl;
    }

    public boolean isSame(AbstractButton button) {
        return isSame(button.getActionCommand().isEmpty() ? button.getName() : button.getActionCommand());
    }
    public boolean isSame(ActionEvent e) {
        return isSame(e.getActionCommand());
    }
    public boolean isSame(String command) {
        return command != null && ! command.isEmpty() && ! this.command.isEmpty() && command.equals(this.command);
    }

}
