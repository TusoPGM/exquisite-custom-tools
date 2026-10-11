
package com.e425.exquisitecustomtools.event;

import com.e425.exquisitecustomtools.ExquisiteCustomTools;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class FellingHandler {

    private static final ResourceKey<Enchantment> FELLING =
            ResourceKey.create(
                    Registries.ENCHANTMENT,
                    ExquisiteCustomTools.id("felling")
            );

    public static void register() {

        PlayerBlockBreakEvents.BEFORE.register(
                (level, player, pos, state, blockEntity) -> {

                    if (TreeProcessing.isHarvesting()) {
                        return true;
                    }

                    if (!(player instanceof ServerPlayer serverPlayer)) {
                        return true;
                    }

                    if (!state.is(BlockTags.LOGS)) {
                        return true;
                    }

                    ItemStack heldItem = player.getMainHandItem();

                    if (!heldItem.is(ItemTags.AXES)) {
                        return true;
                    }

                    ItemEnchantments enchantments =
                            heldItem.getEnchantments();

                    Holder<Enchantment> fellingHolder =
                            level.registryAccess()
                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                    .getOrThrow(FELLING);

                    int fellingLevel =
                            enchantments.getLevel(fellingHolder);

                    if (fellingLevel > 0) {
                        TreeProcessing.harvestTree(
                                level,
                                pos,
                                serverPlayer
                        );
                    }

                    return true;
                }
        );
    }
}
