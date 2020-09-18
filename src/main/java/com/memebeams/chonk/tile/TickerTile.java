package com.memebeams.chonk.tile;

import com.memebeams.chonk.util.RegistryHandler;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TickerTile extends TileEntity implements ITickableTileEntity {
    private static final Logger LOGGER = LogManager.getLogger("Chonk Ticker");
    private static int CURRENT_INDEX = 0;

    private int ticks = 0;
    private int index;

    public TickerTile() {
        super(RegistryHandler.TICKER_TILE.get());
        this.index = CURRENT_INDEX;
        CURRENT_INDEX++;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            this.ticks = this.ticks + 1;
            if (this.ticks >= 20) {
                this.ticks = 0;
                LOGGER.debug("Ticker {} is loaded and ticking", this.index);
            }
        }
    }
}
