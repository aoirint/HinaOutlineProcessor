package com.kanomiya.hinaoutlineprocessor.dialog;

import com.kanomiya.hinaoutlineprocessor.HOPSettings;
import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.structure.HOPDocumentOwner;
import com.kanomiya.hinaoutlineprocessor.structure.HOPMode;

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
public class HOPDialogDocumentOwner extends JDialog implements ActionListener
{
    JTextField owner_name;
    JTextField owner_mail;
    JTextField owner_phone;
    JTextField owner_website;

    HOPDocumentOwner documentOwner;

    final boolean isEditable;

    public HOPDialogDocumentOwner(Window owner, HOPAssets assets, HOPSettings settings, HOPMode mode, HOPDocumentOwner documentOwner) {
        super(owner, assets.localize("owner.dialog.title"), ModalityType.APPLICATION_MODAL);

        setSize(300, 260);
        setLocationByPlatform(true);

        this.documentOwner = documentOwner;
        isEditable = mode == HOPMode.EDIT;

        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        panel.setLayout(layout);

        JLabel owner_name_label = new JLabel();
        JLabel owner_mail_label = new JLabel();
        JLabel owner_phone_label = new JLabel();
        JLabel owner_website_label = new JLabel();
        owner_name_label.putClientProperty(HOPAssets.HOP_I18N_ID, "owner.name");
        owner_mail_label.putClientProperty(HOPAssets.HOP_I18N_ID, "owner.mail");
        owner_phone_label.putClientProperty(HOPAssets.HOP_I18N_ID, "owner.phone");
        owner_website_label.putClientProperty(HOPAssets.HOP_I18N_ID, "owner.website");

        owner_name = new JTextField(documentOwner.name, 20);
        owner_mail = new JTextField(documentOwner.mail, 20);
        owner_phone = new JTextField(documentOwner.phone, 20);
        owner_website = new JTextField(documentOwner.website, 20);

        owner_name_label.setFont(font);
        owner_mail_label.setFont(font);
        owner_phone_label.setFont(font);
        owner_website_label.setFont(font);

        owner_name.setFont(font);
        owner_mail.setFont(font);
        owner_phone.setFont(font);
        owner_website.setFont(font);

        owner_name.setEditable(isEditable);
        owner_mail.setEditable(isEditable);
        owner_phone.setEditable(isEditable);
        owner_website.setEditable(isEditable);

        doGrouping(layout, new Component[][]
                {
                        { owner_name_label, owner_name },
                        { owner_mail_label, owner_mail },
                        { owner_phone_label, owner_phone },
                        { owner_website_label, owner_website },
                });

        add(panel);

        JButton confirm = new JButton();
        confirm.setFont(font);
        confirm.setActionCommand("confirm");
        confirm.putClientProperty(HOPAssets.HOP_I18N_ID, "button.ok");
        confirm.addActionListener(this);
        getRootPane().setDefaultButton(confirm);

        JButton clear = new JButton();
        clear.putClientProperty(HOPAssets.HOP_I18N_ID, "button.clear");
        clear.setFont(font);
        clear.setVisible(isEditable);
        clear.setActionCommand("clear");
        clear.addActionListener(this);

        JPanel panel2 = new JPanel();
        panel2.setBorder(new EmptyBorder(8,8,8,8));
        panel2.add(confirm);
        panel2.add(clear);

        add(panel2, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ("clear".equals(e.getActionCommand())) {
            if (isEditable) {
                owner_name.setText("");
                owner_mail.setText("");
                owner_phone.setText("");
                owner_website.setText("");
            }
        }
        else if ("confirm".equals(e.getActionCommand())) {
            if (isEditable) {
                documentOwner.name = owner_name.getText();
                documentOwner.mail = owner_mail.getText();
                documentOwner.phone = owner_phone.getText();
                documentOwner.website = owner_website.getText();
            }

            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

}
