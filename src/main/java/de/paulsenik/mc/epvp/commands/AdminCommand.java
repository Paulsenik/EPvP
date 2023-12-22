package de.paulsenik.mc.epvp.commands;

import de.paulsenik.mc.epvp.EPvP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AdminCommand extends CommandEssentials implements CommandExecutor, TabCompleter {

  public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
    if (args.length == 0) {
      return command_0(s, command, label, args);
    }
    if (args.length == 1) {
      return command_1(s, command, label, args);
    } else if (args.length == 2) {
      return command_2(s, command, label, args);
    } else if (args.length == 3) {
      return args[0].equalsIgnoreCase("table") && command_table(s, command, label, args);
    }
    return false;
  }

  private boolean command_0(CommandSender s, Command command, String label, String[] args) {
    String[] m = {ChatColor.UNDERLINE + (EPvP.enabled ? ChatColor.GREEN + "ENABLED"
        : ChatColor.RED + "DISABLED"),
        ChatColor.WHITE + " /howto-epvp " + ChatColor.GRAY + ": HowTo-Book",
        ChatColor.WHITE + " /epvp " + ChatColor.GRAY + ": Helpmenu",
        ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "enable",
        ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "disable",
        ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "rate " + ChatColor.GRAY
            + ": shows amount of an Itemtype that is dropped",
        ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "rate [0.0-1.0] " + ChatColor.GRAY
            + ": sets the rate",
        ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "table " + ChatColor.GRAY
            + ": lists the valuable-table",
        ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "table add [MATERIAL] " + ChatColor.GRAY
            + ": adds a material to the table",
        ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "table remove [MATERIAL] " + ChatColor.GRAY
            + ": removes a material from the table",
        ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "table clear " + ChatColor.GRAY
            + ": clears the table"
    };
    send(s, m);
    return true;
  }

  private boolean command_1(CommandSender s, Command command, String label, String[] args) {
    if (args[0].equalsIgnoreCase("enable")) {
      EPvP.enabled = true;
      EPvP.instance.save();
      send(s, "" + ChatColor.GREEN + ChatColor.UNDERLINE + "ENABLED");
      return true;
    } else if (args[0].equalsIgnoreCase("disable")) {
      EPvP.enabled = false;
      EPvP.instance.save();
      send(s, "" + ChatColor.RED + ChatColor.UNDERLINE + "DISABLED");
      return true;
    } else if (args[0].equalsIgnoreCase("rate")) {
      send(s, ChatColor.WHITE + "Drop-Rate: " + ChatColor.GOLD + EPvP.killDropRate);
      return true;
    } else if (args[0].equalsIgnoreCase("table")) {
      send(s, "Table:");
      if (EPvP.dropTable.isEmpty()) {
        send(s, false, ChatColor.GRAY + " <empty>");
      } else {
        for (Material m : (List<Material>) EPvP.dropTable.clone()) {
          send(s, false, ChatColor.GOLD + " " + m.name());
        }
      }
      return true;
    }
    return false;
  }

  private boolean command_2(CommandSender s, Command command, String label, String[] args) {
    if (args[0].equalsIgnoreCase("table")) {
      if (args[1].equalsIgnoreCase("clear")) {
        EPvP.dropTable.clear();
        EPvP.instance.save();
        send(s, ChatColor.GREEN + "Drop-Table cleared!");
        return true;
      }
    } else if (args[0].equalsIgnoreCase("rate")) {
      try {
        double value = Double.parseDouble(args[1]);
        EPvP.killDropRate = Math.min(Math.max(value, 0.0D), 1.0D);
        EPvP.instance.save();
        send(s, ChatColor.WHITE + "Drop-Rate: " + ChatColor.GOLD + EPvP.killDropRate);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
    return false;
  }

  private boolean command_table(CommandSender s, Command command, String label, String[] args) {
    List<String> materialNames = new ArrayList<>();
    Arrays.stream(Material.values()).forEach(m -> materialNames.add(m.toString()));

    List<String> materials = new ArrayList<>();

    // If no Regex -> Try adding the argument raw as a Material.
    if (isRegex(args[2])) {
      materials = filter(materialNames, args[2].toUpperCase(), true);
    } else {
      materials.add(args[2].toUpperCase());
    }

    if (materials.isEmpty()) {
      send(s, ChatColor.RED + "No Material found ");
      return false;
    }

    try {
      for (String materialName : materials) {
        Material m = Material.valueOf(materialName);
        if (args[1].equalsIgnoreCase("add")) {
          add(s, m);
        } else if (args[1].equalsIgnoreCase("remove")) {
          remove(s, m);
        }
      }
    } catch (IllegalArgumentException | NullPointerException ignore) { // wrong MaterialType
    }

    EPvP.instance.save();
    return true;
  }

  private static void add(CommandSender s, Material m) {
    if (EPvP.dropTable.contains(m)) {
      send(s, ChatColor.RED + "Table already contains " + m.name() + "!");
    } else {
      EPvP.dropTable.add(m);
      send(s,
          ChatColor.GREEN + "Added " + ChatColor.GOLD + ChatColor.UNDERLINE + m.name()
              + ChatColor.RESET + ChatColor.GREEN + " to table.");
    }
  }

  private static void remove(CommandSender s, Material m) {
    if (EPvP.dropTable.remove(m)) {
      EPvP.instance.save();
      send(s, ChatColor.GREEN + "Removed " + ChatColor.GOLD + ChatColor.UNDERLINE + m.name()
          + ChatColor.RESET + ChatColor.GREEN + " from table.");
    } else {
      send(s, ChatColor.RED + "Material \"" + m.name() + "\" not found!");
    }
  }

  // Tab-Complete commands
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label,
      String[] args) {
    if (cmd.getName().equalsIgnoreCase("epvp")) {
      List<String> list = new ArrayList<>();
      if (args.length == 1) {
        list.add("enable");
        list.add("disable");
        list.add("rate");
        list.add("table");
      } else if (args.length == 2) {
        if (args[0].equalsIgnoreCase("table")) {
          list.add("add");
          list.add("remove");
          list.add("clear");
        } else if (args[0].equalsIgnoreCase("rate")) {
          list.add(String.valueOf(EPvP.killDropRate));
        }
      } else if (args.length == 3) {
        if (args[0].equalsIgnoreCase("table")) {
          args[2] = args[2].toUpperCase();
          if (args[1].equalsIgnoreCase("add")) {
            for (Object m : Arrays.stream(Material.values())
                .filter(m -> !EPvP.dropTable.contains(m)).toArray()) {
              list.add(m.toString());
            }
          } else if (args[1].equalsIgnoreCase("remove")) {
            for (Material m : EPvP.dropTable) {
              list.add(m.toString());
            }
          }

        }
      }

      // length=3 is referring to the "epvp table add" and "epvp table remove" command.
      return filter(list, args[args.length - 1], args.length == 3);
    }
    return null;
  }
}
