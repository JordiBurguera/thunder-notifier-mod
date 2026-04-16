package com.example.thundernotifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThunderNotifierClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ThunderNotifier");
    public static ConfigManager CONFIG_MANAGER;
    private boolean wasThundering = false;
    private NotificationManager notificationManager;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Thunder Notifier initialized!");

        CONFIG_MANAGER = new ConfigManager();
        this.notificationManager = new NotificationManager(CONFIG_MANAGER);

        ClientTickEvents.END_LEVEL_TICK.register(this::onWorldTick);
    }

    private void onWorldTick(ClientLevel world) {
        if (world == null)
            return;

        boolean isThundering = world.isThundering();

        // Check for rising edge (start of storm)
        if (isThundering && !wasThundering) {
            LOGGER.info("Thunderstorm started!");

            if (CONFIG_MANAGER.isEnableMod()) {
                notificationManager.sendNotification();

                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.sendSystemMessage(
                            Component.literal("§e[Thunder Notifier]§r Thunderstorm detected! Sending notification..."));
                }
            }
        }

        wasThundering = isThundering;
    }
}
