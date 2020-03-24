package com.kanomiya.hinaoutlineprocessor.dialog;

import com.kanomiya.hinaoutlineprocessor.HOPFrame;
import com.kanomiya.hinaoutlineprocessor.HOPSettings;
import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.structure.HOPDocument;
import com.kanomiya.hinaoutlineprocessor.structure.HOPMode;
import com.kanomiya.hinaoutlineprocessor.structure.SimpleDocumentListener;
import com.kanomiya.hinaoutlineprocessor.util.Pair;
import javafx.scene.control.Tab;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.kanomiya.hinaoutlineprocessor.HOPUtils.*;

/**
 * Created by Kanomiya in 2017/02.
 */
public class SearchDialog extends JDialog implements ActionListener {
    public static final int MODE_NORMAL = 0;
    public static final int MODE_REGEX = 1;

    PlainDocument docKeyword = new PlainDocument();
    PlainDocument docReplacement = new PlainDocument();

    HOPFrame owner;
    HOPAssets assets;
    HOPSettings settings;

    public SearchDialog(HOPFrame owner, HOPAssets assets, HOPSettings settings, HOPDocument document, boolean selectReplace) {
        super(owner, assets.localize("text.search.dialog.title"), false);

        setSize(480, 290);
        setLocationByPlatform(true);

        this.owner = owner;
        this.assets = assets;
        this.settings = settings;
        // TODO: body/title兼用とか regex repl

        try {
            docKeyword.insertString(0, settings.search.keyword, null);
            docReplacement.insertString(0, settings.search.replacement, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        docKeyword.addDocumentListener((SimpleDocumentListener) event -> {
            try {
                settings.search.keyword = docKeyword.getText(0, docKeyword.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });

        docReplacement.addDocumentListener((SimpleDocumentListener) event -> {
            try {
                settings.search.replacement = docReplacement.getText(0, docReplacement.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });

        TabFind findTab = new TabFind(false);
        TabFind replaceTab = new TabFind(true);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(assets.localize("text.search.tab.find"), findTab);
        if (document.mode == HOPMode.EDIT) {
            tabbedPane.addTab(assets.localize("text.search.tab.replace"), replaceTab);

            if (selectReplace)
                tabbedPane.setSelectedComponent(replaceTab);
        }

        getRootPane().setDefaultButton(findTab.findNext);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() instanceof TabFind) {
                TabFind tab = (TabFind) tabbedPane.getSelectedComponent();
                tab.load();
                tab.adjust();

                if (tab == findTab)
                    getRootPane().setDefaultButton(findTab.findNext);
                else if (tab == replaceTab)
                    getRootPane().setDefaultButton(replaceTab.findNext);

            }
        });

        add(tabbedPane);
    }

    public Pattern keywordPattern() throws PatternSyntaxException {
        int flag = Pattern.MULTILINE;
        if (settings.search.regexFlagDotAll) flag = flag | Pattern.DOTALL;
        if (! settings.search.matchCase) flag = flag | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        String keyword = settings.search.keyword;
        if (settings.search.searchMode == MODE_NORMAL)
            keyword = Pattern.quote(keyword);

        return Pattern.compile(keyword, flag);
    }

    public Matcher replaceMatcher() throws PatternSyntaxException {
        return keywordPattern().matcher(owner.bodyComponent.getText());
    }

    public Pair<Integer, Integer> findNext(int searchStart) {
        int selectionStart = -1;
        int selectionEnd = -1;
        try {
            Matcher matcher = replaceMatcher();

            if (matcher.find(searchStart)) {
                selectionStart = matcher.start();
                selectionEnd = matcher.end();
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "正規表現が不正です");
        }

        if (selectionStart != -1 && selectionEnd != -1) {
            return Pair.of(selectionStart, selectionEnd);
        }

        return null;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        if ("findNext".equals(command)) {
            int searchStart = owner.bodyComponent.getSelectionEnd();
            if (searchStart == 0 && owner.bodyComponent.getSelectionStart() == 0)
                searchStart = owner.bodyComponent.getCaretPosition();

            Pair<Integer, Integer> result = findNext(searchStart);
            if (result == null) {
                if (settings.search.wrapAround) {
                    result = findNext(settings.search.directionReverse ? owner.bodyComponent.getText().length() : 0);
                }
            }

            if (result != null)
                owner.bodyComponent.select(result.left, result.right);

            owner.bodyComponent.requestFocus();
        }
        else if ("count".equals(command)) {
            int searchStart = 0;
            int count = 0;
            Pair<Integer, Integer> result;
            while ((result = findNext(searchStart)) != null) {
                searchStart = result.right +1;
                count ++;
            }

            JOptionPane.showMessageDialog(this, assets.localize("message.text.search.count", count));
        }
        else if ("replace".equals(command) || "replaceAll".equals(command)) {
            String replacement = settings.search.replacement;
            if (settings.search.searchMode == MODE_NORMAL) {
                replacement = Matcher.quoteReplacement(replacement);
            }

            // TODO: Preserve Case
            if ("replaceAll".equals(command))
                replaceMatcher().replaceAll(replacement);
            else
                replaceMatcher().replaceFirst(replacement);
        }

    }


    class TabFind extends JPanel implements ActionListener {
        JButton findNext, count, replace, replaceAll;

        JTextField textKeyword = new JTextField(20);
        JTextField textReplacement = new JTextField(20);
        JCheckBox checkBoxInSelection = new JCheckBox();

        JRadioButton radioNormalMode = new JRadioButton();
        JRadioButton radioRegexMode = new JRadioButton();

        JCheckBox checkBoxMatchCase = new JCheckBox();
        JCheckBox checkBoxRegexFlagDotAll = new JCheckBox();

        JRadioButton radioDirectionUp = new JRadioButton();
        JRadioButton radioDirectionDown = new JRadioButton();
        JCheckBox checkBoxWrapAround = new JCheckBox();

        public TabFind(boolean replaceTab) {
            checkBoxInSelection.addActionListener(this);
            radioNormalMode.addActionListener(this);
            radioRegexMode.addActionListener(this);
            checkBoxRegexFlagDotAll.addActionListener(this);
            checkBoxMatchCase.addActionListener(this);
            radioDirectionUp.addActionListener(this);
            radioDirectionDown.addActionListener(this);
            checkBoxWrapAround.addActionListener(this);

            textKeyword.setDocument(docKeyword);
            textReplacement.setDocument(docReplacement);

            ButtonGroup groupMode = new ButtonGroup();
            groupMode.add(radioNormalMode);
            groupMode.add(radioRegexMode);

            ButtonGroup groupDirection = new ButtonGroup();
            groupDirection.add(radioDirectionUp);
            groupDirection.add(radioDirectionDown);


            i18n(checkBoxInSelection, "text.search.inSelection");
            i18n(radioNormalMode, "text.search.mode.normal");
            i18n(radioRegexMode, "text.search.mode.regex");
            i18n(checkBoxRegexFlagDotAll, "text.search.rule.regex.dotAll");
            i18n(checkBoxMatchCase, "text.search.rule.matchCase");
            i18n(radioDirectionUp, "text.search.direction.up");
            i18n(radioDirectionDown, "text.search.direction.down");
            i18n(checkBoxWrapAround, "text.search.direction.wrapAround");

            findNext = i18nButton("text.search.findNext", "findNext", SearchDialog.this);
            count = i18nButton("text.search.count", "count", SearchDialog.this);
            replace = i18nButton("text.search.replace", "replace", SearchDialog.this);
            replaceAll = i18nButton("text.search.replaceAll", "replaceAll", SearchDialog.this);

            JLabel findWhat_label = i18nLabel("text.search.findWhat");
            JLabel replaceWith_label = i18nLabel("text.search.replaceWith");

            JPanel input_panel = panelGroup(new Component[][]{
                    {findWhat_label, textKeyword, findNext, },
                    replaceTab ? new Component[] {replaceWith_label, textReplacement, replace} :
                            new Component[] {null, null, count},
                    {null, null, checkBoxInSelection},
            });

            replaceWith_label.setVisible(replaceTab);
            textReplacement.setVisible(replaceTab);
            replace.setVisible(replaceTab);

            doGrouping(this, new Component[][]{
                    {
                        input_panel,
                    },
                    {
                            panelGroup(new Component[][]{
                                    {
                                            titledBorder(assets.localize("text.search.rule"), panelGroup(new Component[][]{
                                                    {checkBoxRegexFlagDotAll, },
                                                    {checkBoxMatchCase, },
                                            })),
                                            titledBorder(assets.localize("text.search.mode"), panelGroup(new Component[][]{
                                                    {radioNormalMode,},
                                                    {radioRegexMode,}
                                            })),
                                            titledBorder(assets.localize("text.search.direction"), panelGroup(new Component[][]{
                                                    {radioDirectionUp,},
                                                    {radioDirectionDown,},
                                                    {checkBoxWrapAround, },
                                            })),
                                    },
                            }),
                    },
            });

            load();
            adjust();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            save();
            adjust();
        }

        public void load() {
            checkBoxInSelection.setSelected(settings.search.inSelection);

            radioRegexMode.setSelected(settings.search.searchMode == MODE_REGEX);
            radioNormalMode.setSelected(settings.search.searchMode == MODE_NORMAL);

            checkBoxRegexFlagDotAll.setSelected(settings.search.regexFlagDotAll);
            checkBoxMatchCase.setSelected(settings.search.matchCase);

            radioDirectionUp.setSelected(settings.search.directionReverse);
            radioDirectionDown.setSelected(! settings.search.directionReverse);
            checkBoxWrapAround.setSelected(settings.search.wrapAround);
        }

        public void save() {
            settings.search.inSelection = checkBoxInSelection.isSelected();
            settings.search.searchMode = radioRegexMode.isSelected() ? MODE_REGEX : MODE_NORMAL;
            settings.search.regexFlagDotAll = checkBoxRegexFlagDotAll.isSelected();
            settings.search.matchCase = checkBoxMatchCase.isSelected();
            settings.search.directionReverse = radioDirectionUp.isSelected();
            settings.search.wrapAround = checkBoxWrapAround.isSelected();
        }

        public void adjust() {
            checkBoxInSelection.setEnabled(settings.search.searchMode == MODE_NORMAL);
            checkBoxRegexFlagDotAll.setEnabled(settings.search.searchMode == MODE_REGEX);
            radioDirectionUp.setEnabled(settings.search.searchMode == MODE_NORMAL);
        }
    }

}
