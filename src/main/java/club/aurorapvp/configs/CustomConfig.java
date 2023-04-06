package club.aurorapvp.configs;

import club.aurorapvp.JavaCrystalsOnNukkit;
import cn.nukkit.utils.Config;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CustomConfig {
  private static final HashMap<String, Object> DEFAULTS = new HashMap<>();
  private static final File FILE = new File(JavaCrystalsOnNukkit.DATA_FOLDER, "config.yml");
  private static Config config;

  public static void init() {
    reload();
    generateDefaults();
  }

  public static void generateDefaults() {
    DEFAULTS.put("damage.head-damage-multiplier", 0.17);
    DEFAULTS.put("damage.max-damage", 160.0);
    DEFAULTS.put("damage.min-damage", 1.0);
    DEFAULTS.put("damage.blocking-reduction-multiplier", 0.66);
    DEFAULTS.put("radius", 10);

    for (String path : DEFAULTS.keySet()) {
      if (!config.exists(path) || config.getString(path) == null) {
        config.set(path, DEFAULTS.get(path));

        config.save(FILE);
      }
    }
  }

  public static Config getConfig() {
    return config;
  }

  public static void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      } catch (IOException e) {
        JavaCrystalsOnNukkit.INSTANCE.getLogger().critical("Failed to generate config file");
      }
    }
    config = JavaCrystalsOnNukkit.INSTANCE.getConfig();
    JavaCrystalsOnNukkit.INSTANCE.getLogger().info("Config reloaded!");
  }
}