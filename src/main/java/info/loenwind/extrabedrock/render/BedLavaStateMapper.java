package info.loenwind.extrabedrock.render;

import javax.annotation.Nonnull;

import info.loenwind.extrabedrock.ExtraBedrockMod;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = ExtraBedrockMod.MODID, value = Side.CLIENT)
public class BedLavaStateMapper extends StateMapperBase {

  @SuppressWarnings("null")
  @SubscribeEvent
  public static void onModelRegistryEvent(ModelRegistryEvent event) {
    ModelLoader.setCustomStateMapper(ExtraBedrockMod.blockBedLava, new BedLavaStateMapper());
  }

  @SuppressWarnings("null")
  @Override
  protected @Nonnull ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()), "normal");
  }

}
