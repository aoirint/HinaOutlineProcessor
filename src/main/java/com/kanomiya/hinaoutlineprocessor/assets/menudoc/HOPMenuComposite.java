package com.kanomiya.hinaoutlineprocessor.assets.menudoc;

import com.kanomiya.hinaoutlineprocessor.assets.HOPAssets;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPMenuComposite
{
    public List<HOPMenuElement> elements = new ArrayList<>();

    public Stream<JMenuItem> injectTo(Consumer<JMenuItem> addMenuItem, Runnable addSeparator) {
        List<JMenuItem> generated = new ArrayList<>();

        for (HOPMenuElement element: elements)
            injectToMenu_Reclusive(element, addMenuItem, addSeparator, generated);

        return generated.stream();
    }
    private void injectToMenu_Reclusive(HOPMenuElement element, Consumer<JMenuItem> addMenuItem, Runnable addSeparator, List<JMenuItem> generated) {

        if (element instanceof HOPMenu) {
            HOPMenu cast = (HOPMenu) element;

            JMenu menu = new JMenu(cast.name);
            menu.setName(cast.name);
            menu.setActionCommand(cast.command);
            menu.putClientProperty(HOPAssets.HOP_I18N_ID, cast.name);

            if (cast.icon != null)
                menu.setIcon(cast.icon);

            if (cast.mnemonic != '\0')
                menu.setMnemonic(cast.mnemonic);

            if (! cast.accelerator.isEmpty())
                menu.setAccelerator(KeyStroke.getKeyStroke(cast.accelerator));

            for (HOPMenuElement child: cast.children)
                injectToMenu_Reclusive(child, menu::add, menu::addSeparator, generated);

            addMenuItem.accept(menu);
            generated.add(menu);
        }
        else if (element instanceof HOPMenuItem) {
            HOPMenuItem cast = (HOPMenuItem) element;

            JMenuItem menuItem = new JMenuItem(cast.name);
            menuItem.setName(cast.name);
            menuItem.putClientProperty(HOPAssets.HOP_I18N_ID, cast.name);

            if (cast.icon != null)
                menuItem.setIcon(cast.icon);

            if (cast.mnemonic != '\0')
                menuItem.setMnemonic(cast.mnemonic);

            if (! cast.accelerator.isEmpty())
                menuItem.setAccelerator(KeyStroke.getKeyStroke(cast.accelerator));

            menuItem.setActionCommand(cast.command);

            addMenuItem.accept(menuItem);
            generated.add(menuItem);
        }
        else if (element instanceof HOPMenuItemSeparator) {
            addSeparator.run();
        }
    }




    private interface HOPMenuElement {
    }

    public static class HOPMenuItem implements HOPMenuElement {
        public String name;
        public String command;
        public ImageIcon icon;
        public char mnemonic;
        public String accelerator;

        public HOPMenuItem(String name, String command, ImageIcon icon, char mnemonic, String accelerator)
        {
            this.name = name;
            this.command = command;
            this.icon = icon;
            this.mnemonic = mnemonic;
            this.accelerator = accelerator;
        }

    }

    public static class HOPMenu extends HOPMenuItem {
        public List<HOPMenuElement> children = new ArrayList<>();

        public HOPMenu(String name, String command, ImageIcon icon, char mnemonic, String accelerator)
        {
            super(name, command, icon, mnemonic, accelerator);
        }

    }

    public static class HOPMenuItemSeparator implements HOPMenuElement {

    }

}
