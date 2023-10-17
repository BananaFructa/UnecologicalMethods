package BananaFructa.UnecologicalMethods;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;
import java.util.HashMap;

public class DrainTank extends FluidTank {

    private int universalCapacity = 0;

    private static HashMap<String,Boolean> bannedCache = new HashMap<>();
    private static HashMap<String,Boolean> pollutantCache = new HashMap<>();

    public DrainTank(int capacity) {
        super(capacity);
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        return null;
    }

    @Override
    public int fillInternal(FluidStack resource, boolean doFill) {
        if (resource.getFluid() == UEMContent.pollutedWater) return 0;

        String rn = resource.getFluid().getBlock().getRegistryName().toString();
        if (bannedCache.containsKey(rn)) if (bannedCache.get(rn)) return 0;
        else {
            for (String s : Config.bannedLiquids) {
                if (s.equals(rn)) {
                    bannedCache.put(rn,true);
                    return 0;
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

        if (!pollutant) return resource.amount;
        else {
            if (universalCapacity + resource.amount <= getCapacity()) {
                if (doFill) universalCapacity += resource.amount;
                return resource.amount;
            } else {
                int dif =  getCapacity() - universalCapacity;
                if (doFill) universalCapacity = getCapacity();
                return dif;

            }
        }
    }

    public void dump(int maxDrain) {
        if (maxDrain <= universalCapacity) {
            universalCapacity -= maxDrain;
        } else {
            int dif = universalCapacity;
            universalCapacity = 0;
        }
    }

    @Override
    public int getFluidAmount() {
        return universalCapacity;
    }

    @Override
    public boolean canDrain() {
        return false;
    }

    public void setFluidAmount(int amount) {
        universalCapacity = amount;
    }

}
