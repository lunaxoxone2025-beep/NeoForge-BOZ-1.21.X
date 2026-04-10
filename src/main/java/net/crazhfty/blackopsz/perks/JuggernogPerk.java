package net.crazhfty.blackopsz.perks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class JuggernogPerk {
    private static final String LAST_STAND_TICK_KEY = "juggernog_last_stand_tick";
    private static final String PLATE_TICK_KEY = "juggernog_plate_tick";

    private static final int EFFECT_REFRESH_TICKS = 220;
    private static final int HEALTH_BOOST_AMPLIFIER = 1;
    private static final int PASSIVE_RESISTANCE_AMPLIFIER = 0;

    private static final int LAST_STAND_THRESHOLD_HEALTH = 6;
    private static final int LAST_STAND_COOLDOWN_TICKS = 35 * 20;
    private static final int LAST_STAND_DURATION_TICKS = 5 * 20;

    private static final int PLATE_INTERVAL_TICKS = 45 * 20;
    private static final int PLATE_DURATION_TICKS = 10 * 20;
    private static final int PLATE_ABSORPTION_AMPLIFIER = 0;

    private JuggernogPerk() {
    }

    public static void tick(ServerPlayer player) {
        if (!PerkManager.hasPerk(player, PerkType.JUGGERNOG)) return;

        player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, EFFECT_REFRESH_TICKS, HEALTH_BOOST_AMPLIFIER, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_REFRESH_TICKS, PASSIVE_RESISTANCE_AMPLIFIER, false, false, true));

        int plateTick = player.getPersistentData().getInt(PLATE_TICK_KEY);
        if (plateTick == 0) {
            player.getPersistentData().putInt(PLATE_TICK_KEY, player.tickCount);
            return;
        }

        if (player.tickCount - plateTick >= PLATE_INTERVAL_TICKS) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, PLATE_DURATION_TICKS, PLATE_ABSORPTION_AMPLIFIER, false, true, true));
            player.getPersistentData().putInt(PLATE_TICK_KEY, player.tickCount);
            player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ARMOR_EQUIP_NETHERITE.value(), SoundSource.PLAYERS, 0.6F, 1.2F, false);
            player.displayClientMessage(Component.translatable("perk.blackopsz.juggernog.plate_online"), true);
        }
    }

    public static void onIncomingDamage(ServerPlayer player, DamageSource source) {
        if (!PerkManager.hasPerk(player, PerkType.JUGGERNOG)) return;
        if (source == null) return;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FALL)) return;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) return;

        if (player.getHealth() > LAST_STAND_THRESHOLD_HEALTH) return;

        int lastStandTick = player.getPersistentData().getInt(LAST_STAND_TICK_KEY);
        if (lastStandTick > 0 && player.tickCount - lastStandTick < LAST_STAND_COOLDOWN_TICKS) return;

        player.getPersistentData().putInt(LAST_STAND_TICK_KEY, player.tickCount);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, LAST_STAND_DURATION_TICKS, 1, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, LAST_STAND_DURATION_TICKS, 1, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, LAST_STAND_DURATION_TICKS, 1, false, true, true));
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.7F, 1.25F, false);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    20, 0.35, 0.6, 0.35, 0.01);
        }
        player.displayClientMessage(Component.translatable("perk.blackopsz.juggernog.last_stand"), true);
    }

    public static void onUnequipped(ServerPlayer player) {
        player.getPersistentData().remove(LAST_STAND_TICK_KEY);
        player.getPersistentData().remove(PLATE_TICK_KEY);
        player.removeEffect(MobEffects.HEALTH_BOOST);
        player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        player.removeEffect(MobEffects.ABSORPTION);
    }

    public static int getPlateIntervalTicks() {
        return PLATE_INTERVAL_TICKS;
    }

    public static int getPlateDurationTicks() {
        return PLATE_DURATION_TICKS;
    }

    public static int getLastStandCooldownTicks() {
        return LAST_STAND_COOLDOWN_TICKS;
    }

    public static int getLastStandDurationTicks() {
        return LAST_STAND_DURATION_TICKS;
    }
}
