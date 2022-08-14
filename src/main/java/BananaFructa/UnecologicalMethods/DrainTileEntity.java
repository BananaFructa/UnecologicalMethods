package BananaFructa.UnecologicalMethods;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.lwjgl.Sys;

import javax.annotation.Nullable;
import java.util.*;

public class DrainTileEntity extends TileEntityIEBase implements ITickable, IEBlockInterfaces.IConfigurableSides, IEBlockInterfaces.IBlockOverlayText {

    public FluidTank tank = new FluidTank(Config.mbPerBlock + 2000);
    private int tickCount = 0;

    private final Random random = new Random();

    HashSet<Long> positions = new HashSet<>();

    private static long ft = 0;

    static {
        for (int i = 7*4 - 1;i >= 0;i--) {
            ft >>= i;
            ft |= 1;
            ft <<= i;
        }
    }

    public DrainTileEntity() {

    }

    private final BlockPos[] dir = {
        new BlockPos(1,0,0),
        new BlockPos(-1,0,0),
        new BlockPos(0,0,1),
        new BlockPos(0,0,-1)
    };

    final int RIGHT = 0;
    final int LEFT = 1;
    final int UP = 2;
    final int DOWN = 3;

    int y = -1;

    private static HashMap<String,Boolean> bannedCache = new HashMap<>();
    private static HashMap<String,Boolean> pollutantCache = new HashMap<>();

    private boolean tooManyIterations = false;


    @Override
    public void update() {

        tickCount++;

        if (tickCount % (Config.tickFrequency * (tooManyIterations ? 5 : 1)) != 0) return;
        if (tank.getFluid() == null) return;

        String rn = tank.getFluid().getFluid().getBlock().getRegistryName().toString();
        if (bannedCache.containsKey(rn)) if (bannedCache.get(rn)) return;
        else {
            for (String s : Config.bannedLiquids) {
                if (s.equals(rn)) {
                    bannedCache.put(rn,true);
                    return;
                }
            }
            bannedCache.put(rn,false);
        }

        boolean pollutant = true;
        if (pollutantCache.containsKey(rn)) pollutant = pollutantCache.get(rn);
        else {
            for (String s : Config.nonPollutants) {
                if (s.equals(rn)) {
                    pollutant = false;
                }
            }
            pollutantCache.put(rn,pollutant);
        }

        if (getWorld().isRemote || getWorld().isBlockIndirectlyGettingPowered(getPos()) != 0 || tank.getFluidAmount() < Config.mbPerBlock) return;

        y = getPos().getY();

        for (int i = 0;i < 4;i++) {
            BlockPos pos = getPos().add(dir[i]);

            if (isValid(pos)) positions.add(convertPosition(pos.getX(),pos.getZ()));
        }

        int iteration = 0;

        do {

            BlockPos pos = checkForValid();

            if (pos != null) {
                if (pollutant) polluteBlock(pos);
                if (tank.getFluid().getFluid() == UEMContent.pollutedWater) tank.drain(1000,true);
                else tank.drain(Config.mbPerBlock,true);
                break;
            }

            iteration++;

            if (iteration >= Config.maximumRange && Config.maximumRange != -1) {
                tooManyIterations = true;
                return;
            }

        } while (advanceSearch());

        tooManyIterations = false;

        positions.clear();
    }

    private void polluteBlock(BlockPos pos) {
        getWorld().setBlockState(pos, UEMContent.blockPollutedWater.getDefaultState());
    }

    private BlockPos checkForValid() {

        for (Long l : positions) {
            BlockPos pos = getBlockPos(l);
            if (isWater(pos)) {
                return pos;
            }
        }


        for (Long l : positions) {
            BlockPos pos = getBlockPos(l);
            for (int i = 0; i < 4; i++) {
                if (isWater(pos.add(dir[i]))) {
                    return pos.add(dir[i]);
                }
            }
        }


        return null;
    }

    private boolean runProbability(float chance) {
        return 100 * chance >= random.nextInt(100) + 1;
    }

    private boolean advanceSearch() {

        HashSet<Long> toAdd = new HashSet<>();

        for (Long l : positions) {

            BlockPos[] pos = {
                    getBlockPos(l).add(dir[UP]).add(dir[RIGHT]),
                    getBlockPos(l).add(dir[UP]).add(dir[LEFT]),
                    getBlockPos(l).add(dir[DOWN]).add(dir[RIGHT]),
                    getBlockPos(l).add(dir[DOWN]).add(dir[LEFT])
            };

            for (BlockPos b : pos) {
                long bl = convertBlockPos(b);
                if (!toAdd.contains(bl) && !positions.contains(bl) && isValid(b) && runProbability(0.4f)) {
                    toAdd.add(bl);
                }
            }

        }

        positions.clear();
        positions.addAll(toAdd);

        return !positions.isEmpty();
    }

    private BlockPos getBlockPos(long l) {
        Tuple<Integer,Integer> t = getPosition(l);
        return new BlockPos(t.getFirst(),y,t.getSecond());
    }

    private long convertBlockPos(BlockPos blockPos) {
        return convertPosition(blockPos.getX(),blockPos.getZ());
    }

    private boolean isWater(BlockPos pos) {
        Block block = getWorld().getBlockState(pos).getBlock();
        return isWater(block);
    }

    private boolean isWater(Block block) {
        for (String registryName : Config.pollutableBlocks) {
            if(block.getRegistryName().toString().equals(registryName)) return true;
        }
        return false;
    }

    private boolean isValid(BlockPos pos) {
        if (!world.getWorldBorder().contains(pos)) return false;
        Block block = getWorld().getBlockState(pos).getBlock();
        return (isWater(pos) || block == UEMContent.blockPollutedWater) && !isWater(pos.add(new Vec3i(0,1,0)));
    }

    private long convertPosition(int x,int z) {
        long v = 0;
        v |= x + 30000000;
        v <<= 7 * 4;
        v |= z + 30000000;
        return v;
    }

    private Tuple<Integer,Integer> getPosition(long v) {
        int z = (int)(v & ft) - 30000000;
        v >>= 7*4;
        int x = (int)(v & ft) - 30000000;
        return new Tuple<>(x,z);
    }

    // =========================================================================================================================

    public int[] sideConfig = new int[] {1,0,1,1,1,1};

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        sideConfig = nbt.getIntArray("sideConfig");
        if(sideConfig==null || sideConfig.length!=6)
            sideConfig = new int[]{1,0,1,1,1,1};
        tank.readFromNBT(nbt.getCompoundTag("tank"));
        if(descPacket)
            this.markContainingBlockForUpdate(null);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        nbt.setIntArray("sideConfig", sideConfig);
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public IEEnums.SideConfig getSideConfig(int side)
    {
        return (side>=0&&side<6)? IEEnums.SideConfig.values()[this.sideConfig[side]+1]: IEEnums.SideConfig.NONE;
    }
    @Override
    public boolean toggleSide(int side, EntityPlayer p)
    {
        sideConfig[side]++;
        if(sideConfig[side]>1)
            sideConfig[side]=-1;
        this.markDirty();
        this.markContainingBlockForUpdate(null);
        getWorld().addBlockEvent(getPos(), this.getBlockType(), 0, 0);
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing==null||sideConfig[facing.ordinal()]==0) )
            return true;
        return super.hasCapability(capability, facing);
    }
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing==null||sideConfig[facing.ordinal()]==0) )
            return (T)tank;
        return super.getCapability(capability, facing);
    }

    @Override
    public String[] getOverlayText(EntityPlayer player, RayTraceResult mop, boolean hammer)
    {
        if(hammer && blusunrize.immersiveengineering.common.Config.IEConfig.colourblindSupport)
        {
            int i = sideConfig[Math.min(sideConfig.length-1, mop.sideHit.ordinal())];
            int j = sideConfig[Math.min(sideConfig.length-1, mop.sideHit.getOpposite().ordinal())];
            return new String[]{
                    I18n.format(Lib.DESC_INFO+"blockSide.facing")
                            +": "+ I18n.format(Lib.DESC_INFO+"blockSide.connectFluid."+i),
                    I18n.format(Lib.DESC_INFO+"blockSide.opposite")
                            +": "+ I18n.format(Lib.DESC_INFO+"blockSide.connectFluid."+j)
            };
        }
        return null;
    }
    @Override
    public boolean useNixieFont(EntityPlayer player, RayTraceResult mop)
    {
        return false;
    }
}
