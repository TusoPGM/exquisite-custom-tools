package com.e425.exquisitecustomtools.mixin;

import com.e425.exquisitecustomtools.ModDataComponents;
import com.e425.exquisitecustomtools.ModItemTags;
import com.e425.exquisitecustomtools.access.GrindstoneUpgradeAccess;
import com.e425.exquisitecustomtools.ExquisiteCustomTools;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin implements GrindstoneUpgradeAccess {
	@Shadow
	@Final
	private Container repairSlots;

	@Shadow
	@Final
	private Container resultSlots;

	@Unique
	private Inventory exquisite$inventory;

	@Unique
	private boolean exquisite$successfulUpgrade;

	@Override
	public boolean exquisite$isSuccessfulUpgrade() {
        return this.exquisite$successfulUpgrade;
    }


	@Inject(
			method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
			at = @At("RETURN")
	)
	private void exquisite$captureInventory(
			int containerId,
			Inventory inventory,
			ContainerLevelAccess access,
			CallbackInfo ci) {

		this.exquisite$inventory = inventory;
	}

	@Inject(
			method = "computeResult",
			at = @At("HEAD"),
			cancellable = true
	)
	private void exquisite$computeUpgrade(
			ItemStack input,
			ItemStack ingredient,
			CallbackInfoReturnable<ItemStack> cir) {
		this.exquisite$successfulUpgrade = false;

		if (input.is(ModItemTags.UPGRADEABLE_TOOLS) && ingredient.is(Items.GOLD_INGOT)) {
			ItemStack result = input.copy();
			Holder<Enchantment> mending =
					exquisite$getEnchantment(Enchantments.MENDING);
			boolean upgraded = exquisite$upgradeEnchantment(result, mending, 1);
			if (!upgraded) {
				cir.setReturnValue(ItemStack.EMPTY);
				return;
			}
			this.exquisite$successfulUpgrade = true;
			cir.setReturnValue(result);
		}

		if (input.is(ModItemTags.UPGRADEABLE_TOOLS) && ingredient.is(Items.IRON_INGOT)) {
			ItemStack result = input.copy();
			Holder<Enchantment> unbreaking =
					exquisite$getEnchantment(Enchantments.UNBREAKING);
			boolean upgraded = exquisite$upgradeEnchantment(result, unbreaking, 3);
			if (!upgraded) {
				cir.setReturnValue(ItemStack.EMPTY);
				return;
			}
			this.exquisite$successfulUpgrade = true;
			cir.setReturnValue(result);
		}

		if (input.is(ModItemTags.UPGRADEABLE_TOOLS) && ingredient.is(Items.LAPIS_LAZULI)) {
			ItemStack result = input.copy();
			Holder<Enchantment> efficiency =
					exquisite$getEnchantment(Enchantments.EFFICIENCY);
			boolean upgraded = exquisite$upgradeEnchantment(result, efficiency, 5);
			if (!upgraded) {
				cir.setReturnValue(ItemStack.EMPTY);
				return;
			}
			this.exquisite$successfulUpgrade = true;
			cir.setReturnValue(result);
		}

		if (input.is(ItemTags.AXES) && ingredient.is(Items.EMERALD)) {
			ItemStack result = input.copy();
			Holder<Enchantment> felling =
					exquisite$getEnchantment(ResourceKey.create(Registries.ENCHANTMENT, ExquisiteCustomTools.id("felling")));
			boolean upgraded = exquisite$upgradeEnchantment(result, felling, 1);
			if (!upgraded) {
				cir.setReturnValue(ItemStack.EMPTY);
				return;
			}
			this.exquisite$successfulUpgrade = true;
			cir.setReturnValue(result);
		}
	}

	@Unique
	private Holder<Enchantment> exquisite$getEnchantment(
			ResourceKey<Enchantment> enchantmentKey) {
		return this.exquisite$inventory.player
				.level()
				.registryAccess()
				.lookupOrThrow(Registries.ENCHANTMENT)
				.getOrThrow(enchantmentKey);
	}

	@Unique
	private boolean exquisite$upgradeEnchantment(
			ItemStack result,
			Holder<Enchantment> enchantment,
			int maxLevel) {
		int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, result);
		if (currentLevel >= maxLevel) {
			return false;
		}
		int newLevel = Math.min(currentLevel + 1, maxLevel);
		result.enchant(enchantment, newLevel);
		return true;
	}

	@ModifyArg(
			method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;",
					ordinal = 1
			),
			index = 0
	)
	private Slot exquisite$allowUpgradeIngredient(Slot original) {

		return new Slot(this.repairSlots, 1, 49, 40) {

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(Items.GOLD_INGOT)
						|| stack.is(Items.IRON_INGOT)
						|| stack.is(Items.LAPIS_LAZULI)
						|| stack.is(Items.EMERALD)
						|| stack.isDamageableItem()
						|| EnchantmentHelper.hasAnyEnchantments(stack);
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		};
	}
}