package com.kanomiya.hinaoutlineprocessor.assets;

import com.kanomiya.hinaoutlineprocessor.assets.menudoc.HOPMenuComposite;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPMenus {
    public final HOPMenuComposite menuBar, popupMenuTree, popupMenuTitle, popupMenuBody;

    public HOPMenus(HOPMenuComposite menuBar, HOPMenuComposite popupMenuTree, HOPMenuComposite popupMenuTitle, HOPMenuComposite popupMenuBody) {
        this.menuBar = menuBar;
        this.popupMenuTree = popupMenuTree;
        this.popupMenuTitle = popupMenuTitle;
        this.popupMenuBody = popupMenuBody;
    }

}
