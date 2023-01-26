package youyihj.zenutils.impl.util.catenation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import crafttweaker.mc1120.world.MCWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import youyihj.zenutils.api.util.catenation.Catenation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class CatenationManager {
    private static final Multimap<World, Catenation> catenations = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
    private static final Multimap<World, Catenation> cantenationsToAdd = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

    private static final List<Catenation> clientCatenations = new ArrayList<>();
    private static final List<Catenation> clientCatenationsToAdd = new ArrayList<>();

    public static void addCatenation(World world, Catenation catenation) {
        cantenationsToAdd.put(world, catenation);
    }

    public static void addClientCatenation(Catenation catenation) {
        clientCatenations.add(catenation);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        switch (event.phase) {
            case START:
                CatenationManager.catenations.get(event.world).removeIf(it -> it.tick(new MCWorld(event.world)));
                break;
            case END:
                catenations.putAll(cantenationsToAdd);
                cantenationsToAdd.clear();
                break;
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.ClientTickEvent event) {
        switch (event.phase) {
            case START:
                CatenationManager.clientCatenations.removeIf(it -> it.tick(new MCWorld(Minecraft.getMinecraft().world)));
                break;
            case END:
                clientCatenations.addAll(clientCatenationsToAdd);
                clientCatenationsToAdd.clear();
                break;
        }
    }
}
