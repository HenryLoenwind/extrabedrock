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
public class BlockBedLava extends Block {

  public static BlockBedLava create() {
    BlockBedLava result = new BlockBedLava("bedlava");
    return result;
  }

  public enum Variant implements IStringSerializable {
    LAVA,
    SOLID_LAVA,
    SOLID_LAVA_TOP,
    SOLID_LAVA_BOTTOM;

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
  public BlockBedLava(@Nonnull String name) {
    super(Material.LAVA);
    this.name = name;
    annotationDerp();
    setUnlocalizedName(name);
    setRegistryName(name);
    setLightLevel(1f);
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
    case SOLID_LAVA:
      return FULL_BLOCK_AABB;
    case SOLID_LAVA_BOTTOM:
      return AABB_BOTTOM;
    case SOLID_LAVA_TOP:
      return AABB_TOP;
    default:
    case LAVA:
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
    return PathNodeType.LAVA;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.SOLID;
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
    IBlockState other = world.getBlockState(pos);
    if (dummy.displaceIfPossible(world, pos) || (other.getBlock() == Blocks.FLOWING_LAVA && other.getValue(BlockDynamicLiquid.LEVEL) > 0)) {
      world.setBlockState(pos, Blocks.FLOWING_LAVA.getBlockState().getBaseState().withProperty(BlockDynamicLiquid.LEVEL, 0), 3);
    }
  }

  @Override
  public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return !world.getBlockState(pos.offset(side)).doesSideBlockRendering(world, pos.offset(side), side.getOpposite());
  }

  @Override
  public boolean doesSideBlockRendering(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    return true;
  }

  private final Dummy dummy = new Dummy();

  final class Dummy extends BlockFluidClassic {
    Dummy() {
      super(new Fluid(FluidRegistry.LAVA.getName(), null, null), Material.LAVA);
      this.density = FluidRegistry.LAVA.getDensity();
      this.temperature = FluidRegistry.LAVA.getTemperature();
      this.maxScaledLight = FluidRegistry.LAVA.getLuminosity();
      this.tickRate = FluidRegistry.LAVA.getViscosity() / 200;
      this.densityDir = FluidRegistry.LAVA.getDensity() > 0 ? -1 : 1;
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
      if (state.getBlock() == BlockBedLava.this) {
        return false;
      }

      if (displacements.containsKey(state.getBlock())) {
        return displacements.get(state.getBlock());
      }

      Material material = state.getMaterial();
      if (material.blocksMovement() || material == Material.WATER || material == Material.LAVA || material == Material.PORTAL) {
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
    return FluidRegistry.LAVA.getViscosity() / 200;
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

      if (world.getBlockState(blockpos1).getBlock() instanceof BlockBedLava) {
        event.setCanceled(true);
      }
    }
  }

  @Override
  public @Nonnull EnumPushReaction getMobilityFlag(@Nonnull IBlockState state) {
    return EnumPushReaction.BLOCK;
  }
}