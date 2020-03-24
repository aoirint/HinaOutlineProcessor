package com.kanomiya.hinaoutlineprocessor.dialog;

import com.kanomiya.hinaoutlineprocessor.HOPFrame;
import com.kanomiya.hinaoutlineprocessor.HOPSettings;
import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.io.DateFormatType;
import com.kanomiya.hinaoutlineprocessor.structure.HOPDocument;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import static com.kanomiya.hinaoutlineprocessor.HOPUtils.doGrouping;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPDialogDocumentSettings extends JDialog  implements ActionListener
{
    HOPFrame owner;
    HOPAssets assets;
    HOPSettings settings;
    HOPDocument document;

    DefaultComboBoxModel<DateFormatType> dateFormatTypeComboBoxModel;
    JComboBox<DateFormatType> dateFormatTypeComboBox;

    public HOPDialogDocumentSettings(HOPFrame owner, HOPAssets assets, HOPSettings settings, HOPDocument document) {
        super(owner, assets.localize("document.settings.dialog.title"), true);

        setSize(400, 100);
        setLocationByPlatform(true);

        this.owner = owner;
        this.assets = assets;
        this.settings = settings;
        this.document = document;

        JPanel panel = new JPanel();
        {
            GroupLayout layout = new GroupLayout(panel);
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
            panel.setLayout(layout);
            panel.setBorder(new EmptyBorder(8, 8, 8, 8));

            JLabel dateFormatType_label = new JLabel();
            dateFormatType_label.putClientProperty(HOPAssets.HOP_I18N_ID, "document.dateFormatType");

            dateFormatTypeComboBoxModel = new DefaultComboBoxModel<>();
            for (DateFormatType type: DateFormatType.values())
                dateFormatTypeComboBoxModel.addElement(type);

            dateFormatTypeComboBox = new JComboBox<>(dateFormatTypeComboBoxModel);
            dateFormatTypeComboBox.setSelectedItem(document.dateFormatType);

            JButton ok = new JButton();
            ok.setActionCommand("ok");
            ok.addActionListener(this);
            ok.putClientProperty(HOPAssets.HOP_I18N_ID, "button.ok");
            ok.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

            doGrouping(layout, new Component[][] {
                    {
                            dateFormatType_label, dateFormatTypeComboBox, ok,
                    },
            });
        }

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("ok".equals(command)) {
            DateFormatType dateFormat = (DateFormatType) dateFormatTypeComboBox.getSelectedItem();
            document.dateFormatType = dateFormat;

            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }
}
