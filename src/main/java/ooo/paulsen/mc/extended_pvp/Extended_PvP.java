package ooo.paulsen.mc.extended_pvp;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Extended_PvP extends JavaPlugin {

    public static final Material[] defaultTable = {
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT, Material.IRON_INGOT, Material.DIAMOND_BLOCK,
            Material.EMERALD_BLOCK, Material.GOLD_BLOCK, Material.IRON_BLOCK
    };
    public static Extended_PvP instance;

    public static Logger l;

    public static CopyOnWriteArrayList<Material> dropTable = new CopyOnWriteArrayList<>();

    public static double killDropRate;

    public static boolean enabled = true;

    public void onEnable() {
        instance = this;
        l = getLogger();
        load();


        getServer().getPluginManager().registerEvents(new Listeners(), (Plugin) this);
        getCommand("epvp").setExecutor(new Commands());
        getCommand("epvp").setTabCompleter(new Commands());
    }

    public void onDisable() {
        save();
    }

    public void save() {
        l.info("Saved");
        FileConfiguration config = getConfig();
        config.set("enabled", Boolean.valueOf(enabled));
        config.set("rate", Double.valueOf(killDropRate));

        ArrayList<String> dropTableNames = new ArrayList<>();
        for (Material m : dropTable) {
            dropTableNames.add(m.name());
        }
        config.set("materials", dropTableNames);

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
            for (Material m : defaultTable)
                temp.add(m.name());
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
        if (tempList != null) {
            dropTable.clear();
            for (String materialName : tempList) {
                if (materialName != null)
                    dropTable.add(Material.valueOf(materialName));
            }
        }
    }

}
