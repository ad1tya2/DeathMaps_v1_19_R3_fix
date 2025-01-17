package net.pl3x.bukkit.deathmaps;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DeathMaps extends JavaPlugin {
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
            public void onDeath(PlayerDeathEvent event) {
                if (event.getDrops().isEmpty()) return;
                Player player = event.getEntity();
                Location loc = player.getLocation();
                BlockPos pos = new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                ItemStack nmsMap = MapItem.create(((CraftWorld) loc.getWorld()).getHandle(), pos.getX(), pos.getZ(), MapView.Scale.CLOSEST.getValue(), true, true);
                MapItem.renderBiomePreviewMap(((CraftWorld) loc.getWorld()).getHandle(), nmsMap);
                MapItemSavedData.addTargetDecoration(nmsMap, pos, "Death", MapDecoration.Type.RED_X);
                org.bukkit.inventory.ItemStack bukkitMap = CraftItemStack.asCraftMirror(nmsMap);
                ItemMeta mapMeta = bukkitMap.getItemMeta();
                mapMeta.setDisplayName(player.getDisplayName() + ChatColor.RED + "'s Death Map");
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.YELLOW + "World: " + ChatColor.WHITE + ChatColor.UNDERLINE + player.getWorld().getName());
                lore.add(ChatColor.DARK_AQUA + "X: " + ChatColor.WHITE + loc.getBlockX() + ChatColor.DARK_AQUA + " Y: " + ChatColor.WHITE + loc.getBlockY() + ChatColor.DARK_AQUA + " Z: " + ChatColor.WHITE + loc.getBlockZ());
                lore.add(ChatColor.GOLD + new SimpleDateFormat("M/d/yy h:mm aa").format(new Date()));
                mapMeta.setLore(lore);
                bukkitMap.setItemMeta(mapMeta);
                new BukkitRunnable() {
                    public void run() {
                        if (player.getInventory().getItemInOffHand().getType() == Material.AIR) {
                            player.getInventory().setItemInOffHand(bukkitMap);
                        } else {
                            player.getInventory().addItem(bukkitMap);
                        }
                    }
                }.runTaskLater(DeathMaps.this, 1L);
            }
        }, this);
    }
}
