package com.blacksnow1002.realmmod.event.handlers;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class DelayedTaskHandler {

    private static final List<DelayedTask> tasks = new ArrayList<>();

    public static void schedule(Runnable task, int delayTicks) {
        tasks.add(new DelayedTask(task, delayTicks));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Iterator<DelayedTask> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            DelayedTask task = iterator.next();
            task.remainingTicks--;

            if (task.remainingTicks <= 0) {
                task.task.run();
                iterator.remove();
            }
        }
    }

    private static class DelayedTask {
        final Runnable task;
        int remainingTicks;

        DelayedTask(Runnable task, int delayTicks) {
            this.task = task;
            this.remainingTicks = delayTicks;
        }
    }
}