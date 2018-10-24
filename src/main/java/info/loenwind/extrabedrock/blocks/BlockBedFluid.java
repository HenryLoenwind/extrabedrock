package info.loenwind.extrabedrock.blocks;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.extrabedrock.ExtraBedrockMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = ExtraBedrockMod.MODID)
public class BlockBedFluid extends Block {

  public static BlockBedFluid createLava() {
    return new BlockBedFluid("bedlava", Material.LAVA);
  }

  public static BlockBedFluid createWater() {
    return new BlockBedFluid("bedwater", Material.WATER);
  }

  public enum Variant implements IStringSerializable {
    NORMAL,
    SOLID,
    SOLID_TOP,
    SOLID_BOTTOM;

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getName() {
      return name().toLowerCase(Locale.ENGLISH);
    }
  }

  public static final @Nonnull PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

  @Nonnull
  protected final String name;
  @Nonnull
  protected final Material material;

  @SuppressWarnings("null")
  protected BlockBedFluid(@Nonnull String name, @Nonnull Material material) {
    super(material);
    this.name = name;
    this.material = material;
    dummy = new Dummy(material == Material.LAVA ? FluidRegistry.LAVA : FluidRegistry.WATER);
    annotationDerp();
    setUnlocalizedName(name);
    setRegistryName(name);
    if (material == Material.LAVA) {
      setLightLevel(1f);
    } else {
      setLightOpacity(3);
    }
    setBlockUnbreakable();
    setResistance(6000000.0F);
    setHardness(-1.0F);
    setTickRandomly(true);
  }

  @SuppressWarnings("null")
  private void annotationDerp() {
    setCreativeTab(null); // is nullable
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(VARIANT, Variant.values()[MathHelper.clamp(meta, 0, Variant.values().length - 1)])
        .withProperty(BlockDynamicLiquid.LEVEL, 0);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(VARIANT).ordinal();
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return state.withProperty(BlockDynamicLiquid.LEVEL, 0);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, VARIANT, BlockDynamicLiquid.LEVEL);
  }

  @Override
  public void getSubBlocks(@Nonnull CreativeTabs itemIn, @Nonnull NonNullList<ItemStack> items) {
  }

  @Override
  public @Nonnull ItemStack getItem(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return ItemStack.EMPTY;
  }

  @Override
  public int quantityDropped(@Nonnull Random random) {
    return 0;
  }

  private static final double px = 1D / 16D;
  protected static final @Nonnull AxisAlignedBB AABB_TOP = new AxisAlignedBB(0 * px, 15 * px, 0 * px, 16 * px, 16 * px, 16 * px);
  protected static final @Nonnull AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0 * px, 0 * px, 0 * px, 16 * px, 1 * px, 16 * px);

  @Override
  @Nullable
  public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    switch (state.getValue(VARIANT)) {
    case SOLID:
      return FULL_BLOCK_AABB;
    case SOLID_BOTTOM:
      return AABB_BOTTOM;
    case SOLID_TOP:
      return AABB_TOP;
    default:
    case NORMAL:
      return NULL_AABB;
    }
  }

  @Override
  public boolean canCollideCheck(@Nonnull IBlockState state, boolean hitIfLiquid) {
    return false;
  }

  @Override
  @Nullable
  public PathNodeType getAiPathNodeType(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return material == Material.LAVA ? PathNodeType.LAVA : PathNodeType.WATER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return material == Material.LAVA ? BlockRenderLayer.SOLID : BlockRenderLayer.TRANSLUCENT;
  }

  @Override
  public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
    if (dummy.canDisplace(world, pos.down()))
      flowIntoBlock(world, pos.down());
    if (dummy.canFlowInto(world, pos.add(-1, 0, 0)))
      flowIntoBlock(world, pos.add(-1, 0, 0));
    if (dummy.canFlowInto(world, pos.add(1, 0, 0)))
      flowIntoBlock(world, pos.add(1, 0, 0));
    if (dummy.canFlowInto(world, pos.add(0, 0, -1)))
      flowIntoBlock(world, pos.add(0, 0, -1));
    if (dummy.canFlowInto(world, pos.add(0, 0, 1)))
      flowIntoBlock(world, pos.add(0, 0, 1));
  }

  @SuppressWarnings("null")
  protected void flowIntoBlock(World world, BlockPos pos) {
    final IBlockState other = world.getBlockState(pos);
    final BlockDynamicLiquid vanilla = material == Material.LAVA ? Blocks.FLOWING_LAVA : Blocks.FLOWING_WATER;
    if (dummy.displaceIfPossible(world, pos) || (other.getBlock() == vanilla && other.getValue(BlockDynamicLiquid.LEVEL) > 0)) {
      world.setBlockState(pos, vanilla.getBlockState().getBaseState().withProperty(BlockDynamicLiquid.LEVEL, 0), 3);
    }
  }

  @Override
  public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    final IBlockState other = world.getBlockState(pos.offset(side));
    if (material != Material.LAVA && (other.getBlock() == this || ((side == EnumFacing.DOWN || side == EnumFacing.UP) && other.getMaterial() == material))) {
      return false;
    }
    return !other.doesSideBlockRendering(world, pos.offset(side), side.getOpposite());
  }

  @Override
  public boolean doesSideBlockRendering(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    return material == Material.LAVA;
  }

  private final @Nonnull Dummy dummy;

  final class Dummy extends BlockFluidClassic {

    Dummy(@Nonnull Fluid fluid) {
      super(new Fluid(fluid.getName(), null, null), material);
      this.density = fluid.getDensity();
      this.temperature = fluid.getTemperature();
      this.maxScaledLight = fluid.getLuminosity();
      this.tickRate = fluid.getViscosity() / 200;
      this.densityDir = fluid.getDensity() > 0 ? -1 : 1;
    }

    @Override
    public boolean displaceIfPossible(World world, BlockPos pos) {
      return super.displaceIfPossible(world, pos);
    }

    @SuppressWarnings("null")
    @Override
    public boolean canFlowInto(IBlockAccess world, BlockPos pos) {
      if (world.isAirBlock(pos))
        return true;

      IBlockState state = world.getBlockState(pos);
      if (state.getBlock() == BlockBedFluid.this) {
        return false;
      }

      if (displacements.containsKey(state.getBlock())) {
        return displacements.get(state.getBlock());
      }

      Material otherMaterial = state.getMaterial();
      if (otherMaterial.blocksMovement() || otherMaterial == Material.WATER || otherMaterial == Material.LAVA || otherMaterial == Material.PORTAL) {
        return false;
      }

      @SuppressWarnings("hiding")
      int density = getDensity(world, pos);
      if (density == Integer.MAX_VALUE) {
        return true;
      }

      if (this.density > density) {
        return true;
      } else {
        return false;
      }
    }
  }

  @Override
  public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    world.scheduleUpdate(pos, this, tickRate(null));
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock,
      @Nonnull BlockPos neighbourPos) {
    world.scheduleUpdate(pos, this, tickRate(null));
  }

  @Override
  public int tickRate(@Nullable World world) {
    return (material == Material.LAVA ? FluidRegistry.LAVA : FluidRegistry.WATER).getViscosity() / 200;
  }

  @Override
  public boolean isReplaceable(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return false;
  }

  @SubscribeEvent
  public static void onBucket(FillBucketEvent event) {
    final RayTraceResult raytraceresult = event.getTarget();
    if (raytraceresult != null) {
      final World world = event.getWorld();
      BlockPos blockpos = raytraceresult.getBlockPos();

      boolean flag1 = world.getBlockState(blockpos).getBlock().isReplaceable(world, blockpos);
      BlockPos blockpos1 = flag1 && raytraceresult.sideHit == EnumFacing.UP ? blockpos : blockpos.offset(raytraceresult.sideHit);

      if (world.getBlockState(blockpos1).getBlock() instanceof BlockBedFluid) {
        event.setCanceled(true);
      }
    }
  }

  @Override
  public @Nonnull EnumPushReaction getMobilityFlag(@Nonnull IBlockState state) {
    return EnumPushReaction.BLOCK;
  }

  @Override
  public int getPackedLightmapCoords(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    int lightThis = world.getCombinedLight(pos, 0);
    int lightUp = world.getCombinedLight(pos.up(), 0);
    int lightThisBase = lightThis & 255;
    int lightUpBase = lightUp & 255;
    int lightThisExt = lightThis >> 16 & 255;
    int lightUpExt = lightUp >> 16 & 255;
    return (lightThisBase > lightUpBase ? lightThisBase : lightUpBase) | ((lightThisExt > lightUpExt ? lightThisExt : lightUpExt) << 16);
  }

}