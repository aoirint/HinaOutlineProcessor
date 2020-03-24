package com.kanomiya.hinaoutlineprocessor;

import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;
import com.kanomiya.hinaoutlineprocessor.assets.InitializeException;
import com.kanomiya.hinaoutlineprocessor.assets.Initializer;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Kanomiya in 2017/01.
 */
public class Main
{
    public static void main(String[] args) throws InitializeException
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }


        HOPSettings settings;
        HOPAssets assets;

        try
        {
            Initializer initializer = new Initializer();

            settings = initializer.loadSettings();
            assets = initializer.loadAssets(settings);

            if (assets.themes.find(settings.view.theme) == null)
                settings.view.theme = "default";

        } catch (InitializeException e)
        {
            JOptionPane.showInternalMessageDialog(null, e, "例外", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return ;
        }


        HOPFrame frame = new HOPFrame(assets, settings);

        if (args.length > 0)
            frame.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "file.open " + args[0]));

        if (frame.document == null && settings.history.restorePreviousOnStartup && settings.history.fileHistory.size() > 0)
            frame.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "file.open " + settings.history.fileHistory.get(0)));

        if (frame.document == null)
            frame.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "file.createNew"));

        frame.setVisible(true);
    }
}
