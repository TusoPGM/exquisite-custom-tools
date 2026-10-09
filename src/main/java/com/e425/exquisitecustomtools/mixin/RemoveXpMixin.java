package com.e425.exquisitecustomtools.mixin;

import com.e425.exquisitecustomtools.ModItemTags;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GrindstoneMenu.class)
public class RemoveXpMixin {

    @Shadow @Final
    private Container repairSlots;

}