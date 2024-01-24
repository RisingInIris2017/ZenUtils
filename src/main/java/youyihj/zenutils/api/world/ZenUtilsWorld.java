package youyihj.zenutils.api.world;

import com.google.common.base.Predicates;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.entity.MCEntityItem;
import crafttweaker.mc1120.player.MCPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.item.CrTItemHandler;
import youyihj.zenutils.api.liquid.CrTLiquidHandler;
import youyihj.zenutils.api.util.CrTUUID;
import youyihj.zenutils.api.util.catenation.ICatenationBuilder;
import youyihj.zenutils.api.util.catenation.persistence.CatenationPersistenceAPI;
import youyihj.zenutils.api.util.catenation.persistence.PersistedCatenationStarter;
import youyihj.zenutils.impl.capability.IZenWorldCapability;
import youyihj.zenutils.impl.capability.ZenWorldCapabilityHandler;
import youyihj.zenutils.impl.player.FakePlayerHolder;
import youyihj.zenutils.impl.util.catenation.CatenationBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.world.IWorld")
@SuppressWarnings("unused")
public class ZenUtilsWorld {
    @Nullable
    @ZenMethod
    public static IPlayer getPlayerByName(IWorld iWorld, String name) {
       return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getPlayerEntityByName(name));
    }

    @Nullable
    @ZenMethod
    public static IPlayer getPlayerByUUID(IWorld iWorld, CrTUUID uuid) {
        return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getPlayerEntityByUUID(uuid.getInternal()));
    }

    @ZenMethod
    public static List<IPlayer> getAllPlayers(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).playerEntities.stream().map(MCPlayer::new).collect(Collectors.toList());
    }

    @ZenMethod
    public static IPlayer getClosestPlayerToEntity(IWorld iWorld, IEntity iEntity, double distance, boolean spectator) {
        return getClosestPlayer(iWorld, iEntity.getPosX(), iEntity.getPosY(), iEntity.getPosZ(), distance, spectator);
    }

    @ZenMethod
    public static IPlayer getClosestPlayer(IWorld iWorld, double posX, double posY, double posZ, double distance, boolean spectator) {
        return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getClosestPlayer(posX, posY, posZ, distance, spectator));
    }

    @ZenMethod
    public static List<IEntity> getEntities(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).loadedEntityList.stream().map(CraftTweakerMC::getIEntity).collect(Collectors.toList());
    }

    @ZenMethod
    public static List<IEntityItem> getEntityItems(IWorld iWorld) {
        //noinspection Guava
        return CraftTweakerMC.getWorld(iWorld).getEntities(EntityItem.class, Predicates.alwaysTrue()).stream().map(MCEntityItem::new).collect(Collectors.toList());
    }

    @ZenMethod
    public static List<IPlayer> getPlayers(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).playerEntities.stream().map(MCPlayer::new).collect(Collectors.toList());
    }

    @ZenMethod
    public static IData getCustomWorldData(IWorld world) {
        IZenWorldCapability cap = getWorldCap(world);
        if (cap == null) return null;
        return cap.getData();
    }

    @ZenMethod
    public static void setCustomWorldData(IWorld world, IData data) {
        IZenWorldCapability cap = getWorldCap(world);
        if (cap == null) return;
        cap.setData(data);
    }

    @ZenMethod
    public static void updateCustomWorldData(IWorld world, IData data) {
        IZenWorldCapability cap = getWorldCap(world);
        if (cap == null) return;
        getWorldCap(world).updateData(data);
    }

    @ZenMethod
    public static IData getCustomChunkData(IWorld world, IBlockPos posToGetChunk) {
        return getChunkCap(world, posToGetChunk).getData();
    }

    @ZenMethod
    public static void setCustomChunkData(IWorld world, IData data, IBlockPos posToGetChunk) {
        getChunkCap(world, posToGetChunk).setData(data);
        getChunk(world, posToGetChunk).markDirty();
    }

    @ZenMethod
    public static void updateCustomChunkData(IWorld world, IData data, IBlockPos posToGetChunk) {
        getChunkCap(world, posToGetChunk).updateData(data);
        getChunk(world, posToGetChunk).markDirty();
    }

    @ZenMethod
    public static void destroyBlock(IWorld world, IBlockPos pos, boolean dropBlock) {
        CraftTweakerMC.getWorld(world).destroyBlock(CraftTweakerMC.getBlockPos(pos), dropBlock);
    }

    @ZenMethod
    public static CrTItemHandler getItemHandler(IWorld world, IBlockPos pos, @stanhebben.zenscript.annotations.Optional IFacing facing) {
        return Optional.ofNullable(CraftTweakerMC.getWorld(world).getTileEntity(CraftTweakerMC.getBlockPos(pos)))
                .map(tileEntity -> {
                    IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, CraftTweakerMC.getFacing(facing));
                    return CrTItemHandler.of(itemHandler);
                })
                .orElse(null);
    }

    @ZenMethod
    public static CrTLiquidHandler getLiquidHandler(IWorld world, IBlockPos pos, @stanhebben.zenscript.annotations.Optional IFacing facing) {
        return Optional.ofNullable(CraftTweakerMC.getWorld(world).getTileEntity(CraftTweakerMC.getBlockPos(pos)))
                .map(tileEntity -> {
                    IFluidHandler fluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, CraftTweakerMC.getFacing(facing));
                    return CrTLiquidHandler.of(fluidHandler);
                })
                .orElse(null);
    }

    @ZenMethod
    public static ICatenationBuilder catenation(IWorld world) {
        return new CatenationBuilder(world);
    }

    @ZenMethod
    public static PersistedCatenationStarter persistedCatenation(IWorld world, String catenationKey) {
        return CatenationPersistenceAPI.startPersistedCatenation(catenationKey, world);
    }

    @ZenMethod
    public static int getBlockBrightness(IWorld world, IBlockPos pos) {
        return CraftTweakerMC.getWorld(world).getLightFor(EnumSkyBlock.BLOCK, CraftTweakerMC.getBlockPos(pos));
    }

    @ZenMethod
    public static int getSkyBrightness(IWorld world, IBlockPos pos, @stanhebben.zenscript.annotations.Optional boolean subtracted) {
        World mcWorld = CraftTweakerMC.getWorld(world);
        int light = mcWorld.getLightFor(EnumSkyBlock.SKY, CraftTweakerMC.getBlockPos(pos));
        return subtracted ? Math.max(0, light - mcWorld.getSkylightSubtracted()) : light;
    }

    @ZenMethod
    public static int getBrightnessSubtracted(IWorld world, IBlockPos pos) {
        return CraftTweakerMC.getWorld(world).getLightFromNeighbors(CraftTweakerMC.getBlockPos(pos));
    }

    @ZenMethod
    @ZenGetter("gameRuleHelper")
    public static GameRuleHelper getGameRuleHelper(IWorld world) {
        return new GameRuleHelper(CraftTweakerMC.getWorld(world).getGameRules());
    }

    @ZenMethod
    @ZenGetter("fakePlayer")
    public static IPlayer getFakePlayer(IWorld world) {
        World mcWorld = CraftTweakerMC.getWorld(world);
        if (mcWorld instanceof WorldServer) {
            return CraftTweakerMC.getIPlayer(FakePlayerHolder.get(((WorldServer) mcWorld)));
        } else {
            throw new IllegalStateException("Server side only.");
        }
    }

    private static IZenWorldCapability getWorldCap(IWorld world) {
        return CraftTweakerMC.getWorld(world).getCapability(ZenWorldCapabilityHandler.ZEN_WORLD_CAPABILITY, null);
    }

    private static IZenWorldCapability getChunkCap(IWorld world, IBlockPos posToGetChunk) {
        return getChunk(world, posToGetChunk).getCapability(ZenWorldCapabilityHandler.ZEN_WORLD_CAPABILITY, null);
    }

    private static Chunk getChunk(IWorld world, IBlockPos pos) {
        return CraftTweakerMC.getWorld(world).getChunkFromBlockCoords(CraftTweakerMC.getBlockPos(pos));
    }
}
