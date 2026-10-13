package space.gorogoro.omikujicapsuletoy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/*
 * OmikujiCapsuleToyUtility
 * @license    GPLv3
 * @copyright  Copyright gorogoro.space 2021
 * @author     kubotan
 * @see        <a href="https://gorogoro.space">gorogoro.space</a>
 */
public class OmikujiCapsuleToyUtility {

  protected static final String NUMALPHA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  protected static final char COLOR_CHAR = '§';
  private static final String COLOR_CODE_CHARS = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
  // アイテムの名前・説明文用。Bukkit の String 版 API と同じ形式(§ と、§x§r§r§g§g§b§b の hex 色)
  private static final LegacyComponentSerializer ITEM_SERIALIZER = LegacyComponentSerializer.builder()
    .character(COLOR_CHAR)
    .hexColors()
    .useUnusualXRepeatedCharacterHexFormat()
    .build();
  // 看板用。Paper の SignChangeEvent.getLine/setLine の内部と同じもの
  private static final LegacyComponentSerializer SIGN_SERIALIZER = LegacyComponentSerializer.legacySection();
  // modify 中のプレイヤーと、チェストを設定するおみくじの登録名
  private static final Map<UUID, String> punches = new HashMap<UUID, String>();

  /**
   * Output stack trace to log file.
   * @param Exception Exception
   */
  public static void logStackTrace(Exception e){
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    pw.flush();
    Bukkit.getLogger().log(Level.WARNING, sw.toString());
  }

  /**
   * Determine whether punch is being processed.
   * @param Player Player
   * @return boolean true:That's right false:That's not it
   */
  public static boolean isInPunch(Player player){
    return punches.containsKey(player.getUniqueId());
  }

  /**
   * get CapsuleToy name in punch.
   * @param Player Player
   * @return String CapsuleToy name
   */
  public static String getCapsuleToyNameInPunch(Player player){
    return punches.get(player.getUniqueId());
  }

  /**
   * Set punch processing.
   * @param Player Player
   * @param OmikujiCapsuleToy capsuletoy
   * @param String CapsuleToy Name
   */
  public static void setPunch(Player player, OmikujiCapsuleToy capsuletoy, String capsuleToyName){
    removePunch(player, capsuletoy);
    punches.put(player.getUniqueId(), capsuleToyName);
  }

  /**
   * Remove punch processing.
   * @param Player Player
   * @param OmikujiCapsuleToy capsuletoy
   */
  public static void removePunch(Player player, OmikujiCapsuleToy capsuletoy){
    punches.remove(player.getUniqueId());
  }

  // プラグインの終了時に、modify 中の状態をすべて消す
  public static void clearPunches() {
    punches.clear();
  }

  /**
   * Send message to player
   * @param CommandSender CommandSender
   * @param String message
   */
  public static void sendMessage(CommandSender sender, String message){
    // §4 は DARK_RED、§c は RED
    sender.sendMessage(COLOR_CHAR + "4[OmikujiCapsuleToy]" + " " + COLOR_CHAR + "c" + message);
  }

  // altColorChar の色コード(&a など)を § に置き換える(ChatColor.translateAlternateColorCodes と同じ動き)
  public static String translateAlternateColorCodes(char altColorChar, String text) {
    char[] chars = text.toCharArray();
    for (int i = 0; i < chars.length - 1; i++) {
      if (chars[i] == altColorChar && COLOR_CODE_CHARS.indexOf(chars[i + 1]) > -1) {
        chars[i] = COLOR_CHAR;
        chars[i + 1] = Character.toLowerCase(chars[i + 1]);
      }
    }
    return new String(chars);
  }

  // config.yml の文を読み、色コード(&)を § に置き換える
  public static String configText(OmikujiCapsuleToy capsuletoy, String key) {
    String text = capsuletoy.getConfig().getString(key);
    return translateAlternateColorCodes('&', text == null ? "" : text);
  }

  // § の色コード付き文字列を、アイテムの名前・説明文用の Component にする
  // 非推奨の String 版 API(setDisplayName など)は斜体を明示的に外しているので、見た目をそろえるため同じようにする
  public static Component toItemComponent(String text) {
    return ITEM_SERIALIZER.deserialize(text).decoration(TextDecoration.ITALIC, false);
  }

