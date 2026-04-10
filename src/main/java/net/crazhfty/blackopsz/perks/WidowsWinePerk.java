package net.crazhfty.blackopsz.perks;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WidowsWinePerk {
    public static final int MAX_CHARGES = 4;
    private static final int REGEN_INTERVAL_TICKS = 24 * 20;
    private static final double TRAP_RADIUS = 5.25D;
    private static final int TRAP_DURATION_TICKS = 60;
    private static final int TRAP_SLOW_AMPLIFIER = 3;
    private static final int TRAP_WEAKNESS_AMPLIFIER = 0;

    private static final Map<UUID, Long> COOLDOWN_MAP = new HashMap<>();
    private static final long COOLDOWN_TIME_MS = 1750L;

    public static boolean triggerOnHit(Player player, DamageSource source) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return false;
        if (!PerkManager.hasPerk(player, PerkType.WIDOWS_WINE)) return false;
        if (!isValidMeleeTrigger(source)) return false;

        int charges = PerkManager.getWidowsWineCharges(player);
        if (charges <= 0) {
            player.displayClientMessage(Component.translatable("perk.blackopsz.widows_wine.no_charges"), true);
            return false;
        }

        if (isOnCooldown(player.getUUID())) return false;

        PerkManager.setWidowsWineCharges(player, charges - 1);
        PerkManager.resetWidowsWineRegenTick(player, player.tickCount);
        executeTrap(serverLevel, player);
        setCooldown(player.getUUID(), System.currentTimeMillis());
        player.displayClientMessage(Component.translatable("perk.blackopsz.widows_wine.entrapped"), true);
        return true;
    }

    public static void tickRegen(ServerPlayer player) {
        if (!PerkManager.hasPerk(player, PerkType.WIDOWS_WINE)) return;

        int charges = PerkManager.getWidowsWineCharges(player);
        if (charges >= MAX_CHARGES) return;

        int lastRegenTick = PerkManager.getWidowsWineRegenTick(player);
        if (lastRegenTick == 0) {
            PerkManager.resetWidowsWineRegenTick(player, player.tickCount);
            return;
        }

        if (player.tickCount - lastRegenTick < REGEN_INTERVAL_TICKS) return;

        int newCharges = Math.min(MAX_CHARGES, charges + 1);
        PerkManager.setWidowsWineCharges(player, newCharges);
        PerkManager.resetWidowsWineRegenTick(player, player.tickCount);
        player.displayClientMessage(Component.translatable("perk.blackopsz.widows_wine.recharged", newCharges, MAX_CHARGES), true);
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.4F, 1.6F, false);
    }

    public static int getRegenIntervalTicks() {
        return REGEN_INTERVAL_TICKS;
    }

    public static double getTrapRadius() {
        return TRAP_RADIUS;
    }

    public static int getTrapDurationTicks() {
        return TRAP_DURATION_TICKS;
    }

    public static long getCooldownTimeMs() {
        return COOLDOWN_TIME_MS;
    }

    private static void executeTrap(ServerLevel serverLevel, Player player) {
        // Activation sound
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPIDER_DEATH, SoundSource.PLAYERS, 1.1F, 0.75F);
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.9F, 0.55F);

        AABB area = player.getBoundingBox().inflate(TRAP_RADIUS);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                e -> e != player && e.isAlive() && e instanceof Mob
        );

        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, TRAP_DURATION_TICKS, TRAP_SLOW_AMPLIFIER, false, true));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, TRAP_DURATION_TICKS, TRAP_WEAKNESS_AMPLIFIER, false, true));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, TRAP_DURATION_TICKS, 1, false, true));

            if (target instanceof Mob mob) {
                mob.setTarget(null);
                mob.getNavigation().stop();
            }

            serverLevel.sendParticles(ParticleTypes.SQUID_INK, target.getX(), target.getY() + 1, target.getZ(), 16, 0.35, 0.6, 0.35, 0.04);
            ItemStack cobweb = new ItemStack(Items.COBWEB);
            for (int i = 0; i < 18; i++) {
                serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, cobweb),
                        target.getX(), target.getY() + 1.0, target.getZ(),
                        1, 0.45, 0.6, 0.45, 0.04);
            }
            serverLevel.sendParticles(ParticleTypes.CLOUD, target.getX(), target.getY() + 1.0, target.getZ(), 6, 0.25, 0.2, 0.25, 0.02);
        }
    }

    private static boolean isValidMeleeTrigger(DamageSource source) {
        if (source == null) return false;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) return false;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) return false;
        return source.getEntity() instanceof LivingEntity;
    }

    private static boolean isOnCooldown(UUID playerId) {
        Long last = COOLDOWN_MAP.get(playerId);
        if (last == null) return false;
        return System.currentTimeMillis() - last < COOLDOWN_TIME_MS;
    }

    private static void setCooldown(UUID playerId, long currentTimeMs) {
        COOLDOWN_MAP.put(playerId, currentTimeMs);
    }
}
