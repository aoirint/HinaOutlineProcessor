package com.kanomiya.hinaoutlineprocessor.assets;

import com.google.gson.Gson;
import com.kanomiya.hinaoutlineprocessor.HOPSettings;
import com.kanomiya.hinaoutlineprocessor.assets.langdoc.HOPLangDocHelper;
import com.kanomiya.hinaoutlineprocessor.assets.langdoc.Language;
import com.kanomiya.hinaoutlineprocessor.assets.menudoc.HOPMenuComposite;
import com.kanomiya.hinaoutlineprocessor.structure.HOPNodeMarkerType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.kanomiya.hinaoutlineprocessor.assets.menudoc.HOPMenuDocHelper.parseMenuDoc;

/**
 * Created by Kanomiya in 2017/02.
 */
public class Initializer {
    private Gson gson = new Gson();

    private final Path pathMenuBar, pathPopupMenuTree, pathPopupMenuTitle, pathPopupMenuBody;

    public Initializer() {
        Path pathAssets = Paths.get("assets");
        Path pathMenus = pathAssets.resolve( "menus");

        Path pathMenuBar = Paths.get("menuBar.xml");
        Path pathPopupMenuTree = Paths.get("popupMenuTree.xml");
        Path pathPopupMenuTitle = Paths.get("popupMenuTitle.xml");
        Path pathPopupMenuBody = Paths.get("popupMenuBody.xml");

        this.pathMenuBar = Files.exists(pathMenuBar) ? pathMenuBar : pathMenus.resolve(pathMenuBar);
        this.pathPopupMenuTree = Files.exists(pathPopupMenuTree) ? pathPopupMenuTree : pathMenus.resolve(pathPopupMenuTree);
        this.pathPopupMenuTitle = Files.exists(pathPopupMenuTitle) ? pathPopupMenuTitle : pathMenus.resolve(pathPopupMenuTitle);
        this.pathPopupMenuBody = Files.exists(pathPopupMenuBody) ? pathPopupMenuBody : pathMenus.resolve(pathPopupMenuBody);
    }

    public HOPSettings loadSettings() throws InitializeException {
        try {
            return Files.exists(HOPAssets.CONFIG_FILE) ?
                    gson.fromJson(Files.newBufferedReader(HOPAssets.CONFIG_FILE), HOPSettings.class) :
                    new HOPSettings();
        } catch (IOException e) {
            e.printStackTrace();
            throw new InitializeException(InitializeException.Cause.ILLEGAL_SETTINGS, e);
        }
    }

    public HOPAssets loadAssets(HOPSettings settings) throws InitializeException {
        // Languages
        final HOPI18n i18n;
        {
            Map<String, Language> languageMap = new HashMap<>();
            try {
                for (Path langDocPath: Files.walk(Paths.get("assets", "lang"), 1).collect(Collectors.toList())) {
                   if (Files.isRegularFile(langDocPath)) {
                        Language language = HOPLangDocHelper.parseLanguage(langDocPath);

                        languageMap.put(language.localeCode, language);
                    }
                }
            }
            catch (IOException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
                throw new InitializeException(InitializeException.Cause.ILLEGAL_ASSETS_LANGUAGES, e);
            }

            i18n = new HOPI18n(languageMap);
        }

        // Icons
        final HOPIcons icons;
        {
            BufferedImage appIcon;
            try {
                appIcon = ImageIO.read(Files.newInputStream(Paths.get("assets", "icon.png")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new InitializeException(InitializeException.Cause.ILLEGAL_ASSETS_APP_ICON, e);
            }

            // Marker Icon Images
            Map<HOPNodeMarkerType, BufferedImage> markerImages = new HashMap<>();
            for (HOPNodeMarkerType type: HOPNodeMarkerType.values()) {
                try
                {
                    BufferedImage image = ImageIO.read(Files.newInputStream(Paths.get("assets", "markers", type.fileName)));
                    markerImages.put(type, image);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    throw new InitializeException(InitializeException.Cause.ILLEGAL_ASSETS_MARKER_ICONS, e);
                }
            }

            // Flag Icons
            Map<String, ImageIcon> flagIcons = new HashMap<>();

            try {
                for (Path flagImgPath: Files.walk(Paths.get("assets", "flags"), 1).collect(Collectors.toList())) {
                    if (Files.isRegularFile(flagImgPath)) {
                        try (InputStream is = Files.newInputStream(flagImgPath)) {
                            BufferedImage image = ImageIO.read(is);

                            flagIcons.put(flagImgPath.getFileName().toString(), new ImageIcon(image));
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new InitializeException(InitializeException.Cause.ILLEGAL_ASSETS_FLAG_ICONS, e);
            }

            icons = new HOPIcons(appIcon, markerImages, flagIcons);
        }

        final HOPMenus menus;
        {
            try {
                HOPMenuComposite menu_menuBar = parseMenuDoc(pathMenuBar, icons);
                HOPMenuComposite menu_popupMenuTree = parseMenuDoc(pathPopupMenuTree, icons);
                HOPMenuComposite menu_popupMenuTitle = parseMenuDoc(pathPopupMenuTitle, icons);
                HOPMenuComposite menu_popupMenuBody = parseMenuDoc(pathPopupMenuBody, icons);

                menus = new HOPMenus(menu_menuBar, menu_popupMenuTree, menu_popupMenuTitle, menu_popupMenuBody);
            }
            catch (IOException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
                throw new InitializeException(InitializeException.Cause.ILLEGAL_ASSETS_MENU_DOCUMENT, e);
            }
        }

        HOPThemes themes;
        {
            Map<String, HOPTheme> themeMap = new HashMap<>();
            HOPTheme defaultTheme = new HOPTheme(true, "default", "Default", HOPTheme.createDefaultColorMap());
            themeMap.put(defaultTheme.id, defaultTheme);


            for (Path themeDir: Arrays.asList(HOPThemes.BUILTIN_DIR, HOPThemes.USER_DIR)) {
                if (Files.exists(themeDir)) {
                    try {
                        for (Path themePath: Files.walk(themeDir, 1).collect(Collectors.toList())) {
                            if (Files.isRegularFile(themePath)) {
                                try (InputStream is = Files.newInputStream(themePath)) {
                                    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                                    HOPTheme theme = HOPTheme.loadFrom(themeDir == HOPThemes.BUILTIN_DIR, document);

                                    themeMap.put(theme.id, theme);
                                }
                            }
                        }
                    } catch (IOException | SAXException | ParserConfigurationException e) {
                        e.printStackTrace();
                        throw new InitializeException(InitializeException.Cause.ILLEGAL_ASSETS_THEME_DOCUMENT, e);
                    }
                }
            }

            themes = new HOPThemes(themeMap);
        }

        return new HOPAssets(settings, i18n, icons, menus, themes);
    }

}
