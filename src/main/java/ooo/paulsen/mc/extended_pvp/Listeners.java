package ooo.paulsen.mc.extended_pvp;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Listeners implements Listener {
    public static ArrayList<String> playerkillList = new ArrayList<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(Extended_PvP.enabled) {
            TextComponent t = new TextComponent("/howto-epvp");
            t.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/realPaulsen/EPvP"));
            t.setColor(ChatColor.LIGHT_PURPLE);
            t.setUnderlined(true);
            t.setBold(true);

            event.getPlayer().spigot().sendMessage(new ComponentBuilder().append(Commands.head + "Read how to use ").append(t).create());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!Extended_PvP.enabled)
            return;

        // OP-Player issued manual /playerkill
        if(playerkillList.contains(player.getDisplayName())){

            // filtering items
            filterDrops(event.getDrops(), player);
            event.setKeepInventory(true);

            // adding Skull
            ItemStack skull = getPlayerSkull(player);
            if (skull != null)
                event.getDrops().add(skull);

            playerkillList.clear();

        } else { // Player was killed normal

            EntityDamageEvent damageEvent = player.getLastDamageCause();
            if (damageEvent == null)
                return;

            if (player.getKiller() != null && player.getKiller() != player) { // is killed by other player

                Extended_PvP.kills++;

                // filtering items
                filterDrops(event.getDrops(), player);
                event.setKeepInventory(true);

                // adding Skull
                ItemStack skull = getPlayerSkull(player);
                if (skull != null)
                    event.getDrops().add(skull);

            }
        }
    }

    private static void filterDrops(List<ItemStack> drops, Player victim) {
        if (victim == null || drops == null)
            return;

        ArrayList<ItemStack> newDrops = new ArrayList<>();
        for (ItemStack s : drops) {
            // items that are in droptable
            if (Extended_PvP.dropTable.contains(s.getType())) {
                int amount = (int) (s.getAmount() * Extended_PvP.killDropRate);
                ItemStack newStack = s.clone();
                newStack.setAmount(amount);
                newDrops.add(newStack);
                removeFromInventory(victim.getInventory(), s.getType(), amount);
            } else { // other items should be dropped normal
                ItemStack newStack = s.clone();
                newDrops.add(newStack);
                removeFromInventory(victim.getInventory(), s.getType(), s.getAmount());
            }
        }
        drops.clear();
        drops.addAll(newDrops);
    }

    /**
     * Removes a specific amount of items of a given material out of the inventory
     * @param inv
     * @param m
     * @param amount
     */
    private static void removeFromInventory(Inventory inv, Material m, int amount) {
        for (ItemStack invItem : inv.getContents()) {
            if (invItem != null &&
                    invItem.getType().equals(m)) {
                int preAmount = invItem.getAmount();
                int newAmount = Math.max(0, preAmount - amount);
                amount = Math.max(0, amount - preAmount);
                invItem.setAmount(newAmount);
                if (amount == 0)
                    break;
            }
        }
    }

    private static ItemStack getPlayerSkull(Player paramPlayer) {
        if (paramPlayer == null)
            return null;

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(paramPlayer);
        meta.setDisplayName(paramPlayer.getName());
        skull.setItemMeta(meta);
        return skull;
    }
}
