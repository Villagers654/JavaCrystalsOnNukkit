package club.aurorapvp;

import club.aurorapvp.events.Events;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;

public class JavaCrystalsOnNukkit extends PluginBase {
  public static Plugin INSTANCE;
  public static Server SERVER;
  public static PluginLogger LOGGER;

  public void onEnable() {
    INSTANCE = this;
    SERVER = getServer();
    LOGGER = getLogger();

    // Setup classes
    Events.init();
  }
}
