package com.ddhuan.ifscience.common;

import net.minecraft.util.DamageSource;

public class customDamage {
    public final static DamageSource FurnaceBurn = new DamageSource("furnace_burn").setDamageBypassesArmor().setFireDamage();
    public final static DamageSource FurnaceExplosion = new DamageSource("furnace_explosion");
    public final static DamageSource BlastFurnaceExplosion = new DamageSource("blast_furnace_explosion");
    public final static DamageSource SmokerExplosion = new DamageSource("smoker_explosion");
    public final static DamageSource StoneAttractMagnet = new DamageSource("stone_by_attractMagnet").setProjectile().setDamageIsAbsolute();
}
