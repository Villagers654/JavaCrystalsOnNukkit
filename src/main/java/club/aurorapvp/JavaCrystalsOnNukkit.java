package club.aurorapvp;

import club.aurorapvp.configs.CustomConfig;
import club.aurorapvp.events.Events;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import java.io.File;

public class JavaCrystalsOnNukkit extends PluginBase {
  public static Plugin INSTANCE;
  public static Server SERVER;
  public static PluginLogger LOGGER;
  public static File DATA_FOLDER;

  public void onEnable() {
    INSTANCE = this;
    SERVER = getServer();
    LOGGER = getLogger();
    DATA_FOLDER = getDataFolder();

    // Setup classes
    CustomConfig.init();
    Events.init();
  }
}
