package BananaFructa.UnecologicalMethods;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class UEMContent {

    public static Block drain;
    public static Item drainItem;
    public static Fluid pollutedWater;
    public static Block blockPollutedWater;
    public static Item itemBlockPolluted ;

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(DrainTileEntity.class,new ResourceLocation(UEM.MODID,DrainTileEntity.class.getSimpleName()));
    }

    public static void init() {
        registerFluids();
        drain = new DrainBlock();
        blockPollutedWater = new BlockPollutedWater();
    }

    public static void registerFluids() {
        pollutedWater = new PollutedWater();
        FluidRegistry.registerFluid(pollutedWater);
        FluidRegistry.addBucketForFluid(pollutedWater);
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        drainItem = new ItemBlock(drain).setRegistryName("drain");
        registry.register(drainItem);

        itemBlockPolluted = new ItemBlock(blockPollutedWater).setRegistryName("polluted_water");
        registry.register(itemBlockPolluted);

    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(blockPollutedWater);
        event.getRegistry().register(drain);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(itemBlockPolluted, 0, new ModelResourceLocation(itemBlockPolluted.getRegistryName(),"fluid"));
        ModelLoader.setCustomStateMapper(blockPollutedWater,new DefaultStateMapper());

        ModelLoader.setCustomModelResourceLocation(drainItem,0,new ModelResourceLocation(drainItem.getRegistryName(),"normal"));
        ModelLoader.setCustomStateMapper(drain,new DefaultStateMapper());
    }

}
