package space.gorogoro.omikujicapsuletoy;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/*
 * OmikujiCapsuleToyListener
 * @license    GPLv3
 * @copyright  Copyright gorogoro.space 2021
 * @author     kubotan
 * @see        <a href="https://gorogoro.space">gorogoro.space</a>
 */
public class OmikujiCapsuleToyListener implements Listener{
  private OmikujiCapsuleToy capsuletoy;

  /**
   * Constructor of OmikujiCapsuleToyListener.
   */
  public OmikujiCapsuleToyListener(OmikujiCapsuleToy capsuletoy) {
    try{
      this.capsuletoy = capsuletoy;
    } catch (Exception e){
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }
  }

  /**
   * On sign change
   * @param SignChangeEvent event
   */
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onSignChange(SignChangeEvent event) {
    try {
      if(!event.getLine(0).toLowerCase().equals("[omikujicapsuletoy]")) {
        return;
      }

      if(!event.getPlayer().hasPermission("omikujicapsuletoy.create")) {
        return;
      }

      Location signLoc = event.getBlock().getLocation();
      if(capsuletoy.getDatabase().isCapsuleToy(signLoc)) {
        event.setCancelled(true);
        OmikujiCapsuleToyUtility.sendMessage(event.getPlayer(), "It is already registered. To continue, please delete first.");
        return;
      }

      String capsuleToyName = event.getLine(1);
      String capsuleToyDisplayName = event.getLine(2);
      String capsuleToyDetail = event.getLine(3);
      String worldName = signLoc.getWorld().getName();
      Integer x = signLoc.getBlockX();
      Integer y = signLoc.getBlockY();
      Integer z = signLoc.getBlockZ();
      Pattern p = Pattern.compile("^[0-9a-zA-Z_]+$");
      if(!p.matcher(capsuleToyName).find()) {
        event.setCancelled(true);
        OmikujiCapsuleToyUtility.sendMessage(event.getPlayer(), "Please enter the second line of the signboard with one-byte alphanumeric underscore.");
        return;
      }

      Integer capsuleToyId = capsuletoy.getDatabase().getCapsuleToy(capsuleToyName, capsuleToyDisplayName, capsuleToyDetail, worldName, x, y, z);
      if(capsuleToyId == null) {
        event.setCancelled(true);
        throw new Exception("Can not get CapsuleToy. CapsuleToyName=" + capsuleToyName);
      }

      event.setLine(0, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("sign-line1-prefix") + capsuleToyDisplayName));
      event.setLine(1, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("sign-line2-prefix") + capsuleToyDetail));
      event.setLine(2, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("sign-line3")));
      event.setLine(3, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("sign-line4")));

    } catch (Exception e){
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }
  }

  /**
   * On player interact
   * @param PlayerInteractEvent event
   */
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    Block clickedBlock = event.getClickedBlock();
    BlockData data = clickedBlock.getBlockData();
    if (data instanceof Sign || data instanceof WallSign) {
      signProc(event);
    }else if(data.getMaterial().equals(Material.CHEST)) {
      chestProc(event);
    }
  }

  /**
   * Sign process.
   * @param PlayerInteractEvent event
   */
  private void signProc(PlayerInteractEvent event) {
    try {
      Sign sign = (Sign) event.getClickedBlock().getState();
      Player p = event.getPlayer();

      Location signLoc = sign.getLocation();
      if(!capsuletoy.getDatabase().isCapsuleToy(signLoc)) {
        return;
      }
      event.setCancelled(true);

      ItemStack ticket = p.getInventory().getItemInMainHand();
      if( !ticket.getType().equals(Material.PAPER) ) {
        OmikujiCapsuleToyUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("hold-the-ticket")));
        return;
      }

      List<String> lores = ticket.getItemMeta().getLore();
      if( lores.size() != 3) {
        OmikujiCapsuleToyUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("hold-the-ticket")));
        return;
      }


      capsuletoy.getCommand();
      if(!lores.get(2).contains(OmikujiCapsuleToyCommand.EVENT_OMIKUJI_CODE)) {
        OmikujiCapsuleToyUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("not-found-ticket-code")));
        return;
      }

      Chest chest = capsuletoy.getDatabase().getCapsuleToyChest(signLoc);
      if(chest == null) {
        OmikujiCapsuleToyUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("not-found-chest1")));
        OmikujiCapsuleToyUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("not-found-chest2")));
        return;
      }

      p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);

      Inventory iv = chest.getInventory();
      int pick = new Random().nextInt(iv.getSize());
      ItemStack pickItem = iv.getItem(pick);
      if(pickItem == null) {
        OmikujiCapsuleToyUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("not-found-pick")));
        return;
      }

      ItemStack sendItem = pickItem.clone();
      p.getInventory().addItem(sendItem);
      OmikujiCapsuleToyUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', capsuletoy.getConfig().getString("found-pick")));

    } catch (Exception e){
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }
  }

  /**
   * Chest process.
   * @param PlayerInteractEvent event
   */
  private void chestProc(PlayerInteractEvent event) {
    try {
      Player p = event.getPlayer();
      if(!p.getType().equals(EntityType.PLAYER)){
        return;
      }

      if(!event.getClickedBlock().getType().equals(Material.CHEST)) {
        return;
      }

      if(!OmikujiCapsuleToyUtility.isInPunch(p)) {
        return;
      } else {
        event.setCancelled(true);
      }

      String capsuleToyName = OmikujiCapsuleToyUtility.getCapsuleToyNameInPunch(p);
      OmikujiCapsuleToyUtility.removePunch(p, capsuletoy);
      if(capsuleToyName == null) {
        return;
      }

      Location loc = event.getClickedBlock().getLocation();
      if(capsuletoy.getDatabase().updateCapsuleToyChest(capsuleToyName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
        OmikujiCapsuleToyUtility.sendMessage(p, "Updated. capsuletoy_name=" + capsuleToyName);
        return;
      }

    } catch (Exception e){
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }
  }
}
