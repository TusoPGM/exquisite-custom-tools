package com.e425.exquisitecustomtools.mixin;

import com.e425.exquisitecustomtools.ExquisiteCustomTools;
import com.e425.exquisitecustomtools.ModDataComponents;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {
    @Unique
    private static final Identifier exquisite$goldAxeModel =
            ExquisiteCustomTools.id("diamond_axe_gold");

    // Resolve from the gameplay marker, including axes upgraded before this release.
    // Only replace the local model ID; never mutate the rendered stack.
    @ModifyVariable(
            method = "appendItemLayers",
            at = @At("STORE"),
            ordinal = 0
    )
    private Identifier exquisite$selectUpgradeModel(
            Identifier original,
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemDisplayContext displayContext,
            Level level,
            ItemOwner owner,
            int seed) {
        return stack.is(Items.DIAMOND_AXE)
                && "gold_axe".equals(stack.get(ModDataComponents.TOOL_UPGRADE))
                ? exquisite$goldAxeModel : original;
    }
}
