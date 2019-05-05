/*
 * Copyright since 2019 Nhut Do <mr.nhut@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.donhut.simplemonitorclient.ui;

import fi.donhut.simplemonitorclient.SimpleMonitorClientApplication;
import fi.donhut.simplemonitorclient.util.SystemUtils;
import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Application tray icon class.
 *
 * @author Nhut Do (mr.nhut@gmail.com)
 */
public class AppTrayIcon extends TrayIcon {

    private static final Logger LOG = LoggerFactory.getLogger(AppTrayIcon.class);

    private static final String IMAGE_PATH = "/images/icons/antenna.png";
    private static final String TOOLTIP = "Simple monitor";

    private ConfigurableApplicationContext applicationContext;
    private final SystemTray systemTray;
    private final String appName;
    private final String logFolder;


    public AppTrayIcon(final ConfigurableApplicationContext applicationContext) throws AWTException {
        super(createImage(IMAGE_PATH, null), TOOLTIP);
        this.applicationContext = applicationContext;
        final Environment environment = applicationContext.getEnvironment();
        appName = environment.getProperty("spring.application.name");
        logFolder = environment.getProperty("logging.file", "");

        setImageAutoSize(true);
        setToolTip(appName);
        setPopupMenu(createSystemTrayPopupMenu());

        systemTray = SystemTray.getSystemTray();
        systemTray.add(this);
    }

    private static Image createImage(final String imagePath, final String description) {
        final URL imageURL = AppTrayIcon.class.getResource(imagePath);
        if (imageURL == null) {
            LOG.error("Failed to create image. Resource not found: {}", imagePath);
            return null;
        }
        return new javax.swing.ImageIcon(imageURL, description).getImage();
    }

    private PopupMenu createSystemTrayPopupMenu() {
        final PopupMenu popupMenu = new PopupMenu();
        popupMenu.setFont(new Font("Verdana", Font.PLAIN, 18));
        popupMenu.add(getAppNameAndVersion());
        popupMenu.addSeparator();
        popupMenu.add(createMenuItemToLogFolder());
        popupMenu.add(createMenuItemOpenConfigFile());
        popupMenu.addSeparator();
        popupMenu.add(createMenuItemRestart());
        popupMenu.add(createMenuItemExit());
        return popupMenu;
    }

    private String getAppNameAndVersion() {
        return appName + " v" + this.getClass().getPackage().getImplementationVersion();
    }

    private MenuItem createMenuItemToLogFolder() {
        final MenuItem menuItem = new MenuItem("Open log file/folder");
        menuItem.addActionListener(e -> {
            try {
                LOG.info("Opening explorer to the folder: {}", logFolder);
                java.nio.file.Path logFolderPath = java.nio.file.Paths.get(logFolder);
                if (logFolder.length() == 0) {
                    logFolderPath = logFolderPath.toAbsolutePath();
                }
                if (SystemUtils.isWindowsOS()) {
                    Runtime.getRuntime().exec("explorer.exe " + logFolderPath);
                } else {
                    final String messageText = "It's currently NOT supported." + " If you needed, make "
                            + "GitHub issue or git pull request.";
                    javax.swing.JOptionPane.showMessageDialog(null, messageText, appName,
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                final String errorText = "Unable to open folder: " + logFolder;
                javax.swing.JOptionPane.showMessageDialog(null, errorText, appName,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        return menuItem;
    }

    private MenuItem createMenuItemOpenConfigFile() {
        final MenuItem menuItem = new MenuItem("Open config file");
        menuItem.addActionListener(e -> {
            try {
                Path configFilePath = Paths.get(Paths.get("").toAbsolutePath().toString(),
                        "application.properties");
                if (SystemUtils.isWindowsOS()) {
                    Runtime.getRuntime().exec("notepad.exe " + configFilePath);
                } else {
                    final String messageText = "It's currently NOT supported." + " If you needed, make "
                            + "GitHub issue or git pull request.";
                    javax.swing.JOptionPane.showMessageDialog(null, messageText, appName,
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                final String errorText = "Unable to file: " + logFolder;
                javax.swing.JOptionPane.showMessageDialog(null, errorText, appName,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        return menuItem;
    }

    private MenuItem createMenuItemRestart() {
        final MenuItem menuItem = new MenuItem("Restart (for config change)");
        menuItem.addActionListener(e -> {
            final ApplicationArguments applicationArguments =
                    applicationContext.getBean(ApplicationArguments.class);

            final Thread thread = new Thread(() -> {
                applicationContext.close();
                applicationContext = SpringApplication.run(
                        SimpleMonitorClientApplication.class,
                        applicationArguments.getSourceArgs());
            });
            thread.setDaemon(false);
            thread.start();
        });
        return menuItem;
    }

    private MenuItem createMenuItemExit() {
        final MenuItem menuItem = new MenuItem("Exit");
        menuItem.addActionListener(e -> {
            final int exitCode = 0;
            final ExitCodeGenerator exitCodeGenerator = () -> exitCode;

            systemTray.remove(AppTrayIcon.this);
            SpringApplication.exit(applicationContext, exitCodeGenerator);
        });
        return menuItem;
    }
}