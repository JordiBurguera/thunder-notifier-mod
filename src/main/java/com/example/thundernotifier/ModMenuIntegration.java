package com.example.thundernotifier;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
                return parent -> {
                        ConfigBuilder builder = ConfigBuilder.create()
                                        .setParentScreen(parent)
                                        .setTitle(Component.literal("Thunder Notifier Config"));

                        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
                        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Component.literal("Enable Mod"),
                                                        ThunderNotifierClient.CONFIG_MANAGER.isEnableMod())
                                        .setDefaultValue(true)
                                        .setSaveConsumer(ThunderNotifierClient.CONFIG_MANAGER::setEnableMod)
                                        .build());

                        return builder.build();
                };
        }
}
