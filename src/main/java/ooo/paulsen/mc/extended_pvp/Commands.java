package ooo.paulsen.mc.extended_pvp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor, TabCompleter {

    private static String head = ChatColor.GOLD + "[" + ChatColor.UNDERLINE + ChatColor.LIGHT_PURPLE + "EPvP" + ChatColor.RESET + ChatColor.GOLD + "]: ";
    private static final ChatColor usedColors[] = {ChatColor.GOLD, ChatColor.UNDERLINE, ChatColor.LIGHT_PURPLE, ChatColor.RESET, ChatColor.RED, ChatColor.GREEN};

    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if (args.length == 0) {
            String m[] = {ChatColor.UNDERLINE + (Extended_PvP.enabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
                    " /epvp " + ChatColor.GRAY + ": Helpmenu",
                    ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "enable",
                    ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "disable",
                    ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "rate " + ChatColor.GRAY + ": shows amount of an Itemtype that is dropped",
                    ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "rate [0.0-1.0] " + ChatColor.GRAY + ": sets the rate",
                    ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "table " + ChatColor.GRAY + ": lists the valuable-table",
                    ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "table add [MATERIAL] " + ChatColor.GRAY + ": adds a material to the table",
                    ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "table remove [MATERIAL] " + ChatColor.GRAY + ": removes a material from the table",
                    ChatColor.GRAY + " /epvp " + ChatColor.WHITE + "table clear " + ChatColor.GRAY + ": clears the table"
            };
            send(s, m);
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("enable")) {
                Extended_PvP.enabled = true;
                Extended_PvP.instance.save();
                send(s, "" + ChatColor.GREEN + ChatColor.UNDERLINE + "ENABLED");
                return true;
            } else if (args[0].equalsIgnoreCase("disable")) {
                Extended_PvP.enabled = false;
                Extended_PvP.instance.save();
                send(s, "" + ChatColor.RED + ChatColor.UNDERLINE + "DISABLED");
                return true;
            } else if (args[0].equalsIgnoreCase("rate")) {
                send(s, ChatColor.WHITE + "Drop-Rate: " + ChatColor.GOLD + Extended_PvP.killDropRate);
                return true;
            } else if (args[0].equalsIgnoreCase("table")) {
                send(s, "Table:");
                if (Extended_PvP.dropTable.isEmpty()) {
                    send(s, false, ChatColor.GRAY + " <empty>");
                } else {
                    for (Material m : (List<Material>) Extended_PvP.dropTable.clone()) {
                        send(s, false, ChatColor.GOLD + " " + m.name());
                    }
                }
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("table")) {
                if (args[1].equalsIgnoreCase("clear")) {
                    Extended_PvP.dropTable.clear();
                    Extended_PvP.instance.save();
                    send(s, ChatColor.GREEN + "Drop-Table cleared!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("rate")) {
                try {
                    double value = Double.parseDouble(args[1]);
                    Extended_PvP.killDropRate = Math.min(Math.max(value, 0.0D), 1.0D);
                    Extended_PvP.instance.save();
                    send(s, ChatColor.WHITE + "Drop-Rate: " + ChatColor.GOLD + Extended_PvP.killDropRate);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        } else if (args.length == 3 &&
                args[0].equalsIgnoreCase("table")) {
            if (args[1].equalsIgnoreCase("add")) {
                try {
                    Material m = Material.valueOf(args[2]);

                    if (m == null)
                        return false;
                    if (Extended_PvP.dropTable.contains(m)) {
                        send(s, ChatColor.RED + "Material already contains " + m.name() + "!");
                    } else {
                        Extended_PvP.dropTable.add(m);
                        Extended_PvP.instance.save();
                        send(s, ChatColor.GREEN + "Added " + ChatColor.GOLD + ChatColor.UNDERLINE + args[2] + ChatColor.RESET + ChatColor.GREEN + " to table.");
                    }

                    return true;
                } catch (IllegalArgumentException | NullPointerException e) {
                    return false;
                }
            }
            if (args[1].equalsIgnoreCase("remove")) {
                try {
                    if (Extended_PvP.dropTable.remove(Material.valueOf(args[2]))) {
                        Extended_PvP.instance.save();
                        send(s, ChatColor.GREEN + "Removed " + ChatColor.GOLD + ChatColor.UNDERLINE + args[2] + ChatColor.RESET + ChatColor.GREEN + " from table.");
                    } else {
                        send(s, ChatColor.RED + "Material not found!");
                    }
                    return true;
                } catch (IllegalArgumentException | NullPointerException e) {
                    return false;
                }
            }
        }
        return false;
    }

    public void send(CommandSender s, boolean doheader, String... message) {
        if (s instanceof Player) {

            s.sendMessage((doheader ? head : "") + message[0]);
            for (int i = 1; i < message.length; i++)
                s.sendMessage(message[i]);

        } else {

            s.sendMessage(ChatColor.stripColor((doheader ? head : "") + message[0]));
            for (int i = 1; i < message.length; i++)
                s.sendMessage(ChatColor.stripColor(message[i]));

        }
    }

    public void send(CommandSender s, String... message) {
        send(s, true, message);
    }

    // Tab-Complete commands
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
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
                    list.add(String.valueOf(Extended_PvP.killDropRate));
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("table")) {
                    if (args[1].equalsIgnoreCase("add")) {
                        for (Material m : Material.values())
                            list.add(m.toString());
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        for (Material m : Extended_PvP.dropTable)
                            list.add(m.toString());
                    }

                }
            }
            return filter(list, args[args.length - 1]);
        }
        return null;
    }

    // returns a list of possible commands according to the already (partly) typed command
    private static List<String> filter(List<String> l, String arg) {
        ArrayList<String> nL = new ArrayList<>();
        for (String s : l) {
            if (s.toLowerCase().contains(arg.toLowerCase()))
                nL.add(s);
        }
        return nL;
    }
}
