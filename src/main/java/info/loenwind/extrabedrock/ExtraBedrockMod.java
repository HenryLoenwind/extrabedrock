package info.loenwind.extrabedrock;

import javax.annotation.Nonnull;

import info.loenwind.extrabedrock.blocks.BlockBedFluid;
import info.loenwind.extrabedrock.blocks.BlockBedStone;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ExtraBedrockMod.MODID, version = ExtraBedrockMod.VERSION, name = ExtraBedrockMod.MODID)
@EventBusSubscriber
public class ExtraBedrockMod {

  public static final String MODID = "extrabedrock";
  public static final String VERSION = "2.0.0";

  public static Block blockBedStone, blockBedLava, blockBedWater;

  @SubscribeEvent
  public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
    event.getRegistry().register(blockBedStone = BlockBedStone.create());
    event.getRegistry().register(blockBedLava = BlockBedFluid.createLava());
    event.getRegistry().register(blockBedWater = BlockBedFluid.createWater());
  }

}
