package com.e425.exquisitecustomtools.mixin;
import com.e425.exquisitecustomtools.access.GrindstoneUpgradeAccess;

import net.minecraft.world.inventory.GrindstoneMenu;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public class RemoveXpMixin {

    @Shadow
    @Final
    private GrindstoneMenu field_16780;

    @ModifyArg(
            method = "method_17417",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
            ),
            index = 2
    )

    private int exquisite$modifyXp(int originalXp) {

        boolean successfulUpgrade =
                ((GrindstoneUpgradeAccess) field_16780).exquisite$isSuccessfulUpgrade();

        if (successfulUpgrade) {
            return 0;
        }

        return originalXp;
    }

}