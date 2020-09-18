package com.memebeams.chonk;

import com.memebeams.chonk.chunkloading.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldTickHandler {
    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event) {
        if (!event.getWorld().isRemote()) {
            if (event.getWorld() instanceof ServerWorld) {
                ChunkManager.worldLoad((ServerWorld) event.getWorld());
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            tickEnd((ServerWorld) event.world);
        }
    }

    private void tickEnd(ServerWorld world) {
        if (!world.isRemote) {
            ChunkManager.tick(world);
        }
    }
}
