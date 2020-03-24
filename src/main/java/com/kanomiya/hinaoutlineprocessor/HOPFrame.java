package com.kanomiya.hinaoutlineprocessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.dialog.*;
import com.kanomiya.hinaoutlineprocessor.io.DefaultIOFormats;
import com.kanomiya.hinaoutlineprocessor.io.HOPDocumentIOFormat;
import com.kanomiya.hinaoutlineprocessor.io.UnknownFormatException;
import com.kanomiya.hinaoutlineprocessor.assets.menudoc.ChildrenControl;
import com.kanomiya.hinaoutlineprocessor.assets.menudoc.MenuItems;
import com.kanomiya.hinaoutlineprocessor.assets.menudoc.WhenEnabled;
import com.kanomiya.hinaoutlineprocessor.structure.*;
import com.kanomiya.hinaoutlineprocessor.util.FileUtils;
import com.kanomiya.hinaoutlineprocessor.util.HOPTreeUtils;
import com.kanomiya.hinaoutlineprocessor.util.TreeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

import static com.kanomiya.hinaoutlineprocessor.HOPUtils.colorCode;
import static com.kanomiya.hinaoutlineprocessor.HOPUtils.findComponents;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPFrame extends JFrame implements ActionListener
{
    HOPAssets assets;
    HOPSettings settings;

    UndoableTreeModel treeModel;
    JTree treeComponent;

    JPanel treePane;
    JPanel titlePane;
    public JPanel bodyPane;

    public UndoableJTextField titleComponent;
    public UndoableJTextArea bodyComponent;

    JComponent focusedPane;

    JMenuBar menuBar;
    JPopupMenu popupMenu_tree, popupMenu_title, popupMenu_body;

    JLabel characterCountLabel, lineCountLabel, caretRowLabel, caretColumnLabel;

    public HOPDialogSettings settingsDialog;
    public HOPDialogThemeSettings themeSettingsDialog;
    public HOPDialogDocumentSettings documentSettingsDialog;
    public HOPDialogDocumentOwner ownerSettingsDialog;
    public SearchDialog searchDialog;

    HOPDocument document;

    boolean unsaved;

    public HOPFrame(HOPAssets assets, HOPSettings settings) {
        super();

        this.assets = assets;
        this.settings = settings;
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(640, 480);
        setIconImage(assets.icons.appIcon);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                actionPerformed(new ActionEvent(HOPFrame.this, ActionEvent.ACTION_PERFORMED, "file.exit"));
            }
        });

        init();
    }

    public void init() {
        treePane = new JPanel();
        treePane.setLayout(new BorderLayout());
        titlePane = new JPanel();
        titlePane.setLayout(new BorderLayout());
        bodyPane = new JPanel();
        bodyPane.setLayout(new BorderLayout());

        treeComponent = new JTree();
        treeComponent.setRootVisible(false);
        treeComponent.setBorder(new EmptyBorder(2,2,2,2));
        treeComponent.setShowsRootHandles(true);
        treeComponent.setExpandsSelectedPaths(true);
        // TODO: treeComponent.setCellEditor(new DefaultTreeCellEditor()); セルえでっと
        treeComponent.setDropMode(DropMode.ON_OR_INSERT);
        treePane.add(treeComponent);

        titleComponent = new UndoableJTextField();
        titleComponent.setBorder(new EmptyBorder(8,8,8,8));
        titleComponent.setOpaque(true);
        titlePane.add(titleComponent);

        bodyComponent = new UndoableJTextArea();
        bodyComponent.setBorder(new EmptyBorder(8,8,8,8));
        bodyComponent.setOpaque(true);
        bodyPane.add(bodyComponent);


        JPanel statusBar = new JPanel();
        statusBar.setLayout(new GridLayout(1, 2));
        {
            characterCountLabel = new JLabel();
            JLabel separator_1 = new JLabel("|");
            lineCountLabel = new JLabel();
            caretRowLabel = new JLabel();
            caretColumnLabel = new JLabel();

            characterCountLabel.putClientProperty(HOPAssets.HOP_I18N_ID, "analyze.short.characterCount");
            characterCountLabel.putClientProperty(HOPAssets.HOP_I18N_ARGUMENTS_ID, new Object[] {0});
            lineCountLabel.putClientProperty(HOPAssets.HOP_I18N_ID, "analyze.short.lineCount");
            lineCountLabel.putClientProperty(HOPAssets.HOP_I18N_ARGUMENTS_ID, new Object[] {1});
            caretRowLabel.putClientProperty(HOPAssets.HOP_I18N_ID, "analyze.short.caretRow");
            caretRowLabel.putClientProperty(HOPAssets.HOP_I18N_ARGUMENTS_ID, new Object[] {1});
            caretColumnLabel.putClientProperty(HOPAssets.HOP_I18N_ID, "analyze.short.caretColumn");
            caretColumnLabel.putClientProperty(HOPAssets.HOP_I18N_ARGUMENTS_ID, new Object[] {1});

            JPanel statusBar_West = new JPanel();
            JPanel statusBar_East = new JPanel();

            statusBar_West.add(characterCountLabel);
            statusBar_West.add(separator_1);
            statusBar_West.add(lineCountLabel);

            statusBar_East.add(caretRowLabel);
            statusBar_East.add(caretColumnLabel);

            statusBar.add(statusBar_West);
            statusBar.add(statusBar_East);
        }


        JSplitPane splitPane = new JSplitPane();

        JScrollPane panel_tree_scroll = new JScrollPane(treePane);

        JPanel panel_edit = new JPanel();
        JPanel panel_edit_title = new JPanel();
        panel_edit_title.setBorder(new LineBorder(Color.DARK_GRAY, 1, true));
        panel_edit_title.setLayout(new BorderLayout());
        panel_edit_title.add(titlePane);

        JScrollPane panel_edit_body_scroll = new JScrollPane(bodyPane);

        panel_edit.setLayout(new BorderLayout());
        panel_edit.add(panel_edit_title, BorderLayout.NORTH);
        panel_edit.add(panel_edit_body_scroll);
        panel_edit.add(statusBar, BorderLayout.SOUTH);


        splitPane.setDividerLocation(180);
        splitPane.setLeftComponent(panel_tree_scroll);
        splitPane.setRightComponent(panel_edit);

        add(splitPane);


        menuBar = new JMenuBar();
        assets.menus.menuBar.injectTo(menuBar::add, () -> {})
                .forEach(item -> item.addActionListener(HOPFrame.this));
        setJMenuBar(menuBar);

        popupMenu_tree = new JPopupMenu();
        popupMenu_tree.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (document == null || document.mode != HOPMode.EDIT) {
                    SwingUtilities.invokeLater(() -> {
                        popupMenu_tree.setVisible(false);
                    });
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        assets.menus.popupMenuTree.injectTo(popupMenu_tree::add, popupMenu_tree::addSeparator)
                .forEach(item -> item.addActionListener(HOPFrame.this));
        treeComponent.setComponentPopupMenu(popupMenu_tree);


        popupMenu_title = new JPopupMenu();
        popupMenu_title.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (! isTitlePaneSelected() || document == null) {
                    SwingUtilities.invokeLater(() -> {
                        popupMenu_title.setVisible(false);
                    });
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        assets.menus.popupMenuTitle.injectTo(popupMenu_title::add, popupMenu_title::addSeparator)
                .forEach(item -> item.addActionListener(HOPFrame.this));
        titleComponent.setComponentPopupMenu(popupMenu_title);

        popupMenu_body = new JPopupMenu();
        popupMenu_body.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (! isBodyPaneSelected() || document == null) {
                    SwingUtilities.invokeLater(() -> {
                        popupMenu_body.setVisible(false);
                    });
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        assets.menus.popupMenuBody.injectTo(popupMenu_body::add, popupMenu_body::addSeparator)
                .forEach(item -> item.addActionListener(HOPFrame.this));
        bodyComponent.setComponentPopupMenu(popupMenu_body);



        treeComponent.addTreeSelectionListener(e -> {
            if (e.getOldLeadSelectionPath() != null && e.getOldLeadSelectionPath().getLastPathComponent() instanceof HOPNode)
            {
                HOPNode oldNode = (HOPNode) e.getOldLeadSelectionPath().getLastPathComponent();

                titleComponent.removeUndoableEditListener(oldNode.undoManager);
                bodyComponent.removeUndoableEditListener(oldNode.undoManager);
            }

            if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getLastPathComponent() instanceof HOPNode)
            {
                HOPNode newNode = (HOPNode) e.getNewLeadSelectionPath().getLastPathComponent();

                boolean unsavedCache = unsaved;

                titleComponent.setText(newNode.title);
                bodyComponent.setText(newNode.body);

                titleComponent.addUndoableEditListener(newNode.undoManager);
                bodyComponent.addUndoableEditListener(newNode.undoManager);

                unsaved = unsavedCache;
                adjust_MenuContents();
                revalidateTitle();
            }

            boolean flag = isNodeSelected();
            titleComponent.setEnabled(flag);
            bodyComponent.setEnabled(flag);
        });

        titleComponent.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                updateFocusedPane(titlePane);
            }
        });

        bodyComponent.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                updateFocusedPane(bodyPane);
            }
        });

        treeComponent.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                updateFocusedPane(treePane);
            }
        });


        bodyComponent.addCaretListener(e ->
        {
            int pos = e.getDot();
            int len = bodyComponent.getText().length();
            String before = bodyComponent.getText().substring(0, pos);
            int row = (int) before.chars().filter(ch -> ch == '\n').count() +1;
            int col = before.substring(Math.max(0, before.lastIndexOf('\n') +1)).length() +1;

            characterCountLabel.putClientProperty(HOPAssets.HOP_I18N_ARGUMENTS_ID, new Object[] {len});
            lineCountLabel.putClientProperty(HOPAssets.HOP_I18N_ARGUMENTS_ID, new Object[] {bodyComponent.getLineCount()});
            caretRowLabel.putClientProperty(HOPAssets.HOP_I18N_ARGUMENTS_ID, new Object[] {row});
            caretColumnLabel.putClientProperty(HOPAssets.HOP_I18N_ARGUMENTS_ID, new Object[] {col});

            assets.localizeComponent(characterCountLabel);
            assets.localizeComponent(lineCountLabel);
            assets.localizeComponent(caretRowLabel);
            assets.localizeComponent(caretColumnLabel);
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event ->
        {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                if (event.getKeyCode() == KeyEvent.VK_Z && event.isControlDown()) {
                    actionPerformed(new ActionEvent(HOPFrame.this, ActionEvent.ACTION_PERFORMED, "edit.undo"));
                    return true;
                }
                else if (event.getKeyCode() == KeyEvent.VK_Y && event.isControlDown()) {
                    actionPerformed(new ActionEvent(HOPFrame.this, ActionEvent.ACTION_PERFORMED, "edit.redo"));
                    return true;
                }
            }

            return false;
        });

        setFocusable(true);
        requestFocusInWindow();

        bodyComponent.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_TAB && e.isShiftDown())
                    actionPerformed(new ActionEvent(HOPFrame.this, ActionEvent.ACTION_PERFORMED, "edit.focus.title"));
            }
        });

        HOPNodeTreeCellRenderer cellRenderer = new HOPNodeTreeCellRenderer(assets, settings);
        treeComponent.setCellRenderer(cellRenderer);

        treeComponent.setTransferHandler(new HOPNodeTransferHandler());


        adjustAllContents();
    }

    public void setDocument(HOPDocument newDocument) {
        if (newDocument == null)
            newDocument = HOPDocument.createNew(assets);

        if (document != null && isNodeSelected()) {
            titleComponent.removeUndoableEditListener(getSelectedNode().undoManager);
            bodyComponent.removeUndoableEditListener(getSelectedNode().undoManager);
        }

        this.document = newDocument;

        treeModel = new UndoableTreeModel(newDocument.nodeTree);
        if (newDocument.nodeTree.getChildCount() == 0)
            treeModel.insertNodeInto(HOPNode.createNoTitle(assets), newDocument.nodeTree, 0);

        treeComponent.setModel(treeModel);

        treeComponent.setSelectionPath(new TreePath(treeModel.getPathToRoot(newDocument.nodeTree.getChildAt(0))));

        if (newDocument.bounds == null)
            newDocument.bounds = HOPBounds.DEFAULT;

        setBounds(newDocument.bounds.getX(), newDocument.bounds.getY(), newDocument.bounds.getWidth(), newDocument.bounds.getHeight());

        treeModel.addUndoableEditListener(newDocument.undoManager);

        updateFocusedPane(treePane);

        unsaved = false;
        adjustAllContents();
        repaint();

        if (titleComponent.hasUnpushedChange())
            titleComponent.endUpdate();

        if (bodyComponent.hasUnpushedChange())
            bodyComponent.endUpdate();

        newDocument.undoManager.discardAllEdits();
        for (HOPNode child: Collections.list(newDocument.nodeTree.children())) {
            child.undoManager.discardAllEdits(); // TODO:
        }
    }

    /**
     *
     * @return if true, no change is unsaved.
     */
    public boolean checkUnsaved() {
        if (unsaved)
        {
            int option = JOptionPane.showConfirmDialog(HOPFrame.this, assets.localize("message.unsaved"), assets.localize("message.unsaved.title"), JOptionPane.YES_NO_CANCEL_OPTION);

            if (option != JOptionPane.NO_OPTION) {
                if (option == JOptionPane.YES_OPTION)
                    actionPerformed(new ActionEvent(HOPFrame.this, ActionEvent.ACTION_PERFORMED, "file.save"));

                if (unsaved || option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION)
                    return false;
            }
        }

        return true;
    }

    public boolean checkOverwrite(Path path) {
        if (Files.exists(path)) {
            int option = JOptionPane.showConfirmDialog(this, assets.localize("message.overwrite", path.getFileName().toString()), assets.localize("message.overwrite.title"), JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION)
                return true;
            else
                return false;
        }

        return true;
    }

    public void revalidateTitle() {
        StringBuilder builder = new StringBuilder();

        if (document != null) {
            builder.append(document.documentPath != null ? document.documentPath.getFileName().toString() : assets.localize("document.unsaved"));
            builder.append(' ').append('@').append(assets.localize(document.mode == HOPMode.EDIT ? "document.mode.edit.short" : "document.mode.view.short"));
            builder.append(' ').append('<').append(document.format != null ? document.format.getName() :  assets.localize("document.owner.unset")).append('>');
            builder.append(' ').append('[').append(document.owner.name.isEmpty() ? assets.localize("document.owner.unset") : document.owner.name);
            if (! document.owner.mail.isEmpty()) builder.append(' ').append('|').append(' ').append(document.owner.mail);
            if (! document.owner.phone.isEmpty()) builder.append(' ').append('|').append(' ').append(document.owner.phone);
            if (! document.owner.website.isEmpty()) builder.append(' ').append('|').append(' ').append(document.owner.website);
            builder.append(']');
        }

        if (unsaved) builder.append(' ').append('*');

        if (builder.length() > 0)
            builder.append(' ').append('-').append(' ');
        builder.append(assets.localize("application.title"));

        setTitle(builder.toString());
    }


    public void adjustAllContents() {
        adjust_ComponentSettings();

        // Dynamic Translation, etc.
        adjust_Contents(themeSettingsDialog);
        adjust_Contents(documentSettingsDialog);
        adjust_Contents(ownerSettingsDialog);
        adjust_Contents(settingsDialog);
        adjust_MainFrameContents();
    }

    public void adjust_ComponentSettings() {
        titleComponent.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        titleComponent.setForeground(assets.getThemeColor("titleComponent.foreground"));
        titleComponent.setBackground(assets.getThemeColor("titleComponent.background"));
        titleComponent.setSelectedTextColor(assets.getThemeColor("titleComponent.selectionForeground"));
        titleComponent.setSelectionColor(assets.getThemeColor("titleComponent.selectionBackground"));
        titleComponent.setCaretColor(assets.getThemeColor("titleComponent.caret"));

        bodyComponent.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        bodyComponent.setForeground(assets.getThemeColor("bodyComponent.foreground"));
        bodyComponent.setBackground(assets.getThemeColor("bodyComponent.background"));
        bodyComponent.setSelectedTextColor(assets.getThemeColor("bodyComponent.selectionForeground"));
        bodyComponent.setSelectionColor(assets.getThemeColor("bodyComponent.selectionBackground"));
        bodyComponent.setCaretColor(assets.getThemeColor("bodyComponent.caret"));
        bodyComponent.setTabSize(4);

        treeComponent.setForeground(assets.getThemeColor("treeComponent.foreground"));
        treeComponent.setBackground(assets.getThemeColor("treeComponent.background"));
    }

    public void adjust_MainFrameContents() {
        boolean loaded = verifyFlag(WhenEnabled.WHEN_DOCUMENT_LOADED);
        boolean selected = verifyFlag(WhenEnabled.WHEN_NODE_SELECTED);
        boolean isEditable = verifyFlag(WhenEnabled.WHEN_EDIT_MODE);

        treeComponent.setEnabled(loaded);
        titleComponent.setEnabled(selected);
        bodyComponent.setEnabled(selected);

        treeComponent.setDragEnabled(isEditable);
        // treeComponent.setEditable(isEditable);
        titleComponent.setEditable(isEditable);
        bodyComponent.setEditable(isEditable);

        for (Component awt: findComponents(this)) {

            if (awt instanceof JComponent) {
                JComponent component = (JComponent) awt;

                assets.localizeComponent(component);
            }
        }

        adjust_MenuContents();
        revalidateTitle();
    }

    public void adjust_MenuContents() {
        List<Component> components = new ArrayList<>();

        components.addAll(findComponents(menuBar));
        components.addAll(findComponents(popupMenu_body));
        components.addAll(findComponents(popupMenu_title));
        components.addAll(findComponents(popupMenu_tree));

        for (Component awt: components) {
            if (awt instanceof JComponent) {
                JComponent component = (JComponent) awt;

                assets.localizeComponent(component);

                if (component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;

                    for (MenuItems menuItem: MenuItems.values()) {
                        if (menuItem.isSame(button)) {
                            button.setEnabled(verifyFlag(menuItem.whenEnabled));
                            button.setVisible(verifyFlag(menuItem.whenVisible));

                            if (component instanceof JMenu && menuItem.childrenControl != ChildrenControl.NONE) {
                                JMenu menu = (JMenu) button;

                                if (menuItem.childrenControl == ChildrenControl.RECENT_FILES) {
                                    menu.removeAll();

                                    settings.history.fileHistory.stream().forEach(fileName ->
                                    {
                                        JMenuItem historyItem = new JMenuItem(fileName);
                                        historyItem.setActionCommand("file.open " + fileName);
                                        historyItem.addActionListener(HOPFrame.this);
                                        menu.add(historyItem);
                                    });
                                }
                            }

                            break;
                        }
                    }
                }
            }
        }


    }

    public void adjust_Contents(Container container) {
        if (container != null)
            for (Component awt: findComponents(container))
                if (awt instanceof JComponent)
                    assets.localizeComponent((JComponent) awt);
    }


    public void updateFocusedPane(JComponent newPane) {
        if (focusedPane != null)
            focusedPane.setBorder(null);

        if (settings.edit.emphasizeFocusedPane)
            newPane.setBorder(new LineBorder(assets.getThemeColor("emphasisBorder"), 2, true));

        focusedPane = newPane;
        adjust_MenuContents();
    }


    public void openFileDialog() {
        JFileChooser jfc = new JFileChooser();
        jfc.setAcceptAllFileFilterUsed(false);

        if (!settings.history.fileHistory.isEmpty())
            jfc.setCurrentDirectory(new File(settings.history.fileHistory.get(0)));

        DefaultIOFormats.list().forEach(jfc::addChoosableFileFilter);

        try {
            HOPDocumentIOFormat defaultFileFilter = DefaultIOFormats.find(settings.edit.lastUsedFormatType);
            jfc.setFileFilter(defaultFileFilter);

            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                Path path = jfc.getSelectedFile().toPath();
                HOPDocumentIOFormat format = (HOPDocumentIOFormat) jfc.getFileFilter();

                loadFromFile(path, format, false, false);
            }
        } catch (UnknownFormatException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(Path path) {
        try {
            loadFromFile(path, DefaultIOFormats.find(path), false, false);
        } catch (UnknownFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e, "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadFromFile(Path path, HOPDocumentIOFormat format, boolean force, boolean silently) {
        if (force || checkUnsaved())
        {
            if (! silently)
                settings.pushFileHistory(path);

            try {
                HOPDocument document = format.parse(FileUtils.readAll(path));
                document.documentPath = path;
                document.format = format;
                settings.edit.lastUsedFormatType = format.getName();

                setDocument(document);

            } catch (IOException | HOPDocumentIOFormat.FormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e, "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public void saveFileDialog() {

        JFileChooser jfc = new JFileChooser();
        jfc.setAcceptAllFileFilterUsed(false);
        if (!settings.history.fileHistory.isEmpty())
            jfc.setCurrentDirectory(new File(settings.history.fileHistory.get(settings.history.fileHistory.size() -1)));

        DefaultIOFormats.list().forEach(jfc::addChoosableFileFilter);

        try {
            HOPDocumentIOFormat defaultFileFilter = document.format != null ? document.format :
                    DefaultIOFormats.find(settings.edit.lastUsedFormatType);

            jfc.setFileFilter(defaultFileFilter);

            if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                Path path = jfc.getSelectedFile().toPath();
                HOPDocumentIOFormat format = (HOPDocumentIOFormat) jfc.getFileFilter();

                if (! path.getFileName().toString().contains("."))
                    path = Paths.get(path.toAbsolutePath().toString() + "." + format.getRecommendExtension());

                if (checkOverwrite(path)) {
                    saveToFile(path, format, false);
                }
            }
        } catch (UnknownFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e, "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveToFile(Path path, HOPDocumentIOFormat format, boolean silently) {
        document.documentPath = path;
        document.format = format;
        document.bounds = new HOPBounds(getY(), getY() +getHeight() -1, getX(), getX() +getWidth() -1);
        document.lastModifiedDate = ZonedDateTime.now();

        if (silently)
            settings.pushFileHistory(path);
        settings.edit.lastUsedFormatType = format.getName();

        try {
            Files.write(path, Arrays.asList(format.format(document)));
        } catch (HOPDocumentIOFormat.FormatException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e, "Exception", JOptionPane.ERROR_MESSAGE);
        }

        unsaved = false;
        revalidateTitle();
    }


    public UndoManager getFocusedPaneUndoManager() {
        if (focusedPane == titlePane || focusedPane == bodyPane) {
            HOPNode selected = getSelectedNode();

            return selected.undoManager;
        }
        else if (focusedPane == treePane) {
            return document.undoManager;
        }

        return null;
    }


    public boolean verifyFlag(WhenEnabled[] array) {
        for (WhenEnabled whenEnabled: array)
            if (! verifyFlag(whenEnabled))
                return false;

        return true;
    }

    public boolean verifyFlag(WhenEnabled whenEnabled) {
        final boolean flag;

        switch (whenEnabled) {
            case ALWAYS:
                flag = true;
                break;
            default:
            case ALWAYS_DISABLED:
                flag = false;
                break;
            case WHEN_DOCUMENT_LOADED:
                flag = isDocumentLoaded();
                break;
            case WHEN_FILE_LOADED:
                flag = isFileLoaded();
                break;
            case WHEN_NODE_SELECTED:
                flag = isNodeSelected();
                break;
            case UNLESS_SELECTED_IS_LAST_ONE:
                flag = ! isSelectedNodeLastOne();
                break;
            case WHEN_ABOVE_SELECTED_EXISTS:
                flag = isAboveSelectedExist();
                break;
            case WHEN_BELOW_SELECTED_EXISTS:
                flag = isBelowSelectedExist();
                break;
            case WHEN_TREE_PANE_FOCUSED:
                flag = isTreePaneSelected();
                break;
            case UNLESS_TREE_PANE_FOCUSED:
                flag = ! isTreePaneSelected();
                break;
            case WHEN_TEXT_PANE_FOCUSED:
                flag = isTextPaneSelected();
                break;
            case WHEN_TITLE_PANE_FOCUSED:
                flag = isTitlePaneSelected();
                break;
            case UNLESS_TITLE_PANE_FOCUSED:
                flag = ! isTitlePaneSelected();
                break;
            case WHEN_BODY_PANE_FOCUSED:
                flag = isBodyPaneSelected();
                break;
            case UNLESS_BODY_PANE_FOCUSED:
                flag = ! isBodyPaneSelected();
                break;
            case WHEN_UNDOABLE:
                flag = isUndoable();
                break;
            case WHEN_REDOABLE:
                flag = isRedoable();
                break;
            case WHEN_EDIT_MODE:
                flag = isDocumentLoaded() && document.mode == HOPMode.EDIT;
                break;
            case WHEN_VIEW_MODE:
                flag = isDocumentLoaded() && document.mode == HOPMode.VIEW;
                break;
            case UNLESS_EDIT_MODE:
                flag = isDocumentLoaded() && document.mode != HOPMode.EDIT;
                break;
            case UNLESS_VIEW_MODE:
                flag = isDocumentLoaded() && document.mode != HOPMode.VIEW;
                break;
        }

        return flag;
    }

    public boolean isDocumentLoaded() {
        return document != null;
    }

    public boolean isFileLoaded() {
        return document != null && document.documentPath != null;
    }

    public boolean isNodeSelected() {
        return treeComponent.getSelectionPath() != null && treeComponent.getSelectionPath().getLastPathComponent() instanceof HOPNode;
    }

    public HOPNode getSelectedNode() {
        if (isNodeSelected()) {
            return (HOPNode) treeComponent.getSelectionPath().getLastPathComponent();
        }

        return null;
    }

    public boolean isSelectedNodeLastOne() {
        HOPNode selected = getSelectedNode();
        return selected != null && selected.getParent() == document.nodeTree && document.nodeTree.getChildCount() == 1;
    }

    public boolean isAboveSelectedExist() {
        HOPNode selected = getSelectedNode();
        return selected != null && selected.getParent().getIndex(selected) > 0;
    }

    public boolean isBelowSelectedExist() {
        HOPNode selected = getSelectedNode();
        return selected != null && selected.getParent().getIndex(selected) < selected.getParent().getChildCount() -1;
    }

    public boolean isTextPaneSelected() {
        return isTitlePaneSelected() || isBodyPaneSelected();
    }

    public boolean isTitlePaneSelected() {
        return focusedPane == titlePane;
    }

    public boolean isBodyPaneSelected() {
        return focusedPane == bodyPane;
    }

    public boolean isTreePaneSelected() {
        return focusedPane == treePane;
    }

    public boolean isUndoable() {
        UndoManager undoManager = getFocusedPaneUndoManager();
        return undoManager != null && undoManager.canUndo() ||
                (isTitlePaneSelected() && titleComponent.hasUnpushedChange()) ||
                (isBodyPaneSelected() && bodyComponent.hasUnpushedChange());
    }

    public boolean isRedoable() {
        UndoManager undoManager = getFocusedPaneUndoManager();
        return undoManager != null && undoManager.canRedo() &&
                ! ((isTitlePaneSelected() && titleComponent.hasUnpushedChange()) ||
                (isBodyPaneSelected() && bodyComponent.hasUnpushedChange()));
    }


    @Override
    public void actionPerformed(ActionEvent event)
    {
        String command = event.getActionCommand();

        try {
            execCommand(command);
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    public void execCommand(String command) throws CommandException {
        // ------------------------------------------------------------------------------------------------------------
        // # File
        // ------------------------------------------------------------------------------------------------------------
        if ("file.createNew".equals(command)) {
            if (checkUnsaved())
            {
                HOPDocumentOwner owner;

                if (! settings.owner.askAutoInjectOwner || JOptionPane.showConfirmDialog(HOPFrame.this, assets.localize("message.owner.confirmAutoInject"), assets.localize("message.owner.confirmAutoInject.title"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    owner = settings.owner.defaultOwner.clone();
                else
                    owner = HOPDocumentOwner.empty();

                setDocument(HOPDocument.createNew(assets, owner));
            }
        }

        else if (command.startsWith("file.open")) {
            if (command.matches("file\\.open .+"))
                loadFromFile(Paths.get(command.split(" ", 2)[1]));
            else
                openFileDialog();
        }

        else if ("file.reopen".equals(command)) {
            if (document.documentPath != null)
                loadFromFile(document.documentPath);
        }

        else if ("file.save".equals(command)) {
            if (document.documentPath == null || document.format == null)
                saveFileDialog();
            else
                saveToFile(document.documentPath, document.format, false);
        }

        else if ("file.saveAs".equals(command)) {
            saveFileDialog();
        }

        else if ("file.close".equals(command)) {
            if (checkUnsaved()) {
                setDocument(null);
            }
        }

        else if ("document.settings".equals(command)) {
            if (document != null) {
                documentSettingsDialog = new HOPDialogDocumentSettings(this, assets, settings, document);
                documentSettingsDialog.addWindowListener(new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        documentSettingsDialog = null;
                    }
                });

                adjust_Contents(documentSettingsDialog);
                documentSettingsDialog.setVisible(true);
                adjust_MainFrameContents();
            }
        }

        else if ("document.owner.settings".equals(command)) {
            ownerSettingsDialog = new HOPDialogDocumentOwner(this, assets, settings, document.mode, document.owner);
            ownerSettingsDialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    ownerSettingsDialog = null;
                }
            });

            adjust_Contents(ownerSettingsDialog);
            ownerSettingsDialog.setVisible(true);
            adjust_MainFrameContents();
        }
        else if (command.startsWith("document.mode.")) {
            String[] args = command.split("\\.", 3);

            if ("view".equals(args[2]))
                document.mode = HOPMode.VIEW;
            else if ("edit".equals(args[2]))
                document.mode = HOPMode.EDIT;

            adjust_MainFrameContents();
        }
/* TODO: Import/Export HTML
        else if (command.startsWith("file.import.")) {
            String type = command.split("\\.", 3)[2];

        }

        else if (command.startsWith("file.export.")) {
            String type = command.split("\\.", 3)[2];

        }
*/
        else if ("file.settings".equals(command)) {
            settingsDialog = new HOPDialogSettings(this, assets, settings);
            settingsDialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    settingsDialog = null;
                }
            });

            adjust_Contents(settingsDialog);
            settingsDialog.setVisible(true);
            adjust_MainFrameContents();
        }

        else if ("file.theme.settings".equals(command)) {
            themeSettingsDialog = new HOPDialogThemeSettings(this, assets, settings);
            themeSettingsDialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    themeSettingsDialog = null;
                }
            });

            adjust_Contents(themeSettingsDialog);
            themeSettingsDialog.setVisible(true);
            adjust_MainFrameContents();
        }

        else if ("file.exit".equals(command)) {
            if (checkUnsaved()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try
                {
                    String str = gson.toJson(settings);
                    Files.write(HOPAssets.CONFIG_FILE, Arrays.asList(str));
                } catch (IOException e)
                {
                    JOptionPane.showMessageDialog(HOPFrame.this, e.getLocalizedMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                }

                System.exit(0);
            }
        }


        // ------------------------------------------------------------------------------------------------------------
        // # Edit
        // ------------------------------------------------------------------------------------------------------------

        else if ("edit.undo".equals(command)) {
            UndoManager undoManager = getFocusedPaneUndoManager();
            if (undoManager != null) {
                if (isTitlePaneSelected() && titleComponent.hasUnpushedChange()) {
                    titleComponent.endUpdate();
                }
                else if (isBodyPaneSelected() && bodyComponent.hasUnpushedChange()) {
                    bodyComponent.endUpdate();
                }

                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        }
        else if ("edit.redo".equals(command)) {
            UndoManager undoManager = getFocusedPaneUndoManager();
            if (undoManager != null) {
                if (isTitlePaneSelected() && titleComponent.hasUnpushedChange()) {
                    titleComponent.endUpdate();
                }
                else if (isBodyPaneSelected() && bodyComponent.hasUnpushedChange()) {
                    bodyComponent.endUpdate();
                }

                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        }
        else if ("edit.focus.title".equals(command))
            titleComponent.requestFocus();
        else if ("edit.focus.body".equals(command))
            bodyComponent.requestFocus();
        else if ("edit.focus.tree".equals(command))
            treeComponent.requestFocus();

        else if ("text.cut".equals(command)) {
            if (focusedPane == titlePane)
                titleComponent.cut();
            else if (focusedPane == bodyPane)
                bodyComponent.cut();
        }
        else if ("text.copy".equals(command)) {
            if (focusedPane == titlePane)
                titleComponent.copy();
            else if (focusedPane == bodyPane)
                bodyComponent.copy();
        }
        else if ("text.paste".equals(command))
        {
            if (focusedPane == titlePane)
                titleComponent.paste();
            else if (focusedPane == bodyPane)
                bodyComponent.paste();
        }
        else if ("text.search.find".equals(command) || "text.search.replace".equals(command)) {
            if (isNodeSelected()) {
                searchDialog = new SearchDialog(this, assets, settings, document, "text.search.replace".equals(command));
                searchDialog.addWindowListener(new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        searchDialog = null;
                    }
                });

                adjust_Contents(searchDialog);
                searchDialog.setVisible(true);
            }
        }

        // ------------------------------------------------------------------------------------------------------------
        // ## Edit: Tree
        // ------------------------------------------------------------------------------------------------------------

        else if ("tree.appendChildNode".equals(command)) {
            if (treeComponent.getSelectionPath() != null)
            {
                TreePath path = treeComponent.getSelectionPath();
                HOPNode selected = (HOPNode) path.getLastPathComponent();

                ZonedDateTime now = ZonedDateTime.now();
                HOPNode newChild = new HOPNode("", now, now, HOPNodeMarker.getDefaultMarker(false), "");

                int index = selected.getChildCount();
                treeModel.insertNodeInto(newChild, selected, index);
                if (treeComponent.isCollapsed(path))
                    treeComponent.expandPath(path);

                treeComponent.setSelectionPath(new TreePath(treeModel.getPathToRoot(newChild)));
                titleComponent.requestFocus();

                unsaved = true;

                repaint();
                revalidate();
                revalidateTitle();
            }
        }

        else if ("tree.appendSiblingNode".equals(command)) {
            if (treeComponent.getSelectionPath() != null)
            {
                TreePath path = treeComponent.getSelectionPath();
                HOPNode selected = (HOPNode) path.getLastPathComponent();
                MutableTreeNode parent = (MutableTreeNode) selected.getParent();

                HOPNode newChild = HOPNode.createNew(parent instanceof HOPNodeTree);

                int index = parent.getChildCount();
                treeModel.insertNodeInto(newChild, parent, index);

                treeComponent.setSelectionPath(new TreePath(treeModel.getPathToRoot(newChild)));
                titleComponent.requestFocus();

                unsaved = true;

                repaint();
                revalidate();
                revalidateTitle();
            }
        }

        else if ("tree.removeNode".equals(command)) {
            if (treeComponent.getSelectionPath() != null)
            {
                MutableTreeNode selected = (MutableTreeNode) treeComponent.getSelectionPath().getLastPathComponent();
                int parentChildCount = selected.getParent().getChildCount();

                if(! (selected.getParent() instanceof HOPNodeTree && parentChildCount == 1)) {
                    TreeNode next;
                    if (parentChildCount != 1)
                    {
                        int index = selected.getParent().getIndex(selected);
                        if (index != parentChildCount -1)
                            index ++;
                        else
                            index --;

                        next = selected.getParent().getChildAt(index);
                    }
                    else
                        next = selected.getParent();

                    int index = selected.getParent().getIndex(selected);
                    treeModel.removeNodeFromParent(selected);

                    treeComponent.setSelectionPath(new TreePath(treeModel.getPathToRoot(next)));
                    treeComponent.requestFocus();
                }
            }
        }

        else if ("tree.cloneNode".equals(command)) {
            treeModel.beginUpdate();

            if (isNodeSelected()) {
                HOPNode node = getSelectedNode();
                HOPNode clone = node.createClone();

                if (node.getParent() instanceof MutableTreeNode) {
                    treeModel.insertNodeInto(clone, (MutableTreeNode) node.getParent(), node.getParent().getIndex(node) +1);
                }
            }
            treeModel.endUpdate();
        }

        else if ("tree.moveUpward".equals(command))
            TreeUtils.moveUpwardSelected(treeComponent);
        else if ("tree.moveDownward".equals(command))
            TreeUtils.moveDownwardSelected(treeComponent);

        else if (command.startsWith("tree.node.marker.changeShape")) {
            String[] args = command.split(" ", 2);
            if (args.length == 2)
                HOPTreeUtils.changeMarkerShapeSelected(treeComponent, HOPNodeMarkerType.find(args[1], HOPNodeMarkerType.DIAMOND));
        }

        else if (command.startsWith("tree.node.marker.changeColor"))  {
            String[] args = command.split(" ", 2);
            Color markerColor;

            if (args.length == 2) {
                markerColor = Color.decode(args[1]);
            } else {
                Color selected = JColorChooser.showDialog(this, "マーカー色の選択", Color.decode(settings.history.lastSelectedMarkerColor));
                if (selected != null) {
                    markerColor = selected;
                    settings.history.lastSelectedMarkerColor = colorCode(selected);
                } else return ;
            }

            HOPTreeUtils.changeMarkerColorSelected(treeComponent, markerColor);
        }


        // ------------------------------------------------------------------------------------------------------------
        // # Analyze
        // ------------------------------------------------------------------------------------------------------------

        else if (command.startsWith("analyze.characterCount.")) {
            String[] spl = command.split("\\.", 3);

            if ("all".equals(spl[2]))
                JOptionPane.showMessageDialog(this,
                        assets.localize("message.analyze.characterCount.all",
                                HOPDocumentUtils.countAllCharacter(document)),
                        assets.localize("message.analyze.characterCount.all.title"), JOptionPane.INFORMATION_MESSAGE);
            else if ("underSelected".equals(spl[2]))
                if (treeComponent.getSelectionPath() != null)
                    JOptionPane.showMessageDialog(this,
                            assets.localize("message.analyze.characterCount.underSelected",
                                    HOPDocumentUtils.countCharacterUnder((HOPNode) treeComponent.getSelectionPath().getLastPathComponent())),
                            assets.localize("message.analyze.characterCount.underSelected.title"), JOptionPane.INFORMATION_MESSAGE);
        }

        else if ("help.about".equals(command)) {
            JOptionPane.showMessageDialog(this, assets.localize("message.help.about", HOPConstants.VERSION), assets.localize("help.about.dialog.title"), JOptionPane.INFORMATION_MESSAGE);
        }

    }


}
