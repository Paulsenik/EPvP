package de.paulsenik.mc.epvp;

import de.paulsenik.mc.epvp.bukkit.Metrics;
import de.paulsenik.mc.epvp.commands.AdminCommand;
import de.paulsenik.mc.epvp.commands.HowToCommand;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class EPvP extends JavaPlugin {

  public static final Material[] defaultTable = {
      // Metals & Minerals
      Material.LAPIS_LAZULI, Material.LAPIS_BLOCK,
      Material.IRON_NUGGET, Material.RAW_IRON, Material.IRON_INGOT, Material.IRON_BLOCK,
      Material.RAW_IRON_BLOCK,
      Material.RAW_GOLD, Material.GOLD_INGOT, Material.GOLD_BLOCK, Material.RAW_GOLD_BLOCK,
      Material.GOLD_NUGGET,
      Material.DIAMOND, Material.DIAMOND_BLOCK,
      Material.EMERALD, Material.EMERALD_BLOCK,
      Material.NETHERITE_BLOCK, Material.NETHERITE_SCRAP, Material.NETHERITE_INGOT,

      //Ores
      Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
      Material.DEEPSLATE_IRON_ORE, Material.IRON_ORE,
      Material.GOLD_ORE, Material.NETHER_GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
      Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
      Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
      Material.ANCIENT_DEBRIS,

      // Armor
      Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET,
      Material.DIAMOND_BOOTS, Material.DIAMOND_HORSE_ARMOR,
      Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE, Material.IRON_HELMET, Material.IRON_BOOTS,
      Material.IRON_HORSE_ARMOR,
      Material.GOLDEN_LEGGINGS, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_HELMET,
      Material.GOLDEN_BOOTS, Material.GOLDEN_HORSE_ARMOR,
      Material.NETHERITE_LEGGINGS, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_HELMET,
      Material.NETHERITE_BOOTS,
      Material.LEATHER_HORSE_ARMOR,
      Material.TURTLE_HELMET,

      // Combat
      Material.SHIELD, Material.TRIDENT, Material.CROSSBOW, Material.BOW,
      Material.NETHERITE_SWORD, Material.DIAMOND_SWORD, Material.GOLDEN_SWORD, Material.IRON_SWORD,

      // Tools
      Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE,
      Material.NETHERITE_SHOVEL,
      Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL,
      Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE,
      Material.GOLDEN_SHOVEL,
      Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL,

      // Whahabohle
      Material.POTION, Material.LINGERING_POTION, Material.SPLASH_POTION,
      Material.EXPERIENCE_BOTTLE,

      // Valuables
      Material.NETHER_STAR, Material.ENCHANTED_BOOK, Material.ENCHANTING_TABLE,
      Material.END_CRYSTAL, Material.TOTEM_OF_UNDYING, Material.ELYTRA, Material.BEACON,
      Material.DRAGON_EGG, Material.DRAGON_HEAD, Material.WITHER_SKELETON_SKULL,
      Material.PLAYER_HEAD, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE,
      Material.NAME_TAG, Material.FIREWORK_ROCKET, Material.FIREWORK_STAR
  };
  public static EPvP instance;

  public static Logger l;

  public static CopyOnWriteArrayList<Material> dropTable = new CopyOnWriteArrayList<>();
  public static boolean enabled = true;
  public static double killDropRate;

  public static volatile int kills = 0; // kills-chart for bStats.org


  public void onEnable() {
    instance = this;
    l = getLogger();
    load(); //config

    getServer().getPluginManager().registerEvents(new Listeners(), this);
    AdminCommand adminCommand = new AdminCommand();
    getCommand("epvp").setExecutor(adminCommand);
    getCommand("epvp").setTabCompleter(adminCommand);
    getCommand("howto-epvp").setExecutor(new HowToCommand());

    bStats();
  }

  public void onDisable() {
    save();
  }

  public void save() {
    l.info("Saved");
    FileConfiguration config = getConfig();
    config.set("enabled", enabled);
    config.set("rate", killDropRate);

    ArrayList<String> dropTableNames = new ArrayList<>();
    for (Material m : dropTable) {
      dropTableNames.add(m.name());
    }
    config.set("materials", dropTableNames);

    // generates Tutorial-book new because of changed Plugin-Settings
    HowToCommand.generateBook();

    config.options().copyDefaults(true);
    saveConfig();
  }

  public void load() {
    FileConfiguration config = getConfig();

    File configFile = new File(this.getDataFolder(), "config.yml");
    if (!configFile.exists()) {

      // Defaults
      config.addDefault("enabled", true);
      config.addDefault("rate", 0.5f);

      ArrayList<String> temp = new ArrayList<>();
      for (Material m : defaultTable) {
        temp.add(m.name());
      }
      config.addDefault("materials", temp);
      dropTable.addAll(Arrays.asList(defaultTable));

      config.options().copyDefaults(true);
      saveConfig();
      l.info("EPVP: No config found. Created a new one.");
      return;
    }

    enabled = config.getBoolean("enabled");
    try {
      killDropRate = config.getDouble("rate");
    } catch (Exception e) {
      l.warning("EPVP: Could not load EPVP config");
      e.printStackTrace();
    }

    List<String> tempList = config.getStringList("materials");
    dropTable.clear();
    for (String materialName : tempList) {
      if (materialName != null) {
        dropTable.add(Material.valueOf(materialName));
      }
    }
  }

  private void bStats() {
    int pluginId = 20537;
    Metrics metrics = new Metrics(this, pluginId);

    // Enabled/Disabled
    metrics.addCustomChart(
        new Metrics.SimplePie("enabled", () -> enabled ? "enabled" : "disabled"));

    // Rate
    metrics.addCustomChart(new Metrics.DrilldownPie("rates", () -> {
      Map<String, Map<String, Integer>> map = new HashMap<>();
      String dropRate = String.valueOf(Math.round(killDropRate * 100.0d) / 100.0d);
      Map<String, Integer> entry = new HashMap<>();
      entry.put(dropRate, 1);
      map.put(dropRate, entry);
      return map;
    }));

    // Kills
    metrics.addCustomChart(new Metrics.SingleLineChart("kills", () -> {
      int tempKills = kills;
      kills = 0;
      if (tempKills > 0) {
        l.info("[bStats] :: Sent " + tempKills + " kills.");
      }
      return tempKills;
    }));

  }

}
