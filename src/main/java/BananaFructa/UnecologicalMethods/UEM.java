package BananaFructa.UnecologicalMethods;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPlacer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.swing.text.html.parser.Entity;

@Mod(modid = UEM.MODID, name = UEM.NAME, version = UEM.VERSION,dependencies = "after:immersiveengineering")
public class UEM
{
    public static final String MODID = "uem";
    public static final String NAME = "Unecological Methods";
    public static final String VERSION = "0.3.3";
    public static boolean TFCEnabled;

    public UEM() {

    }

    @Mod.EventHandler
    public void preeInit(FMLPreInitializationEvent event) {
        Config.init(event.getModConfigurationDirectory());
        UEMContent.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        UEMContent.registerTileEntities();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        TFCEnabled = Loader.isModLoaded("tfc");
        BlockPollutedWater.init();
    }

}

