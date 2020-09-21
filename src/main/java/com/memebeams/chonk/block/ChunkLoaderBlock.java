package com.memebeams.chonk.block;

import com.memebeams.chonk.container.ChunkLoaderContainer;
import com.memebeams.chonk.tile.ChunkLoaderTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

public class ChunkLoaderBlock extends Block {
    public ChunkLoaderBlock() {
        super(AbstractBlock.Properties
                .create(Material.ROCK)
                .hardnessAndResistance(5.0f, 6.0f)
                .sound(SoundType.STONE)
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ChunkLoaderTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof ChunkLoaderTile) {
                INamedContainerProvider provider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen.chonk.chunk_loader");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new ChunkLoaderContainer(i, world, pos, playerEntity);
                    }
                };

                NetworkHooks.openGui((ServerPlayerEntity) player, provider, tile.getPos());
            } else {
                throw new IllegalStateException("Tile was not ChunkLoaderTile!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if (!worldIn.isRemote()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile != null && tile instanceof ChunkLoaderTile) {
                ((ChunkLoaderTile) tile).releaseChunkTickets();
            }
        }
    }
}
