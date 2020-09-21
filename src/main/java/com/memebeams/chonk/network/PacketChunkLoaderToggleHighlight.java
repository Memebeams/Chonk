package com.memebeams.chonk.network;

import com.memebeams.chonk.tile.ChunkLoaderTile;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChunkLoaderToggleHighlight {
    private final RegistryKey<World> dimension;
    private final BlockPos pos;

    public PacketChunkLoaderToggleHighlight(PacketBuffer buf) {
        this.dimension = RegistryKey.func_240903_a_(Registry.WORLD_KEY, buf.readResourceLocation());
        this.pos = buf.readBlockPos();
    }

    public PacketChunkLoaderToggleHighlight(World world, BlockPos pos) {
        this.dimension = world.getDimensionKey();
        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(this.dimension.func_240901_a_());
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerWorld world = ctx.get().getSender().getServer().getWorld(dimension);
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof ChunkLoaderTile) {
                ((ChunkLoaderTile) tile).toggleHighlight();
            }
        });
        return true;
    }
}
