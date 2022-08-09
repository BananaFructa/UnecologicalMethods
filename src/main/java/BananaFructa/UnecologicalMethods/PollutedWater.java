package BananaFructa.UnecologicalMethods;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class PollutedWater extends Fluid {

    public PollutedWater() {
        super("polluted_water", new ResourceLocation(UEM.MODID,"blocks/polluted_water_still"), new ResourceLocation(UEM.MODID, "blocks/polluted_water_flow"));
    }

}
