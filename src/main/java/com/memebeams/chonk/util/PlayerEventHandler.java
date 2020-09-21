package com.memebeams.chonk.util;

import com.memebeams.chonk.Chonk;
import com.memebeams.chonk.Config;
import com.memebeams.chonk.chunkloading.ChunkManager;
import com.memebeams.chonk.tile.ChunkLoaderTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class PlayerEventHandler {
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() == RegistryHandler.CHUNK_LOADER_ITEM.get() && !event.getWorld().isRemote()) {
            ChunkManager manager = ChunkManager.getInstance((ServerWorld) event.getWorld());
            PlayerEntity player = event.getPlayer();

            if (manager.getTileCount(player.getUniqueID()) >= Config.CHUNK_LOADERS_PER_PLAYER.get()) {
                event.setUseItem(Event.Result.DENY);
                player.sendMessage(new TranslationTextComponent("chonk.message.too_many_loaders"), Util.DUMMY_UUID);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().getBlock() == RegistryHandler.CHUNK_LOADER.get() && !event.getWorld().isRemote()) {
            ChunkLoaderTile tile = (ChunkLoaderTile) event.getWorld().getTileEntity(event.getPos());
            tile.setOwner(event.getEntity());
        }
    }
}
