package ooo.paulsen.mc.extended_pvp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Extended_PvP extends JavaPlugin {
    public static Extended_PvP instance;

    public static Logger l;

    public static CopyOnWriteArrayList<Material> dropTable = new CopyOnWriteArrayList<>();

    public static double killDropRate = 0.5D;

    public static boolean enabled = true;

    public void onEnable() {
        instance = this;
        l = getLogger();
        dropTable.add(Material.DIAMOND);
        dropTable.add(Material.IRON_INGOT);
        dropTable.add(Material.GOLD_INGOT);
        dropTable.add(Material.EMERALD);
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

        // Defaults
        config.addDefault("enabled", true);
        config.addDefault("rate", 0.5f);

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
