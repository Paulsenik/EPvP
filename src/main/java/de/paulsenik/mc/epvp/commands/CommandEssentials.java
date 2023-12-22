package de.paulsenik.mc.epvp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEssentials {

  public static final String head =
      ChatColor.GOLD + "[" + ChatColor.UNDERLINE + ChatColor.DARK_PURPLE + "EPvP" + ChatColor.RESET
          + ChatColor.GOLD + "]: ";

  // returns a list of possible commands according to the already (partly) typed command
  public static List<String> filter(List<String> l, String arg, boolean useRegex) {
    ArrayList<String> nL = new ArrayList<>();

    if (useRegex) {
      try {
        Pattern p = Pattern.compile('^' + arg);
        for (String s : l) {
          Matcher m = p.matcher(s);
          if (m.find()) {
            nL.add(s);
          }
        }
        return nL;
      } catch (PatternSyntaxException ignore) {
      }
    }

    // Normal
    for (String s : l) {
      if (s.toLowerCase().contains(arg.toLowerCase())) {
        nL.add(s);
      }
    }

    return nL;
  }


  public static void send(CommandSender s, boolean doHeader, String... message) {
    if (s instanceof Player) {

      s.sendMessage((doHeader ? head : "") + message[0]);
      for (int i = 1; i < message.length; i++) {
        s.sendMessage(message[i]);
      }

    } else {

      s.sendMessage(ChatColor.stripColor((doHeader ? head : "") + message[0]));
      for (int i = 1; i < message.length; i++) {
        s.sendMessage(ChatColor.stripColor(message[i]));
      }

    }
  }

  public static void send(CommandSender s, String... message) {
    send(s, true, message);
  }

  private static final char[] regexChars = {'.', '\\', '[', ']', '^', '$', '*', '(', ')', '?', ':',
      '=',
      '!', '+', '{', '}', '|'};

  public static boolean isRegex(String s) {
    for (char c : regexChars) {
      if (s.contains(String.valueOf(c))) {
        return true;
      }
    }
    return false;
  }
}
