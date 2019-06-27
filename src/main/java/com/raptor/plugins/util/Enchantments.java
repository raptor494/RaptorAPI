package com.raptor.plugins.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public final class Enchantments {

	/**
     * Provides protection against environmental damage
     */
    public static final Enchantment PROTECTION = Enchantment.PROTECTION_ENVIRONMENTAL;

    /**
     * Provides protection against fire damage
     */
    public static final Enchantment FIRE_PROTECTION = Enchantment.PROTECTION_FIRE;

    /**
     * Provides protection against fall damage
     */
    public static final Enchantment FEATHER_FALLING = Enchantment.PROTECTION_FALL;

    /**
     * Provides protection against explosive damage
     */
    public static final Enchantment BLAST_PROTECTION = Enchantment.PROTECTION_EXPLOSIONS;

    /**
     * Provides protection against projectile damage
     */
    public static final Enchantment PROJECTILE_PROTECTION = Enchantment.PROTECTION_PROJECTILE;

    /**
     * Decreases the rate of air loss whilst underwater
     */
    public static final Enchantment RESPIRATION = Enchantment.OXYGEN;

    /**
     * Increases the speed at which a player may mine underwater
     */
    public static final Enchantment AQUA_AFFINITY = Enchantment.WATER_WORKER;

    /**
     * Damages the attacker
     */
    public static final Enchantment THORNS = Enchantment.THORNS;

    /**
     * Increases walking speed while in water
     */
    public static final Enchantment DEPTH_STRIDER = Enchantment.DEPTH_STRIDER;

    /**
     * Freezes any still water adjacent to ice / frost which player is walking on
     */
    public static final Enchantment FROST_WALKER = Enchantment.FROST_WALKER;

    /**
     * Item cannot be removed
     */
    public static final Enchantment BINDING_CURSE = Enchantment.BINDING_CURSE;

    /**
     * Increases damage against all targets
     */
    public static final Enchantment SHARPNESS = Enchantment.DAMAGE_ALL;

    /**
     * Increases damage against undead targets
     */
    public static final Enchantment SMITE = Enchantment.DAMAGE_UNDEAD;

    /**
     * Increases damage against arthropod targets
     */
    public static final Enchantment BANE_OF_ARTHROPODS = Enchantment.DAMAGE_ARTHROPODS;

    /**
     * All damage to other targets will knock them back when hit
     */
    public static final Enchantment KNOCKBACK = Enchantment.KNOCKBACK;

    /**
     * When attacking a target, has a chance to set them on fire
     */
    public static final Enchantment FIRE_ASPECT = Enchantment.FIRE_ASPECT;

    /**
     * Provides a chance of gaining extra loot when killing monsters
     */
    public static final Enchantment LOOTING = Enchantment.LOOT_BONUS_MOBS;

    /**
     * Increases damage against targets when using a sweep attack
     */
    public static final Enchantment SWEEPING_EDGE = Enchantment.SWEEPING_EDGE;

    /**
     * Increases the rate at which you mine/dig
     */
    public static final Enchantment EFFICIENCY = Enchantment.DIG_SPEED;

    /**
     * Allows blocks to drop themselves instead of fragments (for example,
     * stone instead of cobblestone)
     */
    public static final Enchantment SILK_TOUCH = Enchantment.SILK_TOUCH;

    /**
     * Decreases the rate at which a tool looses durability
     */
    public static final Enchantment UNBREAKING = Enchantment.DURABILITY;

    /**
     * Provides a chance of gaining extra loot when destroying blocks
     */
    public static final Enchantment FORTUNE = Enchantment.LOOT_BONUS_BLOCKS;

    /**
     * Provides extra damage when shooting arrows from bows
     */
    public static final Enchantment POWER = Enchantment.ARROW_DAMAGE;

    /**
     * Provides a knockback when an entity is hit by an arrow from a bow
     */
    public static final Enchantment PUNCH = Enchantment.ARROW_KNOCKBACK;

    /**
     * Sets entities on fire when hit by arrows shot from a bow
     */
    public static final Enchantment FLAME = Enchantment.ARROW_FIRE;

    /**
     * Provides infinite arrows when shooting a bow
     */
    public static final Enchantment INFINITY = Enchantment.ARROW_INFINITE;

    /**
     * Decreases odds of catching worthless junk
     */
    public static final Enchantment LUCK_OF_THE_SEA = Enchantment.LUCK;

    /**
     * Increases rate of fish biting your hook
     */
    public static final Enchantment LURE = Enchantment.LURE;

    /**
     * Causes a thrown trident to return to the player who threw it
     */
    public static final Enchantment LOYALTY = Enchantment.LOYALTY;

    /**
     * Deals more damage to mobs that live in the ocean
     */
    public static final Enchantment IMPALING = Enchantment.IMPALING;

    /**
     * When it is rainy, launches the player in the direction their trident is thrown
     */
    public static final Enchantment RIPTIDE = Enchantment.RIPTIDE;

    /**
     * Strikes lightning when a mob is hit with a trident if conditions are
     * stormy
     */
    public static final Enchantment CHANNELING = Enchantment.CHANNELING;

    /**
     * Allows mending the item using experience orbs
     */
    public static final Enchantment MENDING = Enchantment.MENDING;

    /**
     * Item disappears instead of dropping
     */
    public static final Enchantment VANISHING_CURSE = Enchantment.VANISHING_CURSE;
    
    private static final Map<String, Enchantment> REGISTRY;
    
    static {
    	Map<String, Enchantment> registry = new HashMap<>();
    	
    	for(Field field : Enchantments.class.getFields()) {
    		try {
				registry.put(field.getName(), (Enchantment)field.get(null));
			} catch(IllegalArgumentException | IllegalAccessException e) {
				throw new AssertionError(e);
			}
    	}
    	
    	REGISTRY = Collections.unmodifiableMap(registry);
    }
    
    /**
     * @param name the name (no namespace) of the enchantment
     * @throws IllegalArgumentException if {@code name} doesn't correspond to any known enchantment
     */
    public static Enchantment valueOf(String name) {
    	Enchantment ench = REGISTRY.get(name.toUpperCase());
    	if(ench == null)
    		throw new IllegalArgumentException("No Enchantment exists with id '" + name + "'");
    	else return ench;
    }
    
    public static Enchantment valueOf(NamespacedKey key) {
    	return Enchantment.getByKey(key);
    }
	
	private Enchantments() {
		throw new UnsupportedOperationException("Enchantments cannot be instantiated!");
	}
}
