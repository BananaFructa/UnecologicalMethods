package BananaFructa.UnecologicalMethods;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockFarmlandTFC;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Random;

public class BlockPollutedWater extends BlockFluidClassic {

    private static Method turnToDirt;
    private static Method tfcTurnToDirt;

    private static BlockPos[] dir = {
      new BlockPos(1,0,0),
      new BlockPos(-1,0,0),
      new BlockPos(0,0,1),
      new BlockPos(0,0,-1)
    };

    static {
        try {
            turnToDirt = BlockFarmland.class.getDeclaredMethod("func_190970_b",World.class,BlockPos.class);
            turnToDirt.setAccessible(true);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void init() {
        if (UEM.TFCEnabled) {
            try {
                tfcTurnToDirt = BlockFarmlandTFC.class.getDeclaredMethod("turnToDirt",World.class,BlockPos.class);
                tfcTurnToDirt.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public BlockPollutedWater() {
        super(UEMContent.pollutedWater, Material.WATER);
        setUnlocalizedName("polluted_water");
        setRegistryName("polluted_water");
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
        super.updateTick(world, pos, state, rand);
        for (int i = 0;i < 4;i++) {
            if (UEM.TFCEnabled) if (world.getBlockState(pos.add(dir[i])).getBlock() instanceof BlockFarmlandTFC) {
                methodTFCTurnToDirt(world,pos.add(dir[i]),(BlockFarmlandTFC) world.getBlockState(pos.add(dir[i])).getBlock());
            }
            if (world.getBlockState(pos.add(dir[i])).getBlock() == Blocks.FARMLAND) {
                methodTurnToDirt(world,pos.add(dir[i]));
            }
        }
    }

    public static void methodTFCTurnToDirt(World world, BlockPos pos, BlockFarmlandTFC instance) {
        try {
            tfcTurnToDirt.invoke(instance,world,pos);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void methodTurnToDirt(World world, BlockPos blockPos) {
        try {
            turnToDirt.invoke(null,world,blockPos);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return false;
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityFishHook) {
            entityIn.setDead();
        }
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase base = (EntityLivingBase) entityIn;
            boolean poison = false,nausea = false,hunger = false;

            for (PotionEffect effect : base.getActivePotionEffects()) {
                int id = Potion.getIdFromPotion(effect.getPotion());
                int duration = effect.getDuration();
                if (id == 19 && duration >= 8 * 20) poison = true;
                if (id == 9 && duration >= 58 * 20) nausea = true;
                if (id == 17 && duration >= 18 * 20) hunger = true;
            }
            if (!(poison || nausea || hunger)) {
                for (Effect effect : Config.effectList) {
                    try {
                        base.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(effect.location), effect.duration, effect.amplification));
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }
}
