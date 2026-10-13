package space.gorogoro.omikujicapsuletoy;

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
 * OmikujiCapsuleToy
 * @license    GPLv3
 * @copyright  Copyright gorogoro.space 2021
 * @author     kubotan
 * @see        <a href="https://gorogoro.space">gorogoro.space</a>
 */
public class OmikujiCapsuleToy extends JavaPlugin{
  private OmikujiCapsuleToyDatabase database;
  private OmikujiCapsuleToyCommand command;
  private OmikujiCapsuleToyListener listener;

  /**
   * Get OmikujiCapsuleToyDatabase instance.
   */
  public OmikujiCapsuleToyDatabase getDatabase() {
    return database;
  }

  /**
   * Get OmikujiCapsuleToyCommand instance.
   */
  public OmikujiCapsuleToyCommand getCommand() {
    return command;
  }

  /**
   * Get OmikujiCapsuleToyListener instance.
   */
  public OmikujiCapsuleToyListener getListener() {
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
      database = new OmikujiCapsuleToyDatabase(this);
      database.initialize();

      // Register event listener.
      PluginManager pm = getServer().getPluginManager();
      HandlerList.unregisterAll(this);    // clean up
      listener = new OmikujiCapsuleToyListener(this);
      pm.registerEvents(listener, this);

      // Instance prepared of CapsuleToyCommand.
      command = new OmikujiCapsuleToyCommand(this);

    } catch (Exception e){
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }
  }

  /**
   * JavaPlugin method onCommand.
   *
   * @return boolean true:Success false:Display the usage dialog set in plugin.yml
   */
  public boolean onCommand( CommandSender sender, Command commandInfo, String label, String[] args) {
    boolean hideUsageFlag = true;  // true:Success false:Display the usage dialog set in plugin.yml
    try{
      if(!commandInfo.getName().equals("omikujicapsuletoy")) {
        return hideUsageFlag;
      }

      if(args.length <= 0) {
        return hideUsageFlag;
      }
      String subCommand = args[0];

      command.initialize(sender, args);
      switch(subCommand) {
        case "list":
          if(sender.hasPermission("omikujicapsuletoy.list")) {
            hideUsageFlag = command.list();
          }
          break;

        case "modify":
          if(sender.hasPermission("omikujicapsuletoy.modify")) {
            hideUsageFlag = command.modify();
          }
          break;

        case "delete":
          if(sender.hasPermission("omikujicapsuletoy.delete")) {
            hideUsageFlag = command.delete();
          }
          break;

        case "ticket":
          if((sender instanceof BlockCommandSender) || (sender instanceof ConsoleCommandSender) || sender.isOp()) {
            for(Player p:OmikujiCapsuleToyUtility.getTarget(this, args[1], sender)) {  // @a @p @s @r or playername
              command.ticket(p);
            }
            hideUsageFlag = true;
          }
          break;

        case "enable":
          if(sender.isOp()) {
            hideUsageFlag = command.enable();
          }
          break;

        case "reload":
          if(sender.isOp()) {
            hideUsageFlag = command.reload();
          }
          break;

        case "disable":
          if(sender.isOp()) {
            hideUsageFlag = command.disable();
          }
          break;

        default:
          hideUsageFlag = false;
      }
    }catch(Exception e){
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }finally{
      command.finalize();
    }
    return hideUsageFlag;
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
      OmikujiCapsuleToyUtility.logStackTrace(e);
    }
  }
}
