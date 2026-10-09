package com.e425.exquisitecustomtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final TagKey<Item> UPGRADEABLE_TOOLS =
            TagKey.create(
                    Registries.ITEM,
                    Identifier.fromNamespaceAndPath(
                            "exquisite-custom-tools",
                            "upgradeable_tools"
                    )
            );
}
