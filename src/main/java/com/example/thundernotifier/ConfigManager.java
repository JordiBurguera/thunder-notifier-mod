package com.example.thundernotifier;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ThunderNotifier");
    private static final String CONFIG_FILE_NAME = "thunder_notifier.properties";
    private static final String WEBHOOK_FILE_NAME = "discord_webhook.txt";
    private static final String KEY_ENABLE_MOD = "enable_mod";

    private boolean enableMod;
    private String webhookUrl;

    private final File configFile;
    private final File webhookFile;

    public ConfigManager() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        this.configFile = configDir.resolve(CONFIG_FILE_NAME).toFile();
        this.webhookFile = configDir.resolve(WEBHOOK_FILE_NAME).toFile();

        loadConfig();
        loadWebhook();
    }

    private void loadConfig() {
        Properties props = new Properties();
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
                enableMod = Boolean.parseBoolean(props.getProperty(KEY_ENABLE_MOD, "true"));
            } catch (IOException e) {
                LOGGER.error("Failed to load config", e);
                enableMod = true;
            }
        } else {
            enableMod = true;
            saveConfig(props);
        }
    }

    private void saveConfig(Properties props) {
        props.setProperty(KEY_ENABLE_MOD, String.valueOf(enableMod));
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            props.store(out, "Thunder Notifier Configuration");
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    private void loadWebhook() {
        if (webhookFile.exists()) {
            try {
                webhookUrl = Files.readString(webhookFile.toPath()).trim();
            } catch (IOException e) {
                LOGGER.error("Failed to read webhook file", e);
                webhookUrl = "";
            }
        } else {
            webhookUrl = "INSERT_YOUR_WEBHOOK_URL_HERE";
            try {
                Files.writeString(webhookFile.toPath(), webhookUrl);
            } catch (IOException e) {
                LOGGER.error("Failed to create webhook file", e);
            }
        }
    }

    public boolean isEnableMod() {
        return enableMod;
    }

    public void setEnableMod(boolean enable) {
        this.enableMod = enable;
        saveConfig(new Properties());
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public boolean isWebhookConfigured() {
        return webhookUrl != null && !webhookUrl.isEmpty() && !webhookUrl.equals("INSERT_YOUR_WEBHOOK_URL_HERE");
    }
}
