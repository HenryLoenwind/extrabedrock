package info.loenwind.extrabedrock.config.gui;

import java.util.ArrayList;
import java.util.List;

import info.loenwind.extrabedrock.ExtraBedrockMod;
import info.loenwind.extrabedrock.config.ConfigHandler;
import info.loenwind.extrabedrock.config.Section;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigFactory extends GuiConfig {

  public GuiConfigFactory(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), ExtraBedrockMod.MODID, false, false, I18n.translateToLocal(ExtraBedrockMod.MODID + ".config.title"));
  }

  private static List<IConfigElement> getConfigElements(GuiScreen parent) {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String prefix = ExtraBedrockMod.MODID + ".config.";

    for (Section section : Section.values()) {
      if (!section.sync || !ConfigHandler.configLockedByServer) {
        list.add(new ConfigElement(ConfigHandler.configuration.getCategory(section.name).setLanguageKey(prefix + section.name)));
      }
    }

    return list;
  }
}
