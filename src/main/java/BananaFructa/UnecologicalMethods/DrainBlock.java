package BananaFructa.UnecologicalMethods;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DrainBlock extends Block {

    public DrainBlock() {
        super(Material.IRON);
        setRegistryName("drain");
        setUnlocalizedName("drain");
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DrainTileEntity();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
