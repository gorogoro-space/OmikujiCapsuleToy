package space.gorogoro.omikujicapsluetoy;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/*
 * OmikujiCapslueToyCommand
 * @license    GPLv3
 * @copyright  Copyright gorogoro.space 2021
 * @author     kubotan
 * @see        <a href="https://gorogoro.space">gorogoro.space</a>
 */
public class OmikujiCapslueToyCommand {
  private OmikujiCapslueToy capsluetoy;
  private CommandSender sender;
  private String[] args;
  protected static final String META_CHEST = "omikujicapsluetoy.chest";
  protected static final String FORMAT_TICKET_CODE = "EVENT CODE:%s";
  protected static final String EVENT_OMIKUJI_CODE = "2bCq4f7lUy";

  /**
   * Constructor of CapslueToyCommand.
   * @param OmikujiCapslueToy CapslueToy
   */
  public OmikujiCapslueToyCommand(OmikujiCapslueToy capsluetoy) {
    try{
      this.capsluetoy = capsluetoy;
    } catch (Exception e){
      OmikujiCapslueToyUtility.logStackTrace(e);
    }
  }

  /**
   * Initialize
   * @param CommandSender CommandSender
   * @param String[] Argument
   */
  public void initialize(CommandSender sender, String[] args){
    try{
      this.sender = sender;
      this.args = args;
    } catch (Exception e){
      OmikujiCapslueToyUtility.logStackTrace(e);
    }
  }

  /**
   * Finalize
   */
  public void finalize() {
    try{
      this.sender = null;
      this.args = null;
    } catch (Exception e){
      OmikujiCapslueToyUtility.logStackTrace(e);
    }
  }

  /**
   * Processing of command list.
   * @return boolean true:Success false:Failure
   */
  public boolean list() {
    List<String> glist = capsluetoy.getDatabase().list();
    if(glist.size() <= 0) {
      OmikujiCapslueToyUtility.sendMessage(sender, "Record not found.");
      return true;
    }

    for(String msg: glist) {
      OmikujiCapslueToyUtility.sendMessage(sender, msg);
    }
    return true;
  }

  /**
   * Processing of command modify.
   * @return boolean true:Success false:Failure
   */
  public boolean modify() {
    if(args.length != 2) {
      return false;
    }

    if(!(sender instanceof Player)) {
      return false;
    }

    String capslueToyName = args[1];
    if(capsluetoy.getDatabase().getCapslueToy(capslueToyName) == null) {
      OmikujiCapslueToyUtility.sendMessage(sender, "Record not found. capsluetoy_name=" + capslueToyName);
      return true;
    }
    OmikujiCapslueToyUtility.setPunch((Player)sender, capsluetoy, capslueToyName);
    OmikujiCapslueToyUtility.sendMessage(sender, "Please punching(right click) a chest of CapslueToy. capsluetoy_name=" + capslueToyName);
    return true;
  }

  /**
   * Processing of command delete.
   * @return boolean true:Success false:Failure
   */
  public boolean delete() {
    if(args.length != 2) {
      return false;
    }

    String capslueToyName = args[1];
    if(capsluetoy.getDatabase().deleteCapslueToy(capslueToyName)) {
      OmikujiCapslueToyUtility.sendMessage(sender, "Deleted. capsluetoy_name=" + capslueToyName);
      return true;
    }
    return false;
  }

  /**
   * Processing of command ticket.
   * @return boolean true:Success false:Failure
   */
  public boolean ticket(Player p) {
    if(args.length != 2) {
      return false;
    }

    int emptySlot = p.getInventory().firstEmpty();
    if (emptySlot == -1) {
      // not empty
      return false;
    }

    ItemStack ticket = new ItemStack(Material.PAPER, 1);
    ItemMeta im = ticket.getItemMeta();
    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', capsluetoy.getConfig().getString("ticket-display-name")));
    ArrayList<String> lore = new ArrayList<String>();
    lore.add(ChatColor.translateAlternateColorCodes('&', capsluetoy.getConfig().getString("ticket-lore1")));
    lore.add(ChatColor.translateAlternateColorCodes('&', capsluetoy.getConfig().getString("ticket-lore2")));
    lore.add("スクラッチ:  " + ChatColor.RESET + ChatColor.MAGIC + EVENT_OMIKUJI_CODE + ChatColor.RESET);
    im.setLore(lore);
    ticket.setItemMeta(im);
    p.getInventory().setItem(emptySlot, ticket);

    OmikujiCapslueToyUtility.sendMessage(sender, "Issue a ticket. player_name=" +  p.getDisplayName());
    return true;
  }

  /**
   * Processing of command reload.
   * @return boolean true:Success false:Failure
   */
  public boolean reload() {
    capsluetoy.reloadConfig();
    OmikujiCapslueToyUtility.sendMessage(sender, "reloaded.");
    return true;
  }

  /**
   * Processing of command enable.
   * @return boolean true:Success false:Failure
   */
  public boolean enable() {
    capsluetoy.onEnable();
    OmikujiCapslueToyUtility.sendMessage(sender, "enabled.");
    return true;
  }

  /**
   * Processing of command fgdisable.
   * @return boolean true:Success false:Failure
   */
  public boolean disable() {
    capsluetoy.onDisable();
    OmikujiCapslueToyUtility.sendMessage(sender, "disabled.");
    return true;
  }
}
