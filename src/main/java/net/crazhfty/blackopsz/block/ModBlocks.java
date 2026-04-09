package net.crazhfty.blackopsz.block;

import net.crazhfty.blackopsz.BlackOpsZ;
import net.crazhfty.blackopsz.item.ModItems;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(BlackOpsZ.MOD_ID);

    public static final DeferredBlock<Block> AETHER_CRYSTAL_BLOCK = registerBlock("aether_crystal_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.0f)
                    .noOcclusion()
                    .sound(SoundType.AMETHYST)));

    public static final DeferredBlock<Block> AETHER_CRYSTAL_ORE = registerBlock("aether_crystal_ore",
            () -> new DropExperienceBlock(
                    ConstantInt.of(3),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE) // Color en el mapa
                            .strength(3.0f, 3.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> ELEMENTAL_115_ORE = registerBlock("elemental_115_ore",
            () -> new DropExperienceBlock(
                    UniformInt.of(3, 7),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(4.5f, 3.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> ELEMENTAL_115_BLOCK = registerBlock("elemental_115_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .sound(SoundType.NETHERITE_BLOCK)));


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}