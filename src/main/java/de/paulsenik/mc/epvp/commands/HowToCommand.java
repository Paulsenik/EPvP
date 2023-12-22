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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;


public class HowToCommand implements CommandExecutor {

  private static ItemStack howToBook;

  public HowToCommand() {
    generateBook();
  }

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s,
      String[] strings) {
    if (commandSender instanceof Player) {
      openBook((Player) commandSender);
      return true;
    }
    commandSender.sendMessage("Only Players can open the HowTo-Book!");
    return true;
  }

  public static void generateBook() {
    howToBook = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta bookMeta = (BookMeta) howToBook.getItemMeta();

    if (bookMeta != null) {
      bookMeta.setAuthor("Paulsen__");
      bookMeta.setTitle("How2 EPvP");
      bookMeta.setLore(Arrays.asList("This is a HowTo-Book", "of the usefulness of the",
          "EPvP plugin!"));
      bookMeta.setPages(getPages());

      howToBook.setItemMeta(bookMeta);
    }
  }

  private static ArrayList<String> getPages() {
    ArrayList<String> pages = new ArrayList<>();

    // MainPage
    pages.add(
        "          " + (EPvP.enabled ?
            ChatColor.GREEN : ChatColor.RED) + ChatColor.UNDERLINE + "<EPvP>\n"
            + ChatColor.RESET + ChatColor.GRAY + "      by Paulsenik\n" + ChatColor.RESET +
            "\n" +
            ChatColor.BLUE + ChatColor.BOLD + ChatColor.UNDERLINE + "Features:\n\n"
            + ChatColor.RESET +
            ChatColor.BLACK + "- " + ChatColor.GOLD + "Collect heads" + ChatColor.RESET
            + " of other players by killing them.\n\n" +
            "- " + ChatColor.GOLD + "Keep valuables" + ChatColor.RESET
            + " after losing a player-duel (e.g. armor, weapons)");

    pages.add("" + ChatColor.BLUE + ChatColor.BOLD + ChatColor.UNDERLINE + "Settings:\n\n" +
        ChatColor.RESET + "- You keep " + ChatColor.GOLD + ChatColor.UNDERLINE + (100d
        - EPvP.killDropRate * 100) + "%" + ChatColor.RESET + " of the "
        + ChatColor.UNDERLINE + "valuables.\n\n" +
        ChatColor.RESET + "- Valuables saved: " + ChatColor.GOLD
        + ChatColor.UNDERLINE + EPvP.dropTable.size() + "\n\n" +
        ChatColor.RESET + "- All valuables listed on the next " + ChatColor.UNDERLINE + "pages"
        + ChatColor.RESET + "...");

    pages.addAll(printTable());

    return pages;
  }

  private static List<String> printTable() {
    ArrayList<String> s = new ArrayList<>();

    StringBuilder b = new StringBuilder();
    for (int i = 0; i < EPvP.dropTable.size(); i++) {
      if (i % 14 == 0 && i != 0) {
        s.add(b.toString());
        b.delete(0, b.length());
      }
      b.append(ChatColor.GOLD);
      b.append(EPvP.dropTable.get(i));
      b.append("\n");
    }
    s.add(b.toString());

    return s;
  }

  private static void openBook(final Player player) {
    player.openBook(howToBook);
  }
}
