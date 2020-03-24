package com.kanomiya.hinaoutlineprocessor.dialog;

import com.kanomiya.hinaoutlineprocessor.HOPFrame;
import com.kanomiya.hinaoutlineprocessor.HOPSettings;
import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.assets.langdoc.Language;
import com.kanomiya.hinaoutlineprocessor.assets.langdoc.LanguageListCellRenderer;
import com.kanomiya.hinaoutlineprocessor.structure.HOPMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import static com.kanomiya.hinaoutlineprocessor.HOPUtils.doGrouping;
import static com.kanomiya.hinaoutlineprocessor.HOPUtils.panelGroup;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPDialogSettings extends JDialog implements ActionListener
{
    HOPFrame owner;
    HOPAssets assets;
    HOPSettings settings;

    JCheckBox askAutoInjectOwnerCheckBox, restorePreviousOnStartupCheckBox, emphasizeFocusedPaneCheckBox;

    JSpinner fileHistorySizeSpinner;


    public HOPDialogSettings(HOPFrame owner, HOPAssets assets, HOPSettings settings) {
        super(owner, assets.localize("settings.dialog.title"), true);

        setSize(540, 240);
        setLocationByPlatform(true);

        this.owner = owner;
        this.assets = assets;
        this.settings = settings;

        DefaultComboBoxModel<Language> languageModel = new DefaultComboBoxModel<>();
        assets.i18n.list().forEach(languageModel::addElement);

        JLabel language_label = new JLabel();
        language_label.putClientProperty(HOPAssets.HOP_I18N_ID, "settings.language");

        JComboBox<Language> languageComboBox = new JComboBox<>(languageModel);

        languageComboBox.setSelectedItem(assets.i18n.find(settings.view.language));
        languageComboBox.setRenderer(new LanguageListCellRenderer(assets));

        languageComboBox.addActionListener(event -> {
            if (languageComboBox.getSelectedItem() != null) {
                settings.view.language = ((Language) languageComboBox.getSelectedItem()).localeCode;
                owner.adjustAllContents();
            }
        });


        JButton defaultOwnerSettingButton = new JButton();
        defaultOwnerSettingButton.setActionCommand("settings.defaultOwner");
        defaultOwnerSettingButton.putClientProperty(HOPAssets.HOP_I18N_ID, "settings.owner.defaultOwner");
        defaultOwnerSettingButton.addActionListener(this);

        askAutoInjectOwnerCheckBox = new JCheckBox("", settings.owner.askAutoInjectOwner);
        askAutoInjectOwnerCheckBox.putClientProperty(HOPAssets.HOP_I18N_ID, "settings.owner.askAutoInject");

        JLabel fileHistorySize_label = new JLabel();
        fileHistorySize_label.putClientProperty(HOPAssets.HOP_I18N_ID, "settings.fileHistory.fileHistorySize");

        fileHistorySizeSpinner = new JSpinner(new SpinnerNumberModel(settings.history.fileHistorySize, 0, 30, 1));

        restorePreviousOnStartupCheckBox = new JCheckBox("", settings.history.restorePreviousOnStartup);
        restorePreviousOnStartupCheckBox.putClientProperty(HOPAssets.HOP_I18N_ID, "settings.fileHistory.restorePreviousOnStartup");

        emphasizeFocusedPaneCheckBox = new JCheckBox("", settings.edit.emphasizeFocusedPane);
        emphasizeFocusedPaneCheckBox.putClientProperty(HOPAssets.HOP_I18N_ID, "settings.edit.emphasizeFocusedPane");



        JPanel panel_ok = new JPanel();
        panel_ok.setBorder(new EmptyBorder(8,8,8,8));
        {
            JButton ok = new JButton();
            ok.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            ok.addActionListener(this);
            ok.putClientProperty(HOPAssets.HOP_I18N_ID, "button.ok");
            ok.setActionCommand("confirm");
            getRootPane().setDefaultButton(ok);

            panel_ok.add(ok);
        }

        add(panelGroup(new Component[][]{
                {
                        language_label, languageComboBox
                },
                {
                        defaultOwnerSettingButton, askAutoInjectOwnerCheckBox
                },
                {
                        fileHistorySize_label, fileHistorySizeSpinner
                },
                {
                        restorePreviousOnStartupCheckBox, null
                },
                {
                        emphasizeFocusedPaneCheckBox, null
                },
        }));
        add(panel_ok, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        if ("settings.defaultOwner".equals(command)) {
            owner.ownerSettingsDialog = new HOPDialogDocumentOwner(this, assets, settings, HOPMode.EDIT, settings.owner.defaultOwner);
            owner.adjust_Contents(owner.ownerSettingsDialog);
            owner.ownerSettingsDialog.setVisible(true);
        }
        else if ("confirm".equals(command)) {
            settings.owner.askAutoInjectOwner = askAutoInjectOwnerCheckBox.isSelected();

            settings.history.fileHistorySize = (Integer) fileHistorySizeSpinner.getValue();
            settings.history.restorePreviousOnStartup = restorePreviousOnStartupCheckBox.isSelected();

            settings.edit.emphasizeFocusedPane = emphasizeFocusedPaneCheckBox.isSelected();

            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }
}
