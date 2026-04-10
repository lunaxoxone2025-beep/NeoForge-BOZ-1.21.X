package net.crazhfty.blackopsz.perks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class CorePerkEffects {
    private static final int EFFECT_REFRESH_TICKS = 220;
    private static final String QUICK_REVIVE_TICK_KEY = "quick_revive_last_tick";
    private static final int QUICK_REVIVE_COOLDOWN_TICKS = 40 * 20;
    private static final int QUICK_REVIVE_TRIGGER_HEALTH = 7;

    private CorePerkEffects() {
    }

    public static void tick(ServerPlayer player) {
        if (PerkManager.hasPerk(player, PerkType.STAMIN_UP)) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, EFFECT_REFRESH_TICKS, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, EFFECT_REFRESH_TICKS, 0, false, false, true));
        }

        if (PerkManager.hasPerk(player, PerkType.SPEED_COLA)) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, EFFECT_REFRESH_TICKS, 0, false, false, true));
        }

        if (PerkManager.hasPerk(player, PerkType.DEADSHOT)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, EFFECT_REFRESH_TICKS, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.LUCK, EFFECT_REFRESH_TICKS, 0, false, false, true));
        }

        if (PerkManager.hasPerk(player, PerkType.QUICK_REVIVE)) {
            tickQuickRevive(player);
        }
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source == null) return;

        Entity direct = source.getEntity();
        if (direct instanceof Player attacker) {
            if (PerkManager.hasPerk(attacker, PerkType.DOUBLE_TAP)) {
                event.setAmount(event.getAmount() * 1.08f);
            }
            if (PerkManager.hasPerk(attacker, PerkType.DEADSHOT) && source.is(DamageTypeTags.IS_PROJECTILE)) {
                event.setAmount(event.getAmount() * 1.18f);
            }
        }

        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(victim instanceof ServerPlayer serverVictim)) return;

        if (PerkManager.hasPerk(victim, PerkType.PHD_FLOPPER) &&
                (source.is(DamageTypeTags.IS_FALL) || source.is(DamageTypeTags.IS_EXPLOSION))) {
            event.setCanceled(true);
            triggerPhdImmunityFx(serverVictim);
        }
    }

    public static void onUnequipped(ServerPlayer player, PerkType perk) {
        if (perk == PerkType.STAMIN_UP) {
            player.removeEffect(MobEffects.MOVEMENT_SPEED);
            player.removeEffect(MobEffects.JUMP);
        } else if (perk == PerkType.SPEED_COLA) {
            player.removeEffect(MobEffects.DIG_SPEED);
        } else if (perk == PerkType.QUICK_REVIVE) {
            player.getPersistentData().remove(QUICK_REVIVE_TICK_KEY);
        } else if (perk == PerkType.DEADSHOT) {
            player.removeEffect(MobEffects.NIGHT_VISION);
            player.removeEffect(MobEffects.LUCK);
        }
    }

    private static void tickQuickRevive(ServerPlayer player) {
        if (player.getHealth() > QUICK_REVIVE_TRIGGER_HEALTH) return;

        int lastTick = player.getPersistentData().getInt(QUICK_REVIVE_TICK_KEY);
        if (lastTick > 0 && player.tickCount - lastTick < QUICK_REVIVE_COOLDOWN_TICKS) return;

        player.getPersistentData().putInt(QUICK_REVIVE_TICK_KEY, player.tickCount);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 1, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, false, true, true));
        player.displayClientMessage(net.minecraft.network.chat.Component.translatable("perk.blackopsz.quick_revive.proc"), true);
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.7f, 1.35f, false);
    }

    private static void triggerPhdImmunityFx(ServerPlayer player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    14, 0.35, 0.25, 0.35, 0.01);
        }
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.4f, 1.7f, false);
    }
}
