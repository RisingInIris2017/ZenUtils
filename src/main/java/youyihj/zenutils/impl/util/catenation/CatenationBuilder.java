package youyihj.zenutils.impl.util.catenation;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;
import youyihj.zenutils.api.util.catenation.*;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author youyihj
 */
public class CatenationBuilder implements ICatenationBuilder {
    private final IWorld world;
    private final Queue<ICatenationTask> tasks = new ArrayDeque<>();
    private IWorldCondition stopWhen;

    public CatenationBuilder(IWorld world) {
        this.world = world;
    }

    @Override
    public ICatenationBuilder addTask(ICatenationTask task) {
        tasks.add(task);
        return this;
    }

    @Override
    public ICatenationBuilder run(IWorldFunction function) {
        return addTask(new InstantTask(function));
    }

    @Override
    public ICatenationBuilder sleep(long ticks) {
        return addTask(new SleepTask(ticks));
    }

    @Override
    public ICatenationBuilder sleepUntil(IWorldCondition condition) {
        return addTask(new SleepUntilTask(condition));
    }

    @Override
    public ICatenationBuilder stopWhen(IWorldCondition condition) {
        stopWhen = condition;
        return this;
    }

    @Override
    public Catenation start() {
        Catenation catenation = new Catenation(tasks, stopWhen);
        if (world.isRemote()) {
            CraftTweakerAPI.logWarning("This catenation is only run on server, but the world is on client side.");
            CraftTweakerAPI.logWarning("If it is expected, please call `client.catenation()` to build catenation that is run on client.");
        } else {
            CatenationManager.addCatenation(CraftTweakerMC.getWorld(world), catenation);
        }
        return catenation;
    }
}
