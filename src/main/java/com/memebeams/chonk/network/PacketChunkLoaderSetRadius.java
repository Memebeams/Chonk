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

public class PacketChunkLoaderSetRadius {
    private final int radius;
    private final RegistryKey<World> dimension;
    private final BlockPos pos;

    public PacketChunkLoaderSetRadius(PacketBuffer buf) {
        this.radius = buf.readInt();
        this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, buf.readResourceLocation());
        this.pos = buf.readBlockPos();
    }

    public PacketChunkLoaderSetRadius(int radius, World world, BlockPos pos) {
        this.radius = radius;
        this.dimension = world.getDimensionKey();
        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(radius);
        buf.writeResourceLocation(this.dimension.getLocation());
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerWorld world = ctx.get().getSender().getServer().getWorld(dimension);
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof ChunkLoaderTile) {
                ((ChunkLoaderTile) tile).setRadius(this.radius);
            }
        });
        return true;
    }
}
