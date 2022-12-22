package ooo.paulsen.mc.extended_pvp;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Listeners implements Listener {
    private Logger l = Extended_PvP.l;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TextComponent t = new TextComponent("/howto-epvp");
        t.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://github.com/realPaulsen/EPvP/wiki"));
        t.setColor(ChatColor.LIGHT_PURPLE);
        t.setUnderlined(true);
        t.setBold(true);

        event.getPlayer().spigot().sendMessage(new ComponentBuilder().append(Commands.head+"Read how to use ").append(t).create());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player == null || !Extended_PvP.enabled)
            return;
        EntityDamageEvent damageEvent = player.getLastDamageCause();
        if (damageEvent == null)
            return;
        if (player.getKiller() != null && player.getKiller() != player) { // is killed by other player

            Extended_PvP.kills++;

            filterDrops(event.getDrops(), player);
            event.setKeepInventory(true);
            ItemStack skull = getPlayerSkull(player);
            if (skull != null)
                event.getDrops().add(skull);

        } else {
            event.setKeepInventory(false);
        }
    }

    private static void filterDrops(List<ItemStack> drops, Player victim) {
        if (victim == null || drops == null)
            return;
        ArrayList<ItemStack> newDrops = new ArrayList<>();
        for (int i = 0; i < drops.size(); i++) {
            ItemStack s = drops.get(i);
            if (Extended_PvP.dropTable.contains(s.getType())) {
                int amount = (int) (s.getAmount() * Extended_PvP.killDropRate);
                ItemStack newStack = s.clone();
                newStack.setAmount(amount);
                newDrops.add(newStack);
                removeFromInventory((Inventory) victim.getInventory(), s.getType(), amount);
            }
        }
        drops.clear();
        drops.addAll(newDrops);
    }

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
        meta.setOwner(paramPlayer.getName());
        meta.setDisplayName(paramPlayer.getName());
        skull.setItemMeta((ItemMeta) meta);
        return skull;
    }
}
