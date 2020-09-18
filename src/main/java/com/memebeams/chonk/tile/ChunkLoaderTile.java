package com.memebeams.chonk.tile;

import com.memebeams.chonk.Config;
import com.memebeams.chonk.chunkloading.ChunkManager;
import com.memebeams.chonk.util.RegistryHandler;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ChunkLoaderTile extends TileEntity implements ITickableTileEntity {
    private static final Logger LOGGER = LogManager.getLogger("Chonk_ChunkLoader");
    private static final TicketType<ChunkLoaderTile> TICKET_TYPE = TicketType.create("chonk:chunk_loader",
            Comparator.comparing(TileEntity::getPos));
    /**
     * Not 100% sure what this is, but 2 means the ticket has the same value as a forceChunk()
     */
    public static final int TICKET_DISTANCE = 2;

    private final Set<ChunkPos> chunkSet = new ObjectOpenHashSet<>();

    private World prevWorld;
    private BlockPos prevPos;
    private int radius = 0;

    private boolean hasRegistered;
    private boolean isFirstTick = true;
    private boolean showHighlight = false;

    public ChunkLoaderTile() {
        super(RegistryHandler.CHUNK_LOADER_TILE.get());
    }

    public Set<ChunkPos> getChunkSet() {
        ChunkPos center = new ChunkPos(getPos());

        Set<ChunkPos> chunks = new HashSet<>();
        for (int x = center.x - radius; x <= center.x + radius; x++) {
            for (int z = center.z - radius; z <= center.z + radius; z++) {
                chunks.add(new ChunkPos(x, z));
            }
        }

        return chunks;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        if (radius < 0 || radius > Config.CHUNK_LOADER_MAX_RADIUS.get() || radius == this.radius) { return; }
        this.radius = radius;
        this.refreshChunkTickets();
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3);
    }

    public boolean showHighlight() {
        return this.showHighlight;
    }

    public void toggleHighlight() {
        this.showHighlight = !this.showHighlight;
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3);
    }

    public void releaseChunkTickets() {
        releaseChunkTickets(this.world);
    }

    private void releaseChunkTickets(World world) {
        releaseChunkTickets(world, prevPos);
    }

    private void releaseChunkTickets(World world, BlockPos pos) {
        LOGGER.debug("Attempting to remove chunk tickets. Pos: {} World: {}", pos, world.getDimensionKey().getRegistryName());
        ServerChunkProvider chunkProvider = (ServerChunkProvider) world.getChunkProvider();
        Iterator<ChunkPos> chunkIt = chunkSet.iterator();
        ChunkManager manager = ChunkManager.getInstance((ServerWorld) world);
        while (chunkIt.hasNext()) {
            ChunkPos chunkPos = chunkIt.next();
            if (pos != null) {
                manager.deregisterChunk(chunkPos, pos);
            }
            chunkProvider.releaseTicket(TICKET_TYPE, chunkPos, TICKET_DISTANCE, this);
            chunkIt.remove();
        }
        this.hasRegistered = false;
        this.prevWorld = null;
    }

    private void registerChunkTickets(World world) {
        ServerChunkProvider chunkProvider = (ServerChunkProvider) world.getChunkProvider();
        ChunkManager manager = ChunkManager.getInstance((ServerWorld) world);

        prevPos = this.getPos();
        prevWorld = world;

        for (ChunkPos chunkPos : this.getChunkSet()) {
            chunkProvider.registerTicket(TICKET_TYPE, chunkPos, TICKET_DISTANCE, this);
            manager.registerChunk(chunkPos, prevPos);
            chunkSet.add(chunkPos);
        }

        hasRegistered = true;
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if (world != null && !world.isRemote && world.getChunkProvider() instanceof ServerChunkProvider) {
            if (isFirstTick) {
                isFirstTick = false;
//                if (!canOperate()) {
//                    // If we just loaded but are not actually able to operate
//                    // release any tickets we have assigned to us that we loaded with
//                    releaseChunkTickets(world, tile.getPos());
//                }
            }

            if (hasRegistered && prevWorld != null && (prevPos == null || prevWorld != world || prevPos != this.getPos())) {
                releaseChunkTickets(prevWorld);
            }

//            if (hasRegistered && !canOperate()) {
//                releaseChunkTickets(world);
//            }

            if (!hasRegistered) {
                registerChunkTickets(world);
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);

        chunkSet.clear();
        ListNBT list = nbt.getList("chunkSet", Constants.NBT.TAG_LONG);
        for (INBT inbt : list) {
            chunkSet.add(new ChunkPos(((LongNBT) inbt).getLong()));
        }
        this.radius = nbt.getInt("radius");
        this.showHighlight = nbt.getBoolean("showHighlight");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        return this.writeNBT(nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.writeNBT(super.getUpdateTag());
    }

    private CompoundNBT writeNBT(CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        for (ChunkPos pos : chunkSet) {
            list.add(LongNBT.valueOf(pos.asLong()));
        }
        nbt.put("chunkSet", list);
        nbt.putInt("radius", this.radius);
        nbt.putBoolean("showHighlight", this.showHighlight);
        return nbt;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.handleUpdateTag(this.getBlockState(), pkt.getNbtCompound());
    }

    /**
     * Release and re-register tickets, call when chunk set changes
     */
    public void refreshChunkTickets() {
        if (prevWorld != null) {
            releaseChunkTickets(prevWorld);
        }
        if (!this.getWorld().isRemote()) {
            registerChunkTickets(Objects.requireNonNull(this.getWorld()));
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return Double.MAX_VALUE;
    }
}
