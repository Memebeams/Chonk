package com.memebeams.chonk.chunkloading;

import com.memebeams.chonk.tile.ChunkLoaderTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

/**
 * Saved data for managing loaded chunks.
 *
 * Stores a MultiMap style Map of ChunkPos(long) to List of block positions of Chunkloaders.
 *
 * Removes the risk of vanilla forced chunks being unforced on us
 */

public class ChunkManager extends WorldSavedData {

    private static final String CHUNK_LIST_KEY = "chunks";
    private static final Logger LOGGER = LogManager.getLogger("Chonk ChunkManager");
    private static final String SAVEDATA_KEY = "chonk_force_chunks";
    /** Ticket type to keep the chunk loaded initially for a short time, so the Chunkloaders can register theirs */
    private static final TicketType<ChunkPos> INITIAL_LOAD_TICKET_TYPE = TicketType.create("chonk:initial_chunkload", Comparator.comparingLong(ChunkPos::asLong), 10);

    private ChunkMultimap chunks = new ChunkMultimap();

    private ChunkManager() {
        super(SAVEDATA_KEY);
    }

    @Override
    public void read(CompoundNBT nbt) {
        this.chunks = new ChunkMultimap();
        this.chunks.deserializeNBT(nbt.getList(CHUNK_LIST_KEY, Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put(CHUNK_LIST_KEY, this.chunks.serializeNBT());
        return compound;
    }

    public void registerChunk(ChunkPos chunk, BlockPos chunkLoaderPos) {
        this.chunks.add(chunk, chunkLoaderPos);
        markDirty();
    }

    public void deregisterChunk(ChunkPos chunk, BlockPos chunkLoaderPos) {
        this.chunks.remove(chunk, chunkLoaderPos);
        markDirty();
    }

    public static void worldLoad(ServerWorld world) {
        ChunkManager savedData = getInstance(world);
        LOGGER.info("Loading {} chunks for dimension {}", savedData.chunks.size(), world.getDimensionKey().getRegistryName());
        savedData.chunks.long2ObjectEntrySet().fastForEach(entry -> {
            //Add a separate ticket (which has a timout) to let the chunk tick for a short while (chunkloader will refresh if it's able)
            // This is required as we cannot do any validation about tiles (or blocks) being valid still or not, due to the multithreading
            // of world loading and some potential thread locking that exists from querying the world during load
            ChunkPos pos = new ChunkPos(entry.getLongKey());
            world.getChunkProvider().registerTicket(INITIAL_LOAD_TICKET_TYPE, pos, ChunkLoaderTile.TICKET_DISTANCE, pos);
        });
    }

    public static void tick(ServerWorld world) {
        ChunkManager instance = getInstance(world);
        if (!instance.chunks.isEmpty()) {
            //If we have any chunks loaded we need to reset the update entity tick
            // This is similar to what vanilla does for when it has force loaded chunks
            world.resetUpdateEntityTick();
        }
    }

    public static ChunkManager getInstance(ServerWorld world) {
        return world.getSavedData().getOrCreate(ChunkManager::new, SAVEDATA_KEY);
    }
}