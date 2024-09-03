package com.ddhuan.ifscience.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class customDamage {
    public final static DamageSource FurnaceBurn = new DamageSource("furnace_burn").setDamageBypassesArmor().setFireDamage();
    public final static DamageSource FurnaceExplosion = new DamageSource("furnace_explosion");
    public final static DamageSource BlastFurnaceExplosion = new DamageSource("blast_furnace_explosion");
    public final static DamageSource SmokerExplosion = new DamageSource("smoker_explosion");
    public final static DamageSource StoneAttractMagnet = new DamageSource("stone_by_attractMagnet").setProjectile().setDamageIsAbsolute();
    public final static DamageSource MinecartBump1 = new DamageSource("bump_by_minecart1");
    public final static DamageSource MinecartBump2 = new DamageSource("bump_by_minecart2");
    public final static DamageSource AngleGrinder1 = new DamageSource("angle_grinder1");
    public final static AngleGrinderDamageSource AngleGrinder2 = new AngleGrinderDamageSource("angle_grinder2");

    public static class AngleGrinderDamageSource extends DamageSource {
        public PlayerEntity player;

        public AngleGrinderDamageSource(String damageTypeIn) {
            super(damageTypeIn);
        }

        public AngleGrinderDamageSource setPlayer(PlayerEntity player) {
            this.player = player;
            return this;
        }

        @Override
        public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
            String s = "death.attack." + this.damageType;
            return player != null ? new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), player.getDisplayName()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), "???");
        }
    }
}
