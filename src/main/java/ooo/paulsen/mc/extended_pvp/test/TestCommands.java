package ooo.paulsen.mc.extended_pvp.test;

import ooo.paulsen.mc.extended_pvp.Commands;
import ooo.paulsen.mc.extended_pvp.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args == null || args.length == 0 || args[0] == null){
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target != null && Bukkit.getOnlinePlayers().contains(target)) {
            Listeners.playerkillList.add(sender.getName());
            target.setHealth(0);
            return true;
        } else {
            sender.sendMessage(Commands.head+ ChatColor.RED+"Invalid player!");
            return false;
        }
    }
}
