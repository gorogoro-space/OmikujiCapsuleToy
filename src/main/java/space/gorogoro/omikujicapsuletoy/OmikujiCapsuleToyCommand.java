package space.gorogoro.omikujicapsuletoy;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/*
 * OmikujiCapsuleToyCommand
 * @license    GPLv3
 * @copyright  Copyright gorogoro.space 2021
 * @author     kubotan
 * @see        <a href="https://gorogoro.space">gorogoro.space</a>
 */
public class OmikujiCapsuleToyCommand {
  private OmikujiCapsuleToy capsuletoy;
  private CommandSender sender;
  private String[] args;
  protected static final String FORMAT_TICKET_CODE = "EVENT CODE:%s";
  protected static final String EVENT_OMIKUJI_CODE = "2bCq4f7lUy";

  /**
   * Constructor of CapsuleToyCommand.
   * @param OmikujiCapsuleToy CapsuleToy
   */
  public OmikujiCapsuleToyCommand(OmikujiCapsuleToy capsuletoy) {
    try{
      this.capsuletoy = capsuletoy;
    } catch (Exception e){
      OmikujiCapsuleToyUtility.logStackTrace(e);
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
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }
  }

  /**
   * Clear
   */
  // Object#finalize(削除予定)と同じ名前を避けるため clear にしている
  public void clear() {
    try{
      this.sender = null;
      this.args = null;
    } catch (Exception e){
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }
  }

  /**
   * Processing of command list.
   * @return boolean true:Success false:Failure
   */
  public boolean list() {
    List<String> glist = capsuletoy.getDatabase().list();
    if(glist.size() <= 0) {
      OmikujiCapsuleToyUtility.sendMessage(sender, "Record not found.");
      return true;
    }

    for(String msg: glist) {
      OmikujiCapsuleToyUtility.sendMessage(sender, msg);
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

    String capsuleToyName = args[1];
    if(capsuletoy.getDatabase().getCapsuleToy(capsuleToyName) == null) {
      OmikujiCapsuleToyUtility.sendMessage(sender, "Record not found. capsuletoy_name=" + capsuleToyName);
      return true;
    }
    OmikujiCapsuleToyUtility.setPunch((Player)sender, capsuletoy, capsuleToyName);
    OmikujiCapsuleToyUtility.sendMessage(sender, "Please punching(right click) a chest of CapsuleToy. capsuletoy_name=" + capsuleToyName);
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

    String capsuleToyName = args[1];
    if(capsuletoy.getDatabase().deleteCapsuleToy(capsuleToyName)) {
      OmikujiCapsuleToyUtility.sendMessage(sender, "Deleted. capsuletoy_name=" + capsuleToyName);
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
    im.displayName(OmikujiCapsuleToyUtility.toItemComponent(OmikujiCapsuleToyUtility.configText(capsuletoy, "ticket-display-name")));
    List<Component> lore = new ArrayList<Component>();
    lore.add(OmikujiCapsuleToyUtility.toItemComponent(OmikujiCapsuleToyUtility.configText(capsuletoy, "ticket-lore1")));
    lore.add(OmikujiCapsuleToyUtility.toItemComponent(OmikujiCapsuleToyUtility.configText(capsuletoy, "ticket-lore2")));
    // §r は RESET、§k は MAGIC
    char c = OmikujiCapsuleToyUtility.COLOR_CHAR;
    lore.add(OmikujiCapsuleToyUtility.toItemComponent("スクラッチ:  " + c + "r" + c + "k" + EVENT_OMIKUJI_CODE + c + "r"));
    im.lore(lore);
    ticket.setItemMeta(im);
    p.getInventory().setItem(emptySlot, ticket);

    OmikujiCapsuleToyUtility.sendMessage(sender, "Issue a ticket. player_name=" +  LegacyComponentSerializer.legacySection().serialize(p.displayName()));
    return true;
  }

  /**
   * Processing of command reload.
   * @return boolean true:Success false:Failure
   */
  public boolean reload() {
    capsuletoy.reloadConfig();
    OmikujiCapsuleToyUtility.sendMessage(sender, "reloaded.");
    return true;
  }

  /**
   * Processing of command enable.
   * @return boolean true:Success false:Failure
   */
  public boolean enable() {
    capsuletoy.onEnable();
    OmikujiCapsuleToyUtility.sendMessage(sender, "enabled.");
    return true;
  }

  /**
   * Processing of command disable.
   * @return boolean true:Success false:Failure
   */
  public boolean disable() {
    capsuletoy.onDisable();
    OmikujiCapsuleToyUtility.sendMessage(sender, "disabled.");
    return true;
  }
}
