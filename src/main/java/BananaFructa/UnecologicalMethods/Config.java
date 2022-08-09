package BananaFructa.UnecologicalMethods;

import net.minecraftforge.common.config.Configuration;
import scala.Int;

import java.io.File;

public class Config {

    public static Configuration config;

    public static String[] pollutableBlocks;
    public static String[] nonPollutants;
    public static String[] bannedLiquids;
    public static int mbPerBlock;
    public static int tickFrequency;
    public static int maximumRange;

    public static void init(File configDir) {

        config = new Configuration(new File(configDir,"unecologicalmethods.cfg"));

        pollutableBlocks = config.getStringList("pollutable_liquids","general",new String[]{"minecraft:water"},"Liquids which can be polluted. Useful if any mod adds other kids of water");
        nonPollutants = config.getStringList("non_pollutants","general",new String[]{"minecraft:water"},"Liquids which will not produce polluted water");
        bannedLiquids = config.getStringList("banned_liquids","general",new String[]{"minecraft:lava"},"Liquids that cannot be drained.");
        mbPerBlock = config.getInt("mb_per_block","general",2000,1,Integer.MAX_VALUE,"How many mb of pollutant liquid will pollute a block of water(or other liquid). Polluted water itself as a liquid is not affected.");
        tickFrequency = config.getInt("tick_frequency","general",20,1, Integer.MAX_VALUE,"How many ticks between each drain block update.");
        maximumRange = config.getInt("maximum_range","general",-1,-1,Integer.MAX_VALUE,"Maximum pollution range of a drain block. -1 is infinite.");

        if (config.hasChanged()) config.save();
    }

}
