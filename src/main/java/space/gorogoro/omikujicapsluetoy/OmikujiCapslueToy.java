package space.gorogoro.omikujicapsluetoy;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * OmikujiCapslueToy
 * @license    GPLv3
 * @copyright  Copyright gorogoro.space 2021
 * @author     kubotan
 * @see        <a href="https://gorogoro.space">gorogoro.space</a>
 */
public class OmikujiCapslueToy extends JavaPlugin{
  private OmikujiCapslueToyDatabase database;
  private OmikujiCapslueToyCommand command;
  private OmikujiCapslueToyListener listener;

  /**
   * Get OmikujiCapslueToyDatabase instance.
   */
  public OmikujiCapslueToyDatabase getDatabase() {
    return database;
  }

  /**
   * Get OmikujiCapslueToyCommand instance.
   */
  public OmikujiCapslueToyCommand getCommand() {
    return command;
  }

  /**
   * Get OmikujiCapslueToyListener instance.
   */
  public OmikujiCapslueToyListener getListener() {
    return listener;
  }

  /**
   * JavaPlugin method onEnable.
   */
  @Override
  public void onEnable(){
    try{
      getLogger().log(Level.INFO, "The Plugin Has Been Enabled!");

      // If there is no setting file, it is created
      if(!getDataFolder().exists()){
        getDataFolder().mkdir();
      }

      File configFile = new File(getDataFolder(), "config.yml");
      if(!configFile.exists()){
        saveDefaultConfig();
      }

      // Initialize the database.
      database = new OmikujiCapslueToyDatabase(this);
      database.initialize();

      // Register event listener.
      PluginManager pm = getServer().getPluginManager();
      HandlerList.unregisterAll(this);    // clean up
      listener = new OmikujiCapslueToyListener(this);
      pm.registerEvents(listener, this);

      // Instance prepared of CapslueToyCommand.
      command = new OmikujiCapslueToyCommand(this);

    } catch (Exception e){
      OmikujiCapslueToyUtility.logStackTrace(e);
    }
  }

  /**
   * JavaPlugin method onCommand.
   *
   * @return boolean true:Success false:Display the usage dialog set in plugin.yml
   */
  public boolean onCommand( CommandSender sender, Command commandInfo, String label, String[] args) {
    boolean hideUseageFlag = true;  // true:Success false:Display the usage dialog set in plugin.yml
    try{
      if(!commandInfo.getName().equals("omikujicapsluetoy")) {
        return hideUseageFlag;
      }

      if(args.length <= 0) {
        return hideUseageFlag;
      }
      String subCommand = args[0];

      command.initialize(sender, args);
      switch(subCommand) {
        case "list":
          if(sender.hasPermission("omikujicapsluetoy.list")) {
            hideUseageFlag = command.list();
          }
          break;

        case "modify":
          if(sender.hasPermission("omikujicapsluetoy.modify")) {
            hideUseageFlag = command.modify();
          }
          break;

        case "delete":
          if(sender.hasPermission("omikujicapsluetoy.delete")) {
            hideUseageFlag = command.delete();
          }
          break;

        case "ticket":
          if((sender instanceof BlockCommandSender) || (sender instanceof ConsoleCommandSender) || sender.isOp()) {
            for(Player p:OmikujiCapslueToyUtility.getTarget(this, args[1], sender)) {  // @a @p @s @r or playername
              command.ticket(p);
            }
            hideUseageFlag = true;
          }
          break;

        case "enable":
          if(sender.isOp()) {
            hideUseageFlag = command.enable();
          }
          break;

        case "reload":
          if(sender.isOp()) {
            hideUseageFlag = command.reload();
          }
          break;

        case "disable":
          if(sender.isOp()) {
            hideUseageFlag = command.disable();
          }
          break;

        default:
          hideUseageFlag = false;
      }
    }catch(Exception e){
      OmikujiCapslueToyUtility.logStackTrace(e);
    }finally{
      command.finalize();
    }
    return hideUseageFlag;
  }

  /**
   * JavaPlugin method onDisable.
   */
  @Override
  public void onDisable(){
    try{
      database.finalize();
      command.finalize();

      // Unregister all event listener.
      HandlerList.unregisterAll(this);

      getLogger().log(Level.INFO, "The Plugin Has Been Disabled!");
    } catch (Exception e){
      OmikujiCapslueToyUtility.logStackTrace(e);
    }
  }
}
