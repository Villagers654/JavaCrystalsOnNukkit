package club.aurorapvp.events;

import club.aurorapvp.JavaCrystalsOnNukkit;
import club.aurorapvp.events.listeners.ExplosionEvents;
import club.aurorapvp.events.listeners.InteractEvents;
import cn.nukkit.Nukkit;

public class Events {
  public static void init() {
    JavaCrystalsOnNukkit.SERVER.getPluginManager()
        .registerEvents(new ExplosionEvents(), JavaCrystalsOnNukkit.INSTANCE);
    JavaCrystalsOnNukkit.SERVER.getPluginManager()
        .registerEvents(new InteractEvents(), JavaCrystalsOnNukkit.INSTANCE);
  }
}
