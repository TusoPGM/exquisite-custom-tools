package com.e425.exquisitecustomtools;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Registry;

public class ModDataComponents {

    public static final DataComponentType<String> TOOL_UPGRADE =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    Identifier.fromNamespaceAndPath(
                            "exquisite-custom-tools",
                            "tool_upgrade"
                    ),
                    DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .build()
            );

    public static void register() {
        ExquisiteCustomTools.LOGGER.info(
                "Registering Exquisite Custom Tools data components"
        );
    }
}