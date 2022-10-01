package ooo.paulsen.mc.extended_pvp;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Commands implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("EPVP Helpmenu");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("enable")) {
                Extended_PvP.enabled = true;
                Extended_PvP.instance.save();
                sender.sendMessage("EPVP enabled");
                return true;
            }
            if (args[0].equalsIgnoreCase("disable")) {
                Extended_PvP.enabled = false;
                Extended_PvP.instance.save();
                sender.sendMessage("EPVP disabled");
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("drop")) {
                if (args[1].equalsIgnoreCase("list")) {
                    sender.sendMessage("drop list");
                    for (Material m : (List<Material>) Extended_PvP.dropTable.clone()) {
                        sender.sendMessage("- " + m.name());
                    }
                    return true;
                }
                if (args[1].equalsIgnoreCase("rate")) {
                    sender.sendMessage("current Drop-Rate: " + Extended_PvP.killDropRate);
                    return true;
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("drop"))
                if (args[1].equalsIgnoreCase("list")) {
                    if (args[2].equalsIgnoreCase("clear")) {
                        Extended_PvP.dropTable.clear();
                        sender.sendMessage("drop list cleared");
                        return true;
                    }
                } else if (args[1].equalsIgnoreCase("rate")) {
                    try {
                        double value = Double.parseDouble(args[2]);
                        Extended_PvP.killDropRate = Math.min(Math.max(value, 0.0D), 1.0D);
                        Extended_PvP.instance.save();
                        sender.sendMessage("set current Drop-Rate to: " + Extended_PvP.killDropRate);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    return true;
                }
        } else if (args.length == 4 &&
                args[0].equalsIgnoreCase("drop") && args[1].equalsIgnoreCase("list")) {
            if (args[2].equalsIgnoreCase("add")) {
                try {
                    Extended_PvP.dropTable.add(Material.valueOf(args[3]));
                    sender.sendMessage("Added " + args[3]);
                    return true;
                } catch (IllegalArgumentException | NullPointerException e) {
                    return false;
                }
            }
            if (args[2].equalsIgnoreCase("remove")) {
                try {
                    Extended_PvP.dropTable.remove(Material.valueOf(args[3]));
                    sender.sendMessage("Removed " + args[3]);
                    return true;
                } catch (IllegalArgumentException | NullPointerException e) {
                    return false;
                }
            }
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("epvp")) {
            List<String> list = new ArrayList<>();
            if (args.length == 1) {
                list.add("enable");
                list.add("disable");
                list.add("drop");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("drop")) {
                    list.add("list");
                    list.add("rate");
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("drop") && args[1].equalsIgnoreCase("list")) {
                    list.add("add");
                    list.add("remove");
                    list.add("clear");
                }
            } else if (args.length == 4 &&
                    args[0].equalsIgnoreCase("drop") && args[1].equalsIgnoreCase("list")) {
                if (args[2].equalsIgnoreCase("add")) {
                    for (Material m : Material.values())
                        list.add(m.toString());
                } else if (args[2].equalsIgnoreCase("remove")) {
                    for (Material m : Extended_PvP.dropTable)
                        list.add(m.toString());
                }
            }
            return filter(list, args[args.length - 1]);
        }
        return null;
    }

    private static List<String> filter(List<String> l, String arg) {
        ArrayList<String> nL = new ArrayList<>();
        for (String s : l) {
            if (s.contains(arg.toLowerCase()))
                nL.add(s);
        }
        return nL;
    }
}
