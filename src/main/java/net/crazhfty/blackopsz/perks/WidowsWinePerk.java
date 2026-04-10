package net.crazhfty.blackopsz.perks;

import net.crazhfty.blackopsz.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WidowsWinePerk {

    private static final String TAG_COOLDOWN = "blackopsz_widowswine_cd";
    private static final String TAG_ACTIVE = "blackopsz_widowswine_active";

    private static final int COOLDOWN_TICKS = 200; // 10s
    private static final int ROOT_TICKS = 100;     // 5s

    // =====================================================
    // CHECK PERK ITEM (AMULET)
    // =====================================================
    public static boolean hasPerkItem(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.is(ModItems.WIDOWS_WINE_AMULET.get())) {
                return true;
            }
        }
        return false;
    }

    public static void updatePerkState(Player player) {
        player.getPersistentData().putBoolean(TAG_ACTIVE, hasPerkItem(player));
    }

    // =====================================================
    // WEB EXPLOSION FX (WHITE ASH STYLE)
    // =====================================================
    private static void spawnWebExplosion(Player player) {

        if (!(player.level() instanceof ServerLevel level)) return;

        Vec3 pos = player.position();

        // ring explosion
        for (int i = 0; i < 40; i++) {

            double angle = Math.toRadians(i * 9);
            double radius = 2.5;

            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + 0.3;

            level.sendParticles(
                    ParticleTypes.WHITE_ASH,
                    x, y, z,
                    2,
                    0.1, 0.1, 0.1,
                    0.0
            );
        }

        // core burst
        level.sendParticles(
                ParticleTypes.CLOUD,
                pos.x, pos.y + 1.0, pos.z,
                35,
                0.6, 0.6, 0.6,
                0.05
        );

        // sparkle web
        level.sendParticles(
                ParticleTypes.SNOWFLAKE,
                pos.x, pos.y + 1.0, pos.z,
                25,
                0.4, 0.4, 0.4,
                0.02
        );
    }

    // =====================================================
    // FREEZE MOB (TOTAL CONTROL)
    // =====================================================
    private static void rootMob(LivingEntity entity) {

        // full stop movement
        entity.setDeltaMovement(0, 0, 0);
        entity.hurtMarked = true;

        // heavy debuff freeze
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ROOT_TICKS, 10, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.JUMP, ROOT_TICKS, 200, false, false));

        // optional weakness
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ROOT_TICKS, 1, false, false));
    }

    // =====================================================
    // MAIN TRIGGER (WHEN PLAYER GETS HIT)
    // =====================================================
    public static void onPlayerHurt(Player player) {

        if (!player.getPersistentData().getBoolean(TAG_ACTIVE)) return;

        var data = player.getPersistentData();

        if (data.getInt(TAG_COOLDOWN) > 0) return;

        data.putInt(TAG_COOLDOWN, COOLDOWN_TICKS);

        // sound
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.SPIDER_DEATH,
                SoundSource.PLAYERS,
                1.0F,
                0.7F
        );

        // FX
        spawnWebExplosion(player);

        Level level = player.level();

        AABB area = player.getBoundingBox().inflate(4.0);

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                e -> e != player
        );

        for (LivingEntity e : entities) {

            rootMob(e);

            // web particles on mobs
            spawnMobWebParticles(e);

            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
        }
    }

    // =====================================================
    // MOBS WEB PARTICLES
    // =====================================================
    private static void spawnMobWebParticles(LivingEntity entity) {

        if (!(entity.level() instanceof ServerLevel level)) return;

        Vec3 pos = entity.position();

        for (int i = 0; i < 12; i++) {

            double ox = (entity.getRandom().nextDouble() - 0.5) * 1.2;
            double oy = entity.getRandom().nextDouble() * 1.5;
            double oz = (entity.getRandom().nextDouble() - 0.5) * 1.2;

            level.sendParticles(
                    ParticleTypes.WHITE_ASH,
                    pos.x + ox,
                    pos.y + oy,
                    pos.z + oz,
                    1,
                    0,
                    0,
                    0,
                    0
            );
        }
    }

    // =====================================================
    // TICK SYSTEM (COOLDOWN + CHECK)
    // =====================================================
    public static void tick(Player player) {

        updatePerkState(player);

        if (!player.getPersistentData().getBoolean(TAG_ACTIVE)) return;

        var data = player.getPersistentData();

        int cd = data.getInt(TAG_COOLDOWN);

        if (cd > 0) {
            data.putInt(TAG_COOLDOWN, cd - 1);
        }
    }
}