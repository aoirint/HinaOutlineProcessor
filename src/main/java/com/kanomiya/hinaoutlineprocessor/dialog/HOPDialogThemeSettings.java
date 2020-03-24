package com.kanomiya.hinaoutlineprocessor.dialog;

import com.kanomiya.hinaoutlineprocessor.ColorButton;
import com.kanomiya.hinaoutlineprocessor.HOPFrame;
import com.kanomiya.hinaoutlineprocessor.HOPSettings;
import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.assets.HOPTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import static com.kanomiya.hinaoutlineprocessor.HOPUtils.*;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPDialogThemeSettings extends JDialog implements ActionListener
{
    HOPFrame owner;
    HOPAssets assets;
    HOPSettings settings;

    DefaultComboBoxModel<HOPTheme> themeComboBoxModel;
    JComboBox<HOPTheme> themeComboBox;
    JButton themeCreate, themeCopy, themeRemove, themeUse;

    TextField fieldThemeName;
    ColorButton colorTreeForeground, colorTreeBackground, colorTreeSelectionForeground, colorTreeSelectionBackground;
    ColorButton colorTitleForeground, colorTitleBackground, colorTitleSelectionForeground, colorTitleSelectionBackground, colorTitleCaret;
    ColorButton colorBodyForeground, colorBodyBackground, colorBodySelectionForeground, colorBodySelectionBackground, colorBodyCaret;
    ColorButton colorEmphasisBorder;

    JButton apply;

    public HOPDialogThemeSettings(HOPFrame owner, HOPAssets assets, HOPSettings settings) {
        super(owner, assets.localize("theme.dialog.title"), true);

        setSize(500, 440);
        setLocationByPlatform(true);

        this.owner = owner;
        this.assets = assets;
        this.settings = settings;

        themeComboBoxModel = new DefaultComboBoxModel<>();
        assets.themes.list().forEach(themeComboBoxModel::addElement);


        JLabel themeComboBox_label = i18nLabel("theme.themeList");

        themeComboBox = new JComboBox<>(themeComboBoxModel);
        themeComboBox.setBorder(new EmptyBorder(4,4,4,4));
        themeComboBox.addActionListener(event -> {
            if (themeComboBox.getSelectedItem() != null) {
                loadFromTheme((HOPTheme) themeComboBox.getSelectedItem());

                revalidate();
            }
        });

        themeCreate = i18nButton("theme.create", "theme.create", this);
        themeCopy = i18nButton("theme.copy", "theme.copy", this);
        themeRemove = i18nButton("theme.remove", "theme.remove", this);
        themeUse = i18nButton("theme.use", "theme.use", this);
        themeUse.setFont(BIG_FONT);
        themeCreate.setFont(BIG_FONT);
        themeCopy.setFont(BIG_FONT);
        themeRemove.setFont(BIG_FONT);

        JLabel fieldThemeName_label = i18nLabel("theme.name");
        fieldThemeName = new TextField();

        apply = i18nButton("button.apply", "apply", this);
        apply.setFont(BIG_FONT);


        JLabel colorTitle_label = i18nLabel("theme.color.title");

        colorTitleForeground = new ColorButton();
        colorTitleBackground = new ColorButton();
        colorTitleSelectionForeground = new ColorButton();
        colorTitleSelectionBackground = new ColorButton();
        colorTitleCaret = new ColorButton();

        i18n(colorTitleForeground, "theme.color.foreground");
        i18n(colorTitleBackground, "theme.color.background");
        i18n(colorTitleSelectionForeground, "theme.color.selectionForeground");
        i18n(colorTitleSelectionBackground,"theme.color.selectionBackground");
        i18n(colorTitleCaret, "theme.color.caret");


        JLabel colorBody_label = i18nLabel("theme.color.body");
        colorBodyForeground = new ColorButton();
        colorBodyBackground = new ColorButton();
        colorBodySelectionForeground = new ColorButton();
        colorBodySelectionBackground = new ColorButton();
        colorBodyCaret = new ColorButton();
        i18n(colorBodyForeground, "theme.color.foreground");
        i18n(colorBodyBackground, "theme.color.background");
        i18n(colorBodySelectionForeground, "theme.color.selectionForeground");
        i18n(colorBodySelectionBackground, "theme.color.selectionBackground");
        i18n(colorBodyCaret, "theme.color.caret");


        JLabel colorTree_label = i18nLabel("theme.color.tree");
        colorTreeForeground = new ColorButton();
        colorTreeBackground = new ColorButton();
        colorTreeSelectionForeground = new ColorButton();
        colorTreeSelectionBackground = new ColorButton();

        i18n(colorTreeForeground, "theme.color.foreground");
        i18n(colorTreeBackground, "theme.color.background");
        i18n(colorTreeSelectionForeground, "theme.color.selectionForeground");
        i18n(colorTreeSelectionBackground, "theme.color.selectionBackground");


        JLabel colorOthers_label = i18nLabel("theme.color.others");
        colorEmphasisBorder = new ColorButton();
        i18n(colorEmphasisBorder, "theme.color.emphasisBorder");

        add(panelGroup(new Component[][] {
                {
                        panelGroup(new Component[][]{{themeComboBox_label, themeComboBox, themeUse,}}),
                },
                {
                        panelGroup(false, true, new Component[][]{{themeCreate, themeCopy, themeRemove,}}),
                },
                {
                        panelGroup(new Component[][]{{fieldThemeName_label, fieldThemeName, apply,}}),
                },
                {
                        titledBorder(assets.localize("theme.color"), panelGroup(new Component[][]{
                                {
                                        colorTitle_label, colorTitleForeground, colorTitleBackground, colorTitleCaret,
                                },
                                {
                                        null, colorTitleSelectionForeground, colorTitleSelectionBackground, null,
                                },
                                {
                                        colorBody_label, colorBodyForeground, colorBodyBackground, colorBodyCaret,
                                },
                                {
                                        null, colorBodySelectionForeground, colorBodySelectionBackground, null,
                                },
                                {
                                        colorTree_label, colorTreeForeground, colorTreeBackground, null,
                                },
                                {
                                        null, colorTreeSelectionForeground, colorTreeSelectionBackground, null,
                                },
                                {
                                        colorOthers_label, colorEmphasisBorder, null, null,
                                },
                        })),
                },
        }));

        themeComboBox.setSelectedItem(assets.themes.find(settings.view.theme));
    }

    public void loadFromTheme(HOPTheme theme) {
        fieldThemeName.setText(theme.name);

        colorTitleForeground.setColor(theme.getColor("titleComponent.foreground"));
        colorTitleBackground.setColor(theme.getColor("titleComponent.background"));
        colorTitleSelectionForeground.setColor(theme.getColor("titleComponent.selectionForeground"));
        colorTitleSelectionBackground.setColor(theme.getColor("titleComponent.selectionBackground"));
        colorTitleCaret.setColor(theme.getColor("titleComponent.caret"));

        colorBodyForeground.setColor(theme.getColor("bodyComponent.foreground"));
        colorBodyBackground.setColor(theme.getColor("bodyComponent.background"));
        colorBodySelectionForeground.setColor(theme.getColor("bodyComponent.selectionForeground"));
        colorBodySelectionBackground.setColor(theme.getColor("bodyComponent.selectionBackground"));
        colorBodyCaret.setColor(theme.getColor("bodyComponent.caret"));

        colorTreeForeground.setColor(theme.getColor("treeComponent.foreground"));
        colorTreeBackground.setColor(theme.getColor("treeComponent.background"));
        colorTreeSelectionForeground.setColor(theme.getColor("treeComponent.selectionForeground"));
        colorTreeSelectionBackground.setColor(theme.getColor("treeComponent.selectionBackground"));

        colorEmphasisBorder.setColor(theme.getColor("emphasisBorder"));

        boolean isEditable = ! theme.builtin;
        themeRemove.setEnabled(isEditable);
        fieldThemeName.setEditable(isEditable);

        colorTitleForeground.setEnabled(isEditable);
        colorTitleBackground.setEnabled(isEditable);
        colorTitleSelectionForeground.setEnabled(isEditable);
        colorTitleSelectionBackground.setEnabled(isEditable);
        colorTitleCaret.setEnabled(isEditable);

        colorBodyForeground.setEnabled(isEditable);
        colorBodyBackground.setEnabled(isEditable);
        colorBodySelectionForeground.setEnabled(isEditable);
        colorBodySelectionBackground.setEnabled(isEditable);
        colorBodyCaret.setEnabled(isEditable);

        colorTreeForeground.setEnabled(isEditable);
        colorTreeBackground.setEnabled(isEditable);
        colorTreeSelectionForeground.setEnabled(isEditable);
        colorTreeSelectionBackground.setEnabled(isEditable);

        colorEmphasisBorder.setEnabled(isEditable);

        apply.setEnabled(isEditable);

    }

    public void saveToTheme(HOPTheme theme) {
        theme.name = fieldThemeName.getText();

        theme.setColor("titleComponent.foreground", colorTitleForeground.getColor());
        theme.setColor("titleComponent.background", colorTitleBackground.getColor());
        theme.setColor("titleComponent.selectionForeground", colorTitleSelectionForeground.getColor());
        theme.setColor("titleComponent.selectionBackground", colorTitleSelectionBackground.getColor());
        theme.setColor("titleComponent.caret", colorTitleCaret.getColor());

        theme.setColor("bodyComponent.foreground", colorBodyForeground.getColor());
        theme.setColor("bodyComponent.background", colorBodyBackground.getColor());
        theme.setColor("bodyComponent.selectionForeground", colorBodySelectionForeground.getColor());
        theme.setColor("bodyComponent.selectionBackground", colorBodySelectionBackground.getColor());
        theme.setColor("bodyComponent.caret", colorBodyCaret.getColor());

        theme.setColor("treeComponent.foreground", colorTreeForeground.getColor());
        theme.setColor("treeComponent.background", colorTreeBackground.getColor());
        theme.setColor("treeComponent.selectionForeground", colorTreeSelectionForeground.getColor());
        theme.setColor("treeComponent.selectionBackground", colorTreeSelectionBackground.getColor());

        theme.setColor("emphasisBorder", colorEmphasisBorder.getColor());

        boolean isEditable = ! theme.builtin;
        if (isEditable) {
            try {
                theme.saveToFile();
            } catch (TransformerException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e, "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String inputId() {
        String id = JOptionPane.showInputDialog(this, assets.localize("message.theme.inputId"));
        if (id != null) {
            if (assets.themes.find(id) == null) {
                if (! id.contains("\\") && ! id.contains("/") && ! id.isEmpty()) {
                    return id;
                }
                else {
                    JOptionPane.showMessageDialog(this, assets.localize("message.theme.inputId.ngError"));
                }
            }
            else {
                JOptionPane.showMessageDialog(this, assets.localize("message.theme.inputId.duplicate"));
            }
        }

        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("theme.create".equals(command)) {
            String id = inputId();
            if (id != null) {
                HOPTheme theme = new HOPTheme(false, id, "New", HOPTheme.createDefaultColorMap());
                themeComboBoxModel.addElement(theme);
                assets.themes.themeMap.put(theme.id, theme);
                themeComboBox.setSelectedItem(theme);
            }
        }
        else if ("theme.copy".equals(command)) {
            if (themeComboBox.getSelectedItem() != null) {
                String id = inputId();
                if (id != null) {
                    HOPTheme oldTheme = ((HOPTheme) themeComboBox.getSelectedItem());
                    HOPTheme newTheme = new HOPTheme(false, id, oldTheme.name, (HashMap<String, Color>) oldTheme.colorMap.clone());
                    themeComboBoxModel.addElement(newTheme);
                    assets.themes.themeMap.put(newTheme.id, newTheme);
                    themeComboBox.setSelectedItem(newTheme);
                }
            }
        }
        else if ("theme.remove".equals(command)) {
            if (themeComboBox.getSelectedItem() != null && themeComboBoxModel.getSize() > 1) {
                HOPTheme theme = (HOPTheme) themeComboBox.getSelectedItem();
                themeComboBoxModel.removeElement(theme);
                assets.themes.themeMap.remove(theme);
            }
        }
        else if ("theme.use".equals(command)) {
            if (themeComboBox.getSelectedItem() != null) {
                HOPTheme theme = (HOPTheme) themeComboBox.getSelectedItem();
                settings.view.theme = theme.id;
                owner.adjust_ComponentSettings();
            }
        }
        else if ("apply".equals(command)) {
            if (themeComboBox.getSelectedItem() != null) {
                saveToTheme((HOPTheme) themeComboBox.getSelectedItem());
                owner.adjust_ComponentSettings();
                repaint();
            }
        }
    }
}
