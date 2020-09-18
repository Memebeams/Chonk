package com.memebeams.chonk.container;

import com.memebeams.chonk.tile.ChunkLoaderTile;
import com.memebeams.chonk.util.RegistryHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkLoaderContainer extends Container {

    private final ChunkLoaderTile tile;
    private final PlayerEntity player;

    public ChunkLoaderContainer(int id, World world, BlockPos pos, PlayerEntity player) {
        super(RegistryHandler.CHUNK_LOADER_CONTAINER.get(), id);
        this.tile = (ChunkLoaderTile) world.getTileEntity(pos);
        this.player = player;
    }

    public ChunkLoaderTile getTile() {
        return tile;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, RegistryHandler.CHUNK_LOADER.get());
    }
}
