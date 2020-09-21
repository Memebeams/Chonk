package com.memebeams.chonk.util;

import com.memebeams.chonk.Chonk;
import com.memebeams.chonk.block.BlockItemBase;
import com.memebeams.chonk.block.ChunkLoaderBlock;
import com.memebeams.chonk.container.ChunkLoaderContainer;
import com.memebeams.chonk.tile.ChunkLoaderTile;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Chonk.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Chonk.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Chonk.MOD_ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Chonk.MOD_ID);


    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    // Blocks
    public static final RegistryObject<Block> CHUNK_LOADER = BLOCKS.register("chunk_loader", ChunkLoaderBlock::new);

    // Block Items
    public static final RegistryObject<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItemBase(CHUNK_LOADER.get()));

    // Tile Entity Types
    public static final RegistryObject<TileEntityType<ChunkLoaderTile>> CHUNK_LOADER_TILE =
            TILES.register("chunk_loader", () -> TileEntityType.Builder.create(ChunkLoaderTile::new, CHUNK_LOADER.get()).build(null));

    // Containers
    public static final RegistryObject<ContainerType<ChunkLoaderContainer>> CHUNK_LOADER_CONTAINER =
            CONTAINERS.register("chunk_loader", () -> IForgeContainerType.create((windowId, inv, data) -> new ChunkLoaderContainer(windowId, inv.player.getEntityWorld(), data.readBlockPos(), inv.player)));
}
