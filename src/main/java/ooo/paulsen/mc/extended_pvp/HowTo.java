package ooo.paulsen.mc.extended_pvp;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HowTo implements CommandExecutor {

    private static ItemStack howToBook;

    public HowTo() {
        howToBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) howToBook.getItemMeta();

        bookMeta.setAuthor("Blank");
        bookMeta.setTitle("Blank");
        bookMeta.setLore(Arrays.asList("This is a HowTo-Book", "of the useful", "Extended PvP plugin!"));

        TextComponent t = new TextComponent("/howto-epvp");
        t.setColor(ChatColor.LIGHT_PURPLE);
        t.setUnderlined(true);
        t.setBold(true);


        bookMeta.setPages(ChatColor.BLUE + "Dei mam stinkt!", ChatColor.DARK_BLUE + "Mmmhhh, LOL!", t.toLegacyText());

        howToBook.setItemMeta(bookMeta);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            openBook((Player) commandSender);
            return true;
        }
        commandSender.sendMessage("Only Players can open the HowTo-Book!");
        return true;
    }

    public void openBook(final Player player) {
        player.openBook(howToBook);
    }
}
