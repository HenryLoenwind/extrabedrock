package info.loenwind.extrabedrock;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.Logger;

import info.loenwind.extrabedrock.blocks.BlockBedLava;
import info.loenwind.extrabedrock.blocks.BlockBedStone;
import info.loenwind.extrabedrock.config.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = ExtraBedrockMod.MODID, version = ExtraBedrockMod.VERSION, name = ExtraBedrockMod.MODID, guiFactory = "info.loenwind.extrabedrock.config.gui.ConfigFactory")
@EventBusSubscriber
public class ExtraBedrockMod {

  public static final String MODID = "extrabedrock";
  public static final String VERSION = "2.0.0";

  public static Logger LOG;

  public static SimpleNetworkWrapper NETWORK;

  public static ConfigHandler CONFIGHANDLER;

  public static Block blockBedStone;
  public static Block blockBedLava;

  @EventHandler
  public void preinit(FMLPreInitializationEvent event) {
    LOG = event.getModLog();
    NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ExtraBedrockMod.MODID);
    CONFIGHANDLER = new ConfigHandler(MODID, LOG, NETWORK);
    CONFIGHANDLER.init(event);
  }

  @SubscribeEvent
  public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
    event.getRegistry().register(blockBedStone = BlockBedStone.create());
    event.getRegistry().register(blockBedLava = BlockBedLava.create());
  }

}