  // 説明文の 1 行を、色コードを除いた文字だけにする(引換券の判定用)。null は空文字にする
  public static String toPlainText(Component component) {
    return component == null ? "" : PlainTextComponentSerializer.plainText().serialize(component);
  }

  // 看板の行と文字列の変換(Paper の String 版 API と同じ変換)。null は空文字にする
  public static Component toSignComponent(String text) {
    return SIGN_SERIALIZER.deserialize(text);
  }

  public static String fromSignComponent(Component component) {
    return component == null ? "" : SIGN_SERIALIZER.serialize(component);
  }

  /**
   * Generate random code
   * @return String code
   */
  public static String generateCode(){
    
    Random random = new Random();
    int length1 = 4;
    StringBuilder randomString1 = new StringBuilder(length1);
    for (int i = 0; i < length1; i++) {
      int index = random.nextInt(NUMALPHA.length());
      randomString1.append(NUMALPHA.charAt(index));
    }
    int length2 = 6;
    StringBuilder randomString2 = new StringBuilder(length2);
    for (int i = 0; i < length2; i++) {
      int index = random.nextInt(NUMALPHA.length());
      randomString2.append(NUMALPHA.charAt(index));
    }
    int length3 = 4;
    StringBuilder randomString3 = new StringBuilder(length3);
    for (int i = 0; i < length3; i++) {
      int index = random.nextInt(NUMALPHA.length());
      randomString3.append(NUMALPHA.charAt(index));
    }
    
    return String.format("%s-%s-%s",
        randomString1,
        randomString2,
        randomString3
    );
  }
  
  public static ArrayList<Player> getTarget(Plugin capsuletoy, String selector) {
    return getTarget(capsuletoy, selector, null);
  }

  public static ArrayList<Player> getTarget(Plugin capsuletoy, String selector, CommandSender sender) {

    ArrayList<Player> list = new ArrayList<Player>();
    if(!selector.startsWith("@")) {
      Player p = capsuletoy.getServer().getPlayer(selector);
      if(p != null) {
        list.add(p);
      }
    } else {
      switch(selector) {
        case "@a":
          list.addAll(capsuletoy.getServer().getOnlinePlayers());
          break;

        case "@p":
          if((sender instanceof Player) || (sender instanceof BlockCommandSender)) {
            Location senderLocation = null;
            if(sender instanceof Player) {
              Player sp = (Player) sender;
              senderLocation = sp.getLocation();
            } else if(sender instanceof BlockCommandSender) {
              BlockCommandSender sb = (BlockCommandSender) sender;
              senderLocation = sb.getBlock().getLocation();
            }
            if(senderLocation != null) {
              list.add(getNearestPlayerByLocation(senderLocation));
            }
          }
          break;

        case "@s":
          if(sender instanceof Player) {
            list.add((Player) sender);
          }
          break;

        case "@r":
          ArrayList<Player> curPlayerList = new ArrayList<Player>();
          for (Player p : Bukkit.getOnlinePlayers()) {
            curPlayerList.add(p);
          }
          Random random = new Random();
          list.add(curPlayerList.get(random.nextInt(curPlayerList.size())));
          break;
      }
    }
    return list;
  }

  public static Player getNearestPlayerByLocation(Location l) {
    double lastDistance = Double.MAX_VALUE;
    Player latestPlayer = null;
    for(Player p : l.getWorld().getPlayers()) {
      double distance = p.getLocation().distance(l);
      if(distance < lastDistance) {
          lastDistance = distance;
          latestPlayer = p;
      }
    }
    return latestPlayer;
  }

  // 入る余地が束の個数以上あるときだけ持ち物へ入れる。足りなければ束ごと足元へ落とす
  public static boolean giveOrDrop(Player player, ItemStack item) {
    ItemStack clone = item.clone();
    if (hasEnoughSpace(player, clone)) {
      player.getInventory().addItem(clone);
      return true;
    }
    player.getWorld().dropItemNaturally(player.getLocation(), clone);
    return false;
  }

  public static boolean hasEnoughSpace(Player player, ItemStack item) {
    int maxStack = item.getMaxStackSize();
    int space = 0;
    for (ItemStack invItem : player.getInventory().getStorageContents()) {
      if (invItem == null) {
        space += maxStack;
      } else if (invItem.isSimilar(item)) {
        space += maxStack - invItem.getAmount();
      }
    }
    return space >= item.getAmount();
  }
}
