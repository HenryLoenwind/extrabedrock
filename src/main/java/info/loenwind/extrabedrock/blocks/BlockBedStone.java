package info.loenwind.extrabedrock.blocks;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockBedStone extends Block {

  public static BlockBedStone create() {
    return new BlockBedStone("bedstone");
  }

  public enum Variant implements IStringSerializable {
    STONE,
    STONEBRICK,
    COBBLESTONE,
    MOSSY_COBBLESTONE,
    DIRT,
    COARSE_DIRT,
    SAND,
    SANDSTONE,
    OBSIDIAN,
    BRICK,
    NETHERRACK,
    NETHER_BRICK,
    END_STONE

    ;

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getName() {
      return name().toLowerCase(Locale.ENGLISH);
    }
  }

  public static final @Nonnull PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

  @Nonnull
  protected final String name;

  @SuppressWarnings("null")
  public BlockBedStone(@Nonnull String name) {
    super(Material.BARRIER);
    this.name = name;
    setSoundType(SoundType.STONE);
    annotationDerp();
    setUnlocalizedName(name);
    setRegistryName(name);
    setHardness(-1.0F);
    setBlockUnbreakable();
    setResistance(6000000.0F);
    setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, Variant.STONE));
  }

  @SuppressWarnings("null")
  private void annotationDerp() {
    setCreativeTab(null); // is nullable
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(VARIANT, Variant.values()[MathHelper.clamp(meta, 0, Variant.values().length - 1)]);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(VARIANT).ordinal();
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { VARIANT });
  }

  @Override
  public void getSubBlocks(@Nonnull CreativeTabs itemIn, @Nonnull NonNullList<ItemStack> items) {
  }

  @Override
  protected boolean canSilkHarvest() {
    return false;
  }

  @Override
  public @Nonnull ItemStack getItem(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return ItemStack.EMPTY;
  }

  @Override
  public int quantityDropped(@Nonnull Random random) {
    return 0;
  }

  @Override
  public boolean canCollideCheck(@Nonnull IBlockState state, boolean hitIfLiquid) {
    return false;
  }

}