package io.mysticbeans.finditemaddon.Utils;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EnchantmentUtil {

    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();
    private final static Map<Enchantment, String> enchantments = new HashMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

        enchantments.put(Enchantment.ARROW_DAMAGE, "Power");
        enchantments.put(Enchantment.ARROW_FIRE, "Flame");
        enchantments.put(Enchantment.ARROW_KNOCKBACK, "Punch");
        enchantments.put(Enchantment.ARROW_INFINITE, "Infinity");
        enchantments.put(Enchantment.DAMAGE_ALL, "Sharpness");
        enchantments.put(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
        enchantments.put(Enchantment.DAMAGE_UNDEAD, "Smite");
        enchantments.put(Enchantment.CHANNELING, "Channeling");
        enchantments.put(Enchantment.SWEEPING_EDGE, "Sweeping Edge");
        enchantments.put(Enchantment.DIG_SPEED, "Efficiency");
        enchantments.put(Enchantment.DURABILITY, "Unbreaking");
        enchantments.put(Enchantment.MENDING, "Mending");
        enchantments.put(Enchantment.FIRE_ASPECT, "Fire Aspect");
        enchantments.put(Enchantment.IMPALING, "Impaling");
        enchantments.put(Enchantment.KNOCKBACK, "Knockback");
        enchantments.put(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
        enchantments.put(Enchantment.LOOT_BONUS_MOBS, "Looting");
        enchantments.put(Enchantment.LOYALTY, "Loyalty");
        enchantments.put(Enchantment.OXYGEN, "Respiration");
        enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
        enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
        enchantments.put(Enchantment.PROTECTION_FALL, "Feather Falling");
        enchantments.put(Enchantment.PROTECTION_FIRE, "Fire Protection");
        enchantments.put(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
        enchantments.put(Enchantment.RIPTIDE, "Riptide");
        enchantments.put(Enchantment.SILK_TOUCH, "Silk Touch");
        enchantments.put(Enchantment.THORNS, "Thorns");
        enchantments.put(Enchantment.WATER_WORKER, "Aqua Affinity");
        enchantments.put(Enchantment.DEPTH_STRIDER, "Depth Strider");
        enchantments.put(Enchantment.FROST_WALKER, "Frost Walker");
        enchantments.put(Enchantment.BINDING_CURSE, "Curse of Binding");
        enchantments.put(Enchantment.VANISHING_CURSE, "Curse of Vanishing");
        enchantments.put(Enchantment.MULTISHOT, "Multishot");
        enchantments.put(Enchantment.SOUL_SPEED, "Soul Speed");
    }

    public static String mapBukkitEnchantment(Enchantment enchantment) {
        return enchantments.get(enchantment);
    }

    public static String toRoman(int number) {
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }
}
