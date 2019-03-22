package com.raptor.plugins;

import static org.bukkit.Material.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.raptor.plugins.api.RaptorAPI;
import com.raptor.plugins.config.gson.PotionEffectTypeTypeAdapter;
import com.raptor.plugins.util.StringUtils;
import com.raptor.plugins.util.TypedList;
import com.raptor.plugins.util.TypedMap;
import com.raptor.plugins.util.TypedSet;

import de.erethon.commons.item.AttributeWrapper;
import de.erethon.commons.item.InternalAttribute;
import de.erethon.commons.item.InternalOperation;
import de.erethon.commons.item.InternalSlot;
import de.erethon.commons.item.ItemUtil;
import net.md_5.bungee.api.ChatColor;

public class ItemStackModel implements Cloneable {
	Material type;
	String customItem;
	private transient boolean mergedWithCustomItem = false;
	int count = 1;
	int damage;
	String name;
	TypedList<String> lore;
	boolean unbreakable;
	/**
	 * Automatically adjusts the lore of created items
	 * if any enchantments are above 10
	 */
	boolean autoEnchantmentLore;
	EnumSet<ItemFlag> flags;
	TypedMap<Enchantment, Integer> enchantments;
	TypedList<AttributeWrapper> attributes;
	
	// banner or shield patterns
	TypedList<Pattern> patterns;
	
	// shield base color
	DyeColor shieldColor;
	
	// book data
	String title, author;
	TypedList<String> pages;
	BookMeta.Generation generation;
	
	// enchanted book data
	TypedMap<Enchantment, Integer> storedEnchantments;
	
	// firework star data
	FireworkEffect fireworkEffect;
	
	// firework data
	int power;
	TypedList<FireworkEffect> fireworkEffects;
	
	// knowledge book data
	TypedSet<NamespacedKey> recipes;
	
	// leather/map/potion color
	Color color;
	
	// map data
	int mapId;
	boolean scaling;
	String locationName;
	
	// potion/tipped arrow data
	PotionData basePotion;
	TypedList<PotionEffect> potionEffects;
	
	// player head data
	SkullOwner skullOwner;
	
	// tropical fish bucket data
	DyeColor patternColor, bodyColor;
	TropicalFish.Pattern fish;
	
	public ItemStackModel() {}
	
	public ItemStackModel(ItemStack item) {
		mergeWith(item);
	}
	
	void mergeCustomItem() {
		if(mergedWithCustomItem) return;
		mergedWithCustomItem = true;
		if(customItem != null) {
			ItemStack custom = RaptorAPI.getInstance().getCustomItem(customItem);
			if(custom != null) {
				mergeWith(custom);
			}
		}
	}
	
	public Material getType() {
		return this.type;
	}
	
	public ItemStackModel setType(Material material) {
		Validate.notNull(material, "Material may not be null");
		Validate.isTrue(material.isItem(), "Material must be an item");
		this.type = material;
		return this;
	}
	
	public int getAmount() {
		return count;
	}
	
	public ItemStackModel setAmount(int count) {
		Validate.isTrue(count > 0, "Count may not be <= 0");
		this.count = count;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStackModel setName(String name) {
		this.name = name;
		return this;
	}
	
	public boolean shouldAutoAdjustLoreForEnchantments() {
		return autoEnchantmentLore;
	}
	
	public ItemStackModel setAutoAdjustLoreForEnchantments(boolean auto) {
		autoEnchantmentLore = auto;
		return this;
	}
	
	public List<String> getLore() {
		if(lore == null) {
			lore = new TypedList<>(String.class);
		}
		return lore;
	}
	
	public int loreCount() {
		return getLore().size();
	}
	
	public ItemStackModel setLore(List<String> lore) {
		Validate.noNullElements(lore, "Lore list may not be null/contain null elements");
		if(getLore() != lore) {
			this.lore.clear();
			this.lore.addAll(lore);
		}
		return this;
	}
	
	public ItemStackModel setLore(String... lore) {
		Validate.noNullElements(lore, "Lore list may not be null/contain null elements");
		getLore().clear();
		for(String line : lore) {
			this.lore.add(line);
		}
		return this;
	}
	
	public ItemStackModel addLore(String line) {
		Validate.notNull(line, "Lore list may not be null/contain null elements");
		getLore().add(line);
		return this;
	}
	
	public ItemStackModel addLore(int index, String line) {
		Validate.notNull(line, "Lore list may not be null/contain null elements");
		getLore().add(index, line);
		return this;
	}
	
	public ItemStackModel addLore(String line1, String... lines) {
		Validate.noNullElements(lines, "Lore list may not be null/contain null elements");
		Validate.notNull(line1, "Lore list may not be null/contain null elements");
		getLore().add(line1);
		for(String line : lines) {
			lore.add(line);
		}
		return this;
	}
	
	public ItemStackModel addLore(int index, String line1, String... lines) {
		Validate.noNullElements(lines, "Lore list may not be null/contain null elements");
		Validate.notNull(line1, "Lore list may not be null/contain null elements");
		getLore().add(index++, line1);
		for(String line : lines) {
			lore.add(index++, line);
		}
		return this;
	}
	
	public ItemStackModel addLore(Collection<String> lines) {
		Validate.noNullElements(lines, "Lore list may not be null/contain null elements");
		getLore().addAll(lines);
		return this;
	}
	
	public ItemStackModel addLore(int index, Collection<String> lines) {
		Validate.noNullElements(lines, "Lore list may not be null/contain null elements");
		getLore().addAll(index, lines);
		return this;
	}
	
	public ItemStackModel removeLore(String line) {
		Validate.notNull(line, "Lore lines to remove may not be null/contain null elements");
		getLore().remove(line);
		return this;
	}
	
	public ItemStackModel removeLore(int line) {
		getLore().remove(line);
		return this;
	}
	
	public ItemStackModel removeLore(String... lines) {
		Validate.noNullElements(lines, "Lore lines to remove may not be null/contain null elements");
		if(!getLore().isEmpty()) {
			for(String line : lines) {
				lore.remove(line);
			}
		}
		return this;
	}
	
	public ItemStackModel removeLore(Collection<String> lines) {
		Validate.noNullElements(lines, "Lore lines to remove may not be null/contain null elements");
		getLore().removeAll(lines);
		return this;
	}
	
	public ItemStackModel clearLore() {
		getLore().clear();
		return this;
	}
	
	public boolean isUnbreakable() {
		return unbreakable;
	}
	
	public ItemStackModel setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}
	
	public Set<ItemFlag> getFlags() {
		if(flags == null) {
			flags = EnumSet.noneOf(ItemFlag.class);
		}
		return flags;
	}
	
	public int flagCount() {
		return getFlags().size();
	}
	
	public ItemStackModel setFlags(Set<ItemFlag> flags) {
		Validate.noNullElements(flags, "Flags set may not be null/contain null elements");
		if(getFlags() != flags) {
			this.flags.clear();
			this.flags.addAll(flags);
		}
		return this;
	}
	
	public ItemStackModel setFlags(ItemFlag... flags) {
		Validate.noNullElements(flags, "Flags set may not be null/contain null elements");
		getFlags().clear();
		for(ItemFlag flag : flags) {
			this.flags.add(flag);
		}
		return this;
	}
	
	public ItemStackModel addFlag(ItemFlag flag) {
		Validate.notNull(flag, "Flags set may not be null/contain null elements");
		getFlags().add(flag);
		return this;
	}
	
	public ItemStackModel addFlags(ItemFlag flag1, ItemFlag... flags) {
		Validate.noNullElements(flags, "Flags set may not be null/contain null elements");
		Validate.notNull(flag1, "Flags set may not be null/contain null elements");
		getFlags().add(flag1);
		for(ItemFlag flag : flags) {
			this.flags.add(flag);
		}
		return this;
	}
	
	public ItemStackModel addFlags(Collection<ItemFlag> flags) {
		if(getFlags() != flags) {
			Validate.noNullElements(flags, "Flags set may not be null/contain null elements");
			this.flags.addAll(flags);
		}
		return this;
	}
	
	public ItemStackModel removeFlag(ItemFlag flag) {
		Validate.notNull(flag, "Flags to remove may not be null/contain null elements");
		getFlags().remove(flag);
		return this;
	}
	
	public ItemStackModel removeFlags(ItemFlag flag1, ItemFlag... flags) {
		Validate.noNullElements(flags, "Flags to remove may not be null/contain null elements");
		Validate.notNull(flag1, "Flags to remove may not be null/contain null elements");
		if(!getFlags().isEmpty()) {
			this.flags.remove(flag1);
			for(ItemFlag flag : flags) {
				this.flags.remove(flag);
			}
		}
		return this;
	}
	
	public ItemStackModel removeFlags(Collection<ItemFlag> flags) {
		if(getFlags() == flags) {
			this.flags.clear();
		} else {
			Validate.noNullElements(flags, "Flags to remove may not be null/contain null elements");
			this.flags.removeAll(flags);
		}
		return this;
	}
	
	public ItemStackModel clearFlags() {
		getFlags().clear();
		return this;
	}
	
	public Map<Enchantment, Integer> getEnchantments() {
		if(enchantments == null) {
			enchantments = new TypedMap<>(Enchantment.class, Integer.class);
		}
		return enchantments;
	}
	
	public int enchantmentCount() {
		return getEnchantments().size();
	}
	
	public ItemStackModel setEnchantments(Map<Enchantment, Integer> enchantments) {
		if(getEnchantments() != enchantments) {
			Validate.notNull(enchantments, "Enchantments map may not be null/contain null elements");
			Validate.noNullElements(enchantments.keySet(), "Enchantments map may not be null/contain null elements");
			Validate.noNullElements(enchantments.values(), "Enchantments map may not be null/contain null elements");
			this.enchantments.clear();
			this.enchantments.putAll(enchantments);
		}
		return this;
	}
	
	public ItemStackModel addEnchantment(Enchantment enchantment, int level) {
		Validate.notNull(enchantment, "Enchantments map may not be null/contain null elements");
		getEnchantments().put(enchantment, level);
		return this;
	}
	
	public ItemStackModel addEnchantments(Map<Enchantment, Integer> enchantments) {
		if(getEnchantments() != enchantments) {
			Validate.notNull(enchantments, "Enchantments map may not be null/contain null elements");
			Validate.noNullElements(enchantments.keySet(), "Enchantments map may not be null/contain null elements");
			Validate.noNullElements(enchantments.values(), "Enchantments map may not be null/contain null elements");
			this.enchantments.putAll(enchantments);
		}
		return this;
	}
	
	public ItemStackModel removeEnchantment(Enchantment enchantment) {
		Validate.notNull(enchantment, "Enchantments to remove may not be null/contain null elements");
		if(!getEnchantments().isEmpty()) {
			enchantments.remove(enchantment);
		}
		return this;
	}
	
	public ItemStackModel removeEnchantment(Enchantment enchantment, int level) {
		Validate.notNull(enchantment, "Enchantments to remove may not be null/contain null elements");
		if(!getEnchantments().isEmpty()) {
			enchantments.remove(enchantment, level);
		}
		return this;
	}
	
	public ItemStackModel removeEnchantments(Enchantment enchantment1, Enchantment... enchantments) {
		Validate.noNullElements(enchantments, "Enchantments to remove may not be null/contain null elements");
		Validate.notNull(enchantment1, "Enchantments to remove may not be null/contain null elements");
		if(!getEnchantments().isEmpty()) {
			this.enchantments.remove(enchantment1);
			for(Enchantment enchantment : enchantments) {
				this.enchantments.remove(enchantment);
			}
		}
		return this;
	}
	
	public ItemStackModel removeEnchantments(Collection<Enchantment> enchantments) {
		Validate.noNullElements(enchantments, "Enchantments to remove may not be null/contain null elements");
		if(!getEnchantments().isEmpty()) {
			for(Enchantment enchantment : enchantments) {
				this.enchantments.remove(enchantment);
			}
		}
		return this;
	}
	
	public ItemStackModel removeEnchantments(Map<Enchantment, Integer> enchantments) {
		if(getEnchantments() == enchantments) {
			this.enchantments.clear();
		} else {
			Validate.notNull(enchantments, "Enchantments to remove may not be null/contain null elements");
			if(!this.enchantments.isEmpty()) {
				for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
					this.enchantments.remove(entry.getKey(), entry.getValue());
				}
			}
		}
		return this;
	}
	
	public ItemStackModel clearEnchantments() {
		getEnchantments().clear();
		return this;
	}
	
	public List<AttributeWrapper> getAttributes() {
		if(attributes == null) {
			attributes = new TypedList<>(AttributeWrapper.class);
		}
		return attributes;
	}
	
	public int attributeCount() {
		return getAttributes().size();
	}
	
	public ItemStackModel setAttributes(Collection<AttributeWrapper> attributes) {
		if(getAttributes() != attributes) {
			Validate.noNullElements(attributes, "Attributes list may not be null/contain null elements");
			this.attributes.clear();
			this.attributes.addAll(attributes);
		}
		return this;
	}
	
	public ItemStackModel setAttributes(AttributeWrapper... attributes) {
		Validate.noNullElements(attributes, "Attributes list may not be null/contain null elements");
		getAttributes().clear();
		for(AttributeWrapper attribute : attributes) {
			this.attributes.add(attribute);
		}
		return this;
	}
	
	public ItemStackModel setAttributes(Map<Attribute, AttributeModifier> attributes) {
		Validate.notNull(attributes, "Attributes list may not be null/contain null elements");
		Validate.noNullElements(attributes.keySet(), "Attributes list may not be null/contain null elements");
		Validate.noNullElements(attributes.values(), "Attributes list may not be null/contain null elements");
		getAttributes().clear();
		for(Map.Entry<Attribute, AttributeModifier> entry : attributes.entrySet()) {
			this.attributes.add(wrapBukkitAttribute(entry.getKey(), entry.getValue()));
		}
		return this;
	}
	
	public ItemStackModel addAttribute(AttributeWrapper attribute) {
		Validate.notNull(attribute, "Attributes list may not be null/contain null elements");
		getAttributes().add(attribute);
		return this;
	}
	
	public ItemStackModel addAttribute(Attribute attribute, AttributeModifier modifier) {
		Validate.notNull(attribute, "Attributes list may not be null/contain null elements");
		Validate.notNull(modifier, "Attributes list may not be null/contain null elements");
		getAttributes().add(wrapBukkitAttribute(attribute, modifier));
		return this;
	}
	
	public ItemStackModel addAttributes(Collection<AttributeWrapper> attributes) {
		if(getAttributes() != attributes) {
			Validate.noNullElements(attributes, "Attributes list may not be null/contain null elements");
			this.attributes.addAll(attributes);
		}
		return this;
	}
	
	public ItemStackModel addAttributes(Map<Attribute, AttributeModifier> attributes) {
		if(getAttributes() != attributes) {
			Validate.notNull(attributes, "Attributes list may not be null/contain null elements");
			Validate.noNullElements(attributes.keySet(), "Attributes list may not be null/contain null elements");
			Validate.noNullElements(attributes.values(), "Attributes list may not be null/contain null elements");
			for(Map.Entry<Attribute, AttributeModifier> entry : attributes.entrySet()) {
				this.attributes.add(wrapBukkitAttribute(entry.getKey(), entry.getValue()));
			}
		}
		return this;
	}
	
	public ItemStackModel removeAttribute(AttributeWrapper attribute) {
		Validate.notNull(attribute, "Attributes to remove may not be null/contain null elements");
		if(!getAttributes().isEmpty()) {
			this.attributes.removeIf(Predicate.isEqual(attribute));
		}
		return this;
	}
	
	public ItemStackModel removeAttribute(Attribute attribute) {
		Validate.notNull(attribute, "Attributes to remove may not be null/contain null elements");
		if(!getAttributes().isEmpty()) {
			InternalAttribute iAttribute = InternalAttribute.fromBukkit(attribute);
			for(int i = attributes.size()-1; i >= 0; i--) {
				if(attributes.get(i).getAttribute() == iAttribute) {
					attributes.remove(i);
				}
			}
		}
		return this;
	}
	
	public ItemStackModel removeAttribute(Attribute attribute, AttributeModifier modifier) {
		Validate.notNull(attribute, "Attributes to remove may not be null/contain null elements");
		Validate.notNull(modifier, "Attributes to remove may not be null/contain null elements");
		if(!getAttributes().isEmpty()) {
			attributes.removeIf(bukkitAttributeEqualityPredicate(attribute, modifier));
		}
		return this;
	}
	
	public ItemStackModel removeAttributes(AttributeWrapper attribute1, AttributeWrapper... attributes) {
		Validate.noNullElements(attributes, "Attributes to remove may not be null/contain null elements");
		Validate.notNull(attribute1, "Attributes to remove may not be null/contain null elements");
		if(!getAttributes().isEmpty()) {
			this.attributes.removeIf(Predicate.isEqual(attribute1));
			for(AttributeWrapper attribute : attributes) {
				this.attributes.removeIf(Predicate.isEqual(attribute));
			}
		}
		return this;
	}
	
	public ItemStackModel removeAttributes(Collection<AttributeWrapper> attributes) {
		if(getAttributes() == attributes) {
			this.attributes.clear();
		} else {
			Validate.noNullElements(attributes, "Attributes to remove may not be null/contain null elements");
			this.attributes.removeAll(attributes);
		}
		return this;
	}
	
	public ItemStackModel removeAttributes(Map<Attribute, AttributeModifier> attributes) {
		if(!getAttributes().isEmpty()) {
			Validate.notNull(attributes, "Attributes to remove may not be null/contain null elements");
			Validate.noNullElements(attributes.keySet(), "Attributes to remove may not be null/contain null elements");
			Validate.noNullElements(attributes.values(), "Attributes to remove may not be null/contain null elements");
			for(Map.Entry<Attribute, AttributeModifier> entry : attributes.entrySet()) {
				this.attributes.removeIf(bukkitAttributeEqualityPredicate(entry.getKey(), entry.getValue()));
			}
		}
		return this;
	}
	
	private static Predicate<AttributeWrapper> bukkitAttributeEqualityPredicate(Attribute attribute, AttributeModifier modifier) {
		InternalAttribute iAttribute = InternalAttribute.fromBukkit(attribute);
		InternalSlot iSlot = InternalSlot.fromBukkit(modifier.getSlot());
		InternalOperation iOperation = InternalOperation.fromBukkit(modifier.getOperation());
		double amount = modifier.getAmount();
		return wrapper -> wrapper.getAttribute() == iAttribute
				&& wrapper.getSlots().contains(iSlot)
				&& wrapper.getAmount() == amount
				&& wrapper.getOperation().equals(iOperation);
	}
	
	public ItemStackModel clearAttributes() {
		getAttributes().clear();
		return this;
	}
	
	public static AttributeWrapper wrapBukkitAttribute(Attribute attribute, AttributeModifier modifier) {
		return AttributeWrapper.builder()
				.amount(modifier.getAmount())
				.name(modifier.getName())
				.slots(InternalSlot.fromBukkit(modifier.getSlot()))
				.attribute(InternalAttribute.fromBukkit(attribute))
				.operation(InternalOperation.fromBukkit(modifier.getOperation()))
				.build();
	}
	
	public List<Pattern> getPatterns() {
		if(patterns == null) {
			patterns = new TypedList<>(Pattern.class);
		}
		return patterns;
	}
	
	public int patternCount() {
		return getPatterns().size();
	}
	
	public ItemStackModel setPatterns(List<Pattern> patterns) {
		Validate.noNullElements(patterns, "Patterns list may not be null/contain null elements");
		if(getPatterns() != patterns) {
			this.patterns.clear();
			this.patterns.addAll(patterns);
		}
		return this;
	}
	
	public ItemStackModel setPatterns(Pattern... patterns) {
		Validate.noNullElements(patterns, "Patterns list may not be null/contain null elements");
		getPatterns().clear();
		for(Pattern pattern : patterns) {
			this.patterns.add(pattern);
		}
		return this;
	}
	
	public ItemStackModel addPattern(Pattern pattern) {
		Validate.notNull(pattern, "Patterns list may not be null/contain null elements");
		getPatterns().add(pattern);
		return this;
	}
	
	public ItemStackModel addPattern(int index, Pattern pattern) {
		Validate.notNull(pattern, "Patterns list may not be null/contain null elements");
		getPatterns().add(index, pattern);
		return this;
	}
	
	public ItemStackModel addPatterns(Pattern pattern1, Pattern... patterns) {
		Validate.noNullElements(patterns, "Patterns list may not be null/contain null elements");
		Validate.notNull(pattern1, "Patterns list may not be null/contain null elements");
		getPatterns().add(pattern1);
		for(Pattern pattern : patterns) {
			this.patterns.add(pattern);
		}
		return this;
	}
	
	public ItemStackModel addPatterns(int index, Pattern pattern1, Pattern... patterns) {
		Validate.noNullElements(patterns, "Patterns list may not be null/contain null elements");
		Validate.notNull(pattern1, "Patterns list may not be null/contain null elements");
		getPatterns().add(index++, pattern1);
		for(Pattern pattern : patterns) {
			this.patterns.add(index++, pattern);
		}
		return this;
	}
	
	public ItemStackModel addPatterns(Collection<Pattern> patterns) {
		Validate.noNullElements(patterns, "Patterns list may not be null/contain null elements");
		getPatterns().addAll(patterns);
		return this;
	}
	
	public ItemStackModel addPatterns(int index, Collection<Pattern> patterns) {
		Validate.noNullElements(patterns, "Patterns list may not be null/contain null elements");
		getPatterns().addAll(index, patterns);
		return this;
	}
	
	public ItemStackModel removePattern(Pattern pattern) {
		Validate.notNull(pattern, "Patterns to remove may not be null/contain null elements");
		getPatterns().remove(pattern);
		return this;
	}
	
	public ItemStackModel removePattern(int index) {
		getPatterns().remove(index);
		return this;
	}
	
	public ItemStackModel removePatterns(Pattern pattern1, Pattern... patterns) {
		Validate.noNullElements(patterns, "Patterns to remove may not be null/contain null elements");
		Validate.notNull(pattern1, "Patterns to remove may not be null/contain null elements");
		getPatterns().remove(pattern1);
		for(Pattern pattern : patterns) {
			this.patterns.remove(pattern);
		}
		return this;
	}
	
	public ItemStackModel removePatterns(Collection<Pattern> patterns) {
		Validate.noNullElements(patterns, "Patterns to remove may not be null/contain null elements");
		getPatterns().removeAll(patterns);
		return this;
	}
	
	public ItemStackModel clearPatterns() {
		getPatterns().clear();
		return this;
	}
	
	public DyeColor getShieldColor() {
		return shieldColor;
	}
	
	public ItemStackModel setShieldColor(DyeColor shieldColor) {
		this.shieldColor = shieldColor;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ItemStackModel setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public ItemStackModel setAuthor(String author) {
		this.author = author;
		return this;
	}

	public List<String> getPages() {
		if(pages == null) {
			pages = new TypedList<>(String.class);
		}
		return pages;
	}
	
	public int pageCount() {
		return getPages().size();
	}

	public ItemStackModel setPages(List<String> pages) {
		Validate.noNullElements(pages, "Pages list may not be null/contain null elements");
		if(getPages() != pages) {
			this.pages.clear();
			this.pages.addAll(pages);
		}
		return this;
	}
	
	public ItemStackModel setPages(String... pages) {
		Validate.noNullElements(pages, "Pages list may not be null/contain null elements");
		getPages().clear();
		for(String page : pages) {
			this.pages.add(page);
		}
		return this;
	}
	
	public ItemStackModel addPage(String page) {
		Validate.notNull(page, "Pages list may not be null/contain null elements");
		getPages().add(page);
		return this;
	}
	
	public ItemStackModel addPage(int index, String page) {
		Validate.notNull(page, "Pages list may not be null/contain null elements");
		getPages().add(index, page);
		return this;
	}
	
	public ItemStackModel addPages(String page1, String... pages) {
		Validate.noNullElements(pages, "Pages list may not be null/contain null elements");
		Validate.notNull(page1, "Pages list may not be null/contain null elements");
		getPages().add(page1);
		for(String page : pages) {
			this.pages.add(page);
		}
		return this;
	}
	
	public ItemStackModel addPages(int index, String page1, String... pages) {
		Validate.noNullElements(pages, "Pages list may not be null/contain null elements");
		Validate.notNull(page1, "Pages list may not be null/contain null elements");
		getPages().add(index++, page1);
		for(String page : pages) {
			this.pages.add(index++, page);
		}
		return this;
	}
	
	public ItemStackModel addPages(Collection<String> pages) {
		Validate.noNullElements(pages, "Pages list may not be null/contain null elements");
		getPages().addAll(pages);
		return this;
	}
	
	public ItemStackModel addPages(int index, Collection<String> pages) {
		Validate.noNullElements(pages, "Pages list may not be null/contain null elements");
		getPages().addAll(index, pages);
		return this;
	}
	
	public ItemStackModel removePage(String page) {
		Validate.notNull(page, "Pages to remove may not be null/contain null elements");
		getPages().remove(page);
		return this;
	}
	
	public ItemStackModel removePage(int page) {
		getPages().remove(page);
		return this;
	}
	
	public ItemStackModel removePages(String page1, String... pages) {
		Validate.noNullElements(pages, "Pages to remove may not be null/contain null elements");
		Validate.notNull(page1, "Pages to remove may not be null/contain null elements");
		getPages().remove(page1);
		for(String page : pages) {
			this.pages.remove(page);
		}
		return this;
	}
	
	public ItemStackModel removePages(Collection<String> pages) {
		Validate.noNullElements(pages, "Pages to remove may not be null/contain null elements");
		getPages().removeAll(pages);
		return this;
	}
	
	public ItemStackModel clearPages() {
		getPages().clear();
		return this;
	}

	public BookMeta.Generation getGeneration() {
		return generation;
	}

	public ItemStackModel setGeneration(BookMeta.Generation generation) {
		this.generation = generation;
		return this;
	}

	public Map<Enchantment, Integer> getStoredEnchantments() {
		if(storedEnchantments == null) {
			storedEnchantments = new TypedMap<>(Enchantment.class, Integer.class);
		}
		return storedEnchantments;
	}
	
	public int storedEnchantmentCount() {
		return getStoredEnchantments().size();
	}

	public ItemStackModel setStoredEnchantments(Map<Enchantment, Integer> storedEnchantments) {
		Validate.notNull(storedEnchantments, "Stored enchantments map may not be null/contain null elements");
		Validate.noNullElements(storedEnchantments.keySet(), "Stored enchantments map may not be null/contain null elements");
		Validate.noNullElements(storedEnchantments.values(), "Stored enchantments map may not be null/contain null elements");
		if(getStoredEnchantments() != storedEnchantments) {
			this.storedEnchantments.clear();
			this.storedEnchantments.putAll(storedEnchantments);
		}
		return this;
	}
	
	public ItemStackModel addStoredEnchantment(Enchantment storedEnchantment, int level) {
		Validate.notNull(storedEnchantment, "Stored enchantments map may not be null/contain null elements");
		getStoredEnchantments().put(storedEnchantment, level);
		return this;
	}
	
	public ItemStackModel addStoredEnchantments(Map<Enchantment, Integer> storedEnchantments) {
		if(getStoredEnchantments() != storedEnchantments) {
			Validate.notNull(storedEnchantments, "Stored enchantments map may not be null/contain null elements");
			Validate.noNullElements(storedEnchantments.keySet(), "Stored enchantments map may not be null/contain null elements");
			Validate.noNullElements(storedEnchantments.values(), "Stored enchantments map may not be null/contain null elements");
			this.storedEnchantments.putAll(storedEnchantments);
		}
		return this;
	}
	
	public ItemStackModel removeStoredEnchantment(Enchantment storedEnchantment) {
		Validate.notNull(storedEnchantment, "Stored enchantments to remove may not be null/contain null elements");
		if(!getStoredEnchantments().isEmpty()) {
			storedEnchantments.remove(storedEnchantment);
		}
		return this;
	}
	
	public ItemStackModel removeStoredEnchantment(Enchantment storedEnchantment, int level) {
		Validate.notNull(storedEnchantment, "Stored enchantments to remove may not be null/contain null elements");
		if(!getStoredEnchantments().isEmpty()) {
			storedEnchantments.remove(storedEnchantment, level);
		}
		return this;
	}
	
	public ItemStackModel removeStoredEnchantments(Enchantment storedEnchantment1, Enchantment... storedEnchantments) {
		Validate.noNullElements(storedEnchantments, "Stored enchantments to remove may not be null/contain null elements");
		Validate.notNull(storedEnchantment1, "Stored enchantments to remove may not be null/contain null elements");
		if(!getStoredEnchantments().isEmpty()) {
			this.storedEnchantments.remove(storedEnchantment1);
			for(Enchantment storedEnchantment : storedEnchantments) {
				this.storedEnchantments.remove(storedEnchantment);
			}
		}
		return this;
	}
	
	public ItemStackModel removeStoredEnchantments(Collection<Enchantment> storedEnchantments) {
		Validate.noNullElements(storedEnchantments, "Stored enchantments to remove may not be null/contain null elements");
		if(!getStoredEnchantments().isEmpty()) {
			for(Enchantment storedEnchantment : storedEnchantments) {
				this.storedEnchantments.remove(storedEnchantment);
			}
		}
		return this;
	}
	
	public ItemStackModel removeStoredEnchantments(Map<Enchantment, Integer> storedEnchantments) {
		if(getStoredEnchantments() == storedEnchantments) {
			this.storedEnchantments.clear();
		} else {
			Validate.notNull(storedEnchantments, "Stored enchantments to remove may not be null/contain null elements");
			if(!this.storedEnchantments.isEmpty()) {
				for(Map.Entry<Enchantment, Integer> entry : storedEnchantments.entrySet()) {
					this.storedEnchantments.remove(entry.getKey(), entry.getValue());
				}
			}
		}
		return this;
	}
	
	public ItemStackModel clearStoredEnchantments() {
		getStoredEnchantments().clear();
		return this;
	}

	public FireworkEffect getFireworkEffect() {
		return fireworkEffect;
	}

	public ItemStackModel setFireworkEffect(FireworkEffect fireworkEffect) {
		this.fireworkEffect = fireworkEffect;
		return this;
	}

	/**
	 * @return the firework's power
	 */
	public int getPower() {
		return power;
	}

	public ItemStackModel setPower(int power) {
		Validate.isTrue(power >= 0, "Firework power may not be negative");
		this.power = power;
		return this;
	}

	public List<FireworkEffect> getFireworkEffects() {
		if(fireworkEffects == null) {
			fireworkEffects = new TypedList<>(FireworkEffect.class);
		}
		return fireworkEffects;
	}
	
	public int fireworkEffectCount() {
		return getFireworkEffects().size();
	}

	public ItemStackModel setFireworkEffects(List<FireworkEffect> fireworkEffects) {
		if(getFireworkEffects() != fireworkEffects) {
			Validate.noNullElements(fireworkEffects, "Firework effects list may not be null/contain null elements");
			this.fireworkEffects.clear();
			this.fireworkEffects.addAll(fireworkEffects);
		}
		return this;
	}
	
	public ItemStackModel setFireworkEffects(FireworkEffect... fireworkEffects) {
		Validate.noNullElements(fireworkEffects, "Firework effects list may not be null/contain null elements");
		getFireworkEffects().clear();
		for(FireworkEffect fireworkEffect : fireworkEffects) {
			this.fireworkEffects.add(fireworkEffect);
		}
		return this;
	}
	
	public ItemStackModel addFireworkEffect(FireworkEffect fireworkEffect) {
		Validate.notNull(fireworkEffect, "Firework effects list may not be null/contain null elements");
		getFireworkEffects().add(fireworkEffect);
		return this;
	}
	
	public ItemStackModel addFireworkEffects(FireworkEffect fireworkEffect1, FireworkEffect... fireworkEffects) {
		Validate.noNullElements(fireworkEffects, "Firework effects list may not be null/contain null elements");
		Validate.notNull(fireworkEffect1, "Firework effects list may not be null/contain null elements");
		getFireworkEffects().add(fireworkEffect1);
		for(FireworkEffect fireworkEffect : fireworkEffects) {
			this.fireworkEffects.add(fireworkEffect);
		}
		return this;
	}
	
	public ItemStackModel addFireworkEffects(Collection<FireworkEffect> fireworkEffects) {
		Validate.noNullElements(fireworkEffects, "Firework effects list may not be null/contain null elements");
		getFireworkEffects().addAll(fireworkEffects);
		return this;
	}
	
	public ItemStackModel removeFireworkEffect(FireworkEffect fireworkEffect) {
		Validate.notNull(fireworkEffect, "Firework effects to remove may not be null/contain null elements");
		getFireworkEffects().remove(fireworkEffect);
		return this;
	}
	
	public ItemStackModel removeFireworkEffects(FireworkEffect fireworkEffect1, FireworkEffect... fireworkEffects) {
		Validate.noNullElements(fireworkEffects, "Firework effects to remove may not be null/contain null elements");
		Validate.notNull(fireworkEffect1, "Firework effects to remove may not be null/contain null elements");
		getFireworkEffects().remove(fireworkEffect1);
		for(FireworkEffect fireworkEffect : fireworkEffects) {
			this.fireworkEffects.remove(fireworkEffect);
		}
		return this;
	}
	
	public ItemStackModel removeFireworkEffects(Collection<FireworkEffect> fireworkEffects) {
		Validate.noNullElements(fireworkEffects, "Firework effects to remove may not be null/contain null elements");
		getFireworkEffects().removeAll(fireworkEffects);
		return this;
	}
	
	public ItemStackModel clearFireworkEffects() {
		getFireworkEffects().clear();
		return this;
	}

	public Set<NamespacedKey> getRecipes() {
		if(recipes == null) {
			recipes = new TypedSet<>(NamespacedKey.class);
		}
		return recipes;
	}

	public int recipeCount() {
		return getRecipes().size();
	}
	
	public ItemStackModel setRecipes(List<NamespacedKey> recipes) {
		Validate.noNullElements(recipes, "Recipes list may not be null/contain null elements");
		if(getRecipes() != recipes) {
			this.recipes.clear();
			this.recipes.addAll(recipes);
		}
		return this;
	}
	
	public ItemStackModel setRecipes(NamespacedKey... recipes) {
		Validate.noNullElements(recipes, "Recipes list may not be null/contain null elements");
		getRecipes().clear();
		for(NamespacedKey recipe : recipes) {
			this.recipes.add(recipe);
		}
		return this;
	}
	
	@SuppressWarnings("deprecation")
	private static NamespacedKey makeKey(String key) {
		int i = key.indexOf(':');
		if(i == -1)
			return NamespacedKey.minecraft(key);
		else
			return new NamespacedKey(key.substring(0, i), key.substring(i+1));
	}
	
	public ItemStackModel setRecipes(String... recipes) {
		Validate.notNull(recipes, "Recipes list may not be null/contain null elements");
		for(String recipe : recipes) {
			Validate.notEmpty(recipe, "Recipes list may not be null/contain null elements");
		}
		getRecipes().clear();
		for(String recipe : recipes) {
			this.recipes.add(makeKey(recipe));
		}
		return this;
	}
	
	public ItemStackModel addRecipe(NamespacedKey recipe) {
		Validate.notNull(recipe, "Recipes list may not be null/contain null elements");
		getRecipes().add(recipe);
		return this;
	}
	
	public ItemStackModel addRecipe(String recipe) {
		Validate.notEmpty(recipe, "Recipes list may not be null/contain null elements");
		getRecipes().add(makeKey(recipe));
		return this;
	}
	
	public ItemStackModel addRecipes(NamespacedKey recipe1, NamespacedKey... recipes) {
		Validate.noNullElements(recipes, "Recipes list may not be null/contain null elements");
		Validate.notNull(recipe1, "Recipes list may not be null/contain null elements");
		getRecipes().add(recipe1);
		for(NamespacedKey recipe : recipes) {
			this.recipes.add(recipe);
		}
		return this;
	}
	
	public ItemStackModel addRecipes(String recipe1, String... recipes) {
		Validate.notNull(recipes, "Recipes list may not be null/contain null elements");
		Validate.notEmpty(recipe1, "Recipes list may not be null/contain null elements");
		for(String recipe : recipes) {
			Validate.notEmpty(recipe, "Recipes list may not be null/contain null elements");
		}
		getRecipes().add(makeKey(recipe1));
		for(String recipe : recipes) {
			this.recipes.add(makeKey(recipe));
		}
		return this;
	}
	
	public ItemStackModel addRecipes(Collection<NamespacedKey> recipes) {
		Validate.noNullElements(recipes, "Recipes list may not be null/contain null elements");
		getRecipes().addAll(recipes);
		return this;
	}
	
	public ItemStackModel removeRecipe(NamespacedKey recipe) {
		Validate.notNull(recipe, "Recipes to remove may not be null/contain null elements");
		getRecipes().remove(recipe);
		return this;
	}
	
	public ItemStackModel removeRecipe(String recipe) {
		Validate.notEmpty(recipe, "Recipes to remove may not be null/contain null elements");
		getRecipes().remove(makeKey(recipe));
		return this;
	}
	
	public ItemStackModel removeRecipes(NamespacedKey recipe1, NamespacedKey... recipes) {
		Validate.noNullElements(recipes, "Recipes to remove may not be null/contain null elements");
		Validate.notNull(recipe1, "Recipes to remove may not be null/contain null elements");
		getRecipes().remove(recipe1);
		for(NamespacedKey recipe : recipes) {
			this.recipes.remove(recipe);
		}
		return this;
	}
	
	public ItemStackModel removeRecipes(String recipe1, String... recipes) {
		Validate.notNull(recipes, "Recipes to remove may not be null/contain null elements");
		Validate.notEmpty(recipe1, "Recipes to remove may not be null/contain null elements");
		for(String recipe : recipes) {
			Validate.notEmpty(recipe, "Recipes to remove may not be null/contain null elements");
		}
		getRecipes().remove(makeKey(recipe1));
		for(String recipe : recipes) {
			this.recipes.remove(makeKey(recipe));
		}
		return this;
	}
	
	public ItemStackModel removeRecipes(Collection<NamespacedKey> recipes) {
		Validate.noNullElements(recipes, "Recipes to remove may not be null/contain null elements");
		getRecipes().removeAll(recipes);
		return this;
	}
	
	public ItemStackModel clearRecipes() {
		getRecipes().clear();
		return this;
	}

	public Color getColor() {
		return color;
	}

	public ItemStackModel setColor(Color color) {
		this.color = color;
		return this;
	}

	public int getMapId() {
		return mapId;
	}

	public ItemStackModel setMapId(int mapId) {
		this.mapId = mapId;
		return this;
	}

	/**
	 * @return whether this map is scaled or not
	 */
	public boolean isScaling() {
		return scaling;
	}

	public ItemStackModel setScaling(boolean scaling) {
		this.scaling = scaling;
		return this;
	}

	/**
	 * @return this map's location name
	 */
	public String getLocationName() {
		return locationName;
	}

	public ItemStackModel setLocationName(String locationName) {
		this.locationName = locationName;
		return this;
	}

	public PotionData getBasePotion() {
		return basePotion;
	}

	public ItemStackModel setBasePotion(PotionData basePotion) {
		this.basePotion = basePotion;
		return this;
	}

	public List<PotionEffect> getPotionEffects() {
		if(potionEffects == null) {
			potionEffects = new TypedList<>(PotionEffect.class);
		}
		return potionEffects;
	}
	
	public int potionEffectCount() {
		return getPotionEffects().size();
	}

	public ItemStackModel setPotionEffects(List<PotionEffect> potionEffects) {
		Validate.noNullElements(potionEffects, "Potion effects list may not be null/contain null elements");
		if(getPotionEffects() != potionEffects) {
			this.potionEffects.clear();
			this.potionEffects.addAll(potionEffects);
		}
		return this;
	}
	
	public ItemStackModel setPotionEffects(PotionEffect... potionEffects) {
		Validate.noNullElements(potionEffects, "Potion effects list may not be null/contain null elements");
		getPotionEffects().clear();
		for(PotionEffect potionEffect : potionEffects) {
			this.potionEffects.add(potionEffect);
		}
		return this;
	}
	
	public ItemStackModel addPotionEffect(PotionEffect potionEffect) {
		Validate.notNull(potionEffect, "Potion effects list may not be null/contain null elements");
		getPotionEffects().add(potionEffect);
		return this;
	}
	
	public ItemStackModel addPotionEffects(PotionEffect potionEffect1, PotionEffect... potionEffects) {
		Validate.noNullElements(potionEffects, "Potion effects list may not be null/contain null elements");
		Validate.notNull(potionEffect1, "Potion effects list may not be null/contain null elements");
		getPotionEffects().add(potionEffect1);
		for(PotionEffect potionEffect : potionEffects) {
			this.potionEffects.add(potionEffect);
		}
		return this;
	}
	
	public ItemStackModel addPotionEffects(Collection<PotionEffect> potionEffects) {
		Validate.noNullElements(potionEffects, "Potion effects list may not be null/contain null elements");
		getPotionEffects().addAll(potionEffects);
		return this;
	}
	
	public ItemStackModel removePotionEffect(PotionEffect potionEffect) {
		Validate.notNull(potionEffect, "Potion effects to remove may not be null/contain null elements");
		getPotionEffects().removeIf(Predicate.isEqual(potionEffect));
		return this;
	}
	
	public ItemStackModel removePotionEffects(PotionEffect potionEffect1, PotionEffect... potionEffects) {
		Validate.noNullElements(potionEffects, "Potion effects to remove may not be null/contain null elements");
		Validate.notNull(potionEffect1, "Potion effects to remove may not be null/contain null elements");
		getPotionEffects().removeIf(Predicate.isEqual(potionEffect1));
		for(PotionEffect potionEffect : potionEffects) {
			this.potionEffects.removeIf(Predicate.isEqual(potionEffect));
		}
		return this;
	}
	
	public ItemStackModel removePotionEffects(Collection<PotionEffect> potionEffects) {
		Validate.noNullElements(potionEffects, "Potion effects to remove may not be null/contain null elements");
		getPotionEffects().removeAll(potionEffects);
		return this;
	}
	
	public ItemStackModel clearPotionEffects() {
		getPotionEffects().clear();
		return this;
	}

	public SkullOwner getSkullOwner() {
		return skullOwner;
	}

	public ItemStackModel setSkullOwner(SkullOwner skullOwner) {
		this.skullOwner = skullOwner;
		return this;
	}

	public DyeColor getPatternColor() {
		return patternColor == null? DyeColor.WHITE : patternColor;
	}

	public ItemStackModel setPatternColor(DyeColor patternColor) {
		this.patternColor = patternColor;
		return this;
	}

	public DyeColor getBodyColor() {
		return bodyColor == null? DyeColor.WHITE : bodyColor;
	}

	public ItemStackModel setBodyColor(DyeColor bodyColor) {
		this.bodyColor = bodyColor;
		return this;
	}

	public TropicalFish.Pattern getFish() {
		return fish == null? TropicalFish.Pattern.KOB : fish;
	}

	public ItemStackModel setFish(TropicalFish.Pattern fishPattern) {
		this.fish = fishPattern;
		return this;
	}
	
	
	@SuppressWarnings("deprecation")
	public void mergeWith(final ItemStack item) {
		final ItemMeta meta = item.getItemMeta();
		
		if(this.type == null) {
			this.type = item.getType();
		}
		
		if(this.count == 0 || this.count == 1) {
			count = item.getAmount();
		}
		
		if(name == null && meta.hasDisplayName()) {
			name = meta.getDisplayName();
		}
		
		if(isEmpty(lore) && meta.hasLore()) {
			setLore(meta.getLore());
		}
		
		if(damage == 0 && meta instanceof Damageable) {
			damage = ((Damageable) meta).getDamage();
		}
		
		unbreakable = meta.isUnbreakable();
		
		if(isEmpty(flags)) {
			getFlags().addAll(meta.getItemFlags());
		}
		
		if(isEmpty(enchantments) && meta.hasEnchants()) {
			addEnchantments(meta.getEnchants());
		}

		Collection<AttributeWrapper> otherAttributes = ItemUtil.getAttributes(item);
		if(isEmpty(attributes) && !isEmpty(otherAttributes)) {
			setAttributes(otherAttributes);
		}
		
		if(isBanner(this.type)) {
			BannerMeta bannerMeta = (BannerMeta)meta;
			if(isEmpty(patterns) && bannerMeta.numberOfPatterns() != 0) {
				setPatterns(bannerMeta.getPatterns());
			}
		} else if(this.type == SHIELD) {
			BlockStateMeta blockStateMeta = (BlockStateMeta)meta;
			Banner banner = (Banner)blockStateMeta.getBlockState();
			if(isEmpty(patterns) && banner.numberOfPatterns() != 0) {
				setPatterns(banner.getPatterns());
			}
		} else if(isReadableBook(this.type)) {
			BookMeta bookMeta = (BookMeta)meta;
			
			if(title == null && bookMeta.hasTitle()) {
				title = bookMeta.getTitle();
			}
			
			if(author == null && bookMeta.hasAuthor()) {
				author = bookMeta.getAuthor();
			}
	
			if(this.type == WRITTEN_BOOK && generation == null && bookMeta.hasGeneration()) {
				generation = bookMeta.getGeneration();
			}
			
			if(isEmpty(pages) && bookMeta.hasPages()) {
				setPages(bookMeta.getPages());
			}
		} else if(this.type == ENCHANTED_BOOK) {
			EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)meta;
			
			if(isEmpty(storedEnchantments) && bookMeta.hasStoredEnchants()) {
				addStoredEnchantments(bookMeta.getStoredEnchants());
			}
		} else if(this.type == FIREWORK_STAR) {
			FireworkEffectMeta fireworkMeta = (FireworkEffectMeta)meta;
			
			if(fireworkEffect == null && fireworkMeta.hasEffect()) {
				fireworkEffect = fireworkMeta.getEffect();
			}
		} else if(this.type == FIREWORK_ROCKET) {
			FireworkMeta fireworkMeta = (FireworkMeta)meta;
			
			if(isEmpty(fireworkEffects) && fireworkMeta.hasEffects()) {
				setFireworkEffects(fireworkMeta.getEffects());
			}
		} else if(this.type == KNOWLEDGE_BOOK) {
			KnowledgeBookMeta bookMeta = (KnowledgeBookMeta)meta;
			
			if(isEmpty(recipes) && bookMeta.hasRecipes()) {
				addRecipes(bookMeta.getRecipes());
			}
		} else if(isLeatherArmor(this.type)) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta)meta;
			Color clr = armorMeta.getColor();
			Color defaultLeatherColor = Bukkit.getItemFactory().getDefaultLeatherColor();
			
			if((color == null || color.equals(defaultLeatherColor)) 
					&& !clr.equals(defaultLeatherColor)) {
				color = clr;
			}
			
		} else if(this.type == FILLED_MAP) {
			MapMeta mapMeta = (MapMeta)meta;
			
			if(color == null && mapMeta.hasColor()) {
				color = mapMeta.getColor();
			}
			
			mapId = mapMeta.getMapId();
			
			scaling = mapMeta.isScaling();
			
			if(locationName == null && mapMeta.hasLocationName()) {
				locationName = mapMeta.getLocationName();
			}
			
		} else if(canHavePotionEffects(this.type) ) {
			PotionMeta potionMeta = (PotionMeta)meta;
			
			if(basePotion == null) {
				basePotion = potionMeta.getBasePotionData();
			}
						
			if(isEmpty(potionEffects) && potionMeta.hasCustomEffects()) {
				setPotionEffects(potionMeta.getCustomEffects());
			}
		} else if(this.type == PLAYER_HEAD ) {
			SkullMeta skullMeta = (SkullMeta)meta;
			if(skullOwner == null && skullMeta.hasOwner()) {
				String texture = ItemUtil.getTextureValue(item);
				skullOwner = new TexturedSkullOwner(skullMeta.getOwningPlayer().getUniqueId(), texture);
			}
		} else if(this.type == TROPICAL_FISH_BUCKET) {
			TropicalFishBucketMeta bucketMeta = (TropicalFishBucketMeta)meta;
			
			if(patternColor == null) {
				patternColor = bucketMeta.getPatternColor();
			}
			
			if(bodyColor == null) {
				bodyColor = bucketMeta.getBodyColor();
			}
			
			if(fish == null) {
				fish = bucketMeta.getPattern();
			}
		}
	}
	
	public static final int MATCH_COUNT = 1,
							MATCH_NAME  = 1 << 1,
							MATCH_LORE = 1 << 2,
							EXACT_MATCH_LORE = 1 << 3,
							MATCH_UNBREAKABLE = 1 << 4,
							MATCH_FLAGS = 1 << 5,
							MATCH_ENCHANTMENTS = 1 << 6,
							EXACT_MATCH_ENCHANTMENT_LEVELS = 1 << 7,
							MATCH_ATTRIBUTES = 1 << 8,
							MATCH_PATTERNS = 1 << 9,
							MATCH_WRITTEN_BOOK_FIELDS = 1 << 10,
							MATCH_WRITTEN_BOOK_PAGES = 1 << 11,
							MATCH_STORED_ENCHANTMENTS = 1 << 12,
							EXACT_MATCH_STORED_ENCHANTMENT_LEVELS = 1 << 13,
							MATCH_FIREWORK_EFFECTS = 1 << 14,
							MATCH_LEATHER_COLOR = 1 << 15,
							MATCH_RECIPES = 1 << 16,
							MATCH_MAP_COLOR = 1 << 17,
							MATCH_MAP_LOCATION_NAME = 1 << 18,
							MATCH_MAP_SCALING = 1 << 19,
							MATCH_POTION_EFFECTS = 1 << 20,
							MATCH_POTION_COLOR = 1 << 21,
							MATCH_PLAYER_HEAD_OWNER = 1 << 22,
							MATCH_PLAYER_HEAD_TEXTURE = 1 << 23,
							MATCH_FISH_BUCKET_TYPE = 1 << 24,
							MATCH_FISH_BUCKET_BODY_COLOR = 1 << 25,
							MATCH_FISH_BUCKET_PATTERN_COLOR = 1 << 26,
							MATCH_DAMAGE = 1 << 27,
							MATCH_DAMAGE_MIN = 1 << 28
							;
	
	private static boolean hasFlag(int flags, int flag) {
		return (flags & flag) == flag;
	}
	
	public boolean matches(final ItemStack item) {
		return matches(item, 
			MATCH_UNBREAKABLE | MATCH_NAME | MATCH_LORE
			| MATCH_FLAGS | MATCH_ENCHANTMENTS
			| MATCH_PATTERNS | MATCH_WRITTEN_BOOK_FIELDS
			| MATCH_WRITTEN_BOOK_PAGES);
	}
	
	@SuppressWarnings("deprecation")
	public boolean matches(final ItemStack item, final int matchFlags) {
		if(this.type != item.getType())
			return false;
		if(hasFlag(matchFlags, MATCH_COUNT) && count != item.getAmount())
			return false;
		final ItemMeta meta = item.getItemMeta();
		if(hasFlag(matchFlags, MATCH_DAMAGE) && meta instanceof Damageable) {
			int theirDamage = ((Damageable) meta).getDamage();
			if(hasFlag(matchFlags, MATCH_DAMAGE_MIN)) {
				if(theirDamage < damage)
					return false;
			} else {
				if(theirDamage != damage)
					return false;
			}
		}
		if(hasFlag(matchFlags, MATCH_UNBREAKABLE) && unbreakable && !meta.isUnbreakable())
			return false;
		if(hasFlag(matchFlags, MATCH_NAME) && name != null && meta.hasDisplayName() && !name.equals(meta.getDisplayName()))
			return false;
		if(hasFlag(matchFlags, MATCH_LORE) && !isEmpty(lore) && meta.hasLore()) {
			if(hasFlag(matchFlags, EXACT_MATCH_LORE))
				return !lore.equals(meta.getLore());
			List<String> otherLore = meta.getLore();
			if(lore.size() == 1)
				return otherLore.contains(lore.get(0));
			int i = otherLore.indexOf(lore.get(0));
			if(i == -1) return false;
			i++;
			for(int j = 1; j < lore.size(); i++, j++) {
				if(i >= otherLore.size()
						|| !lore.get(j).equals(otherLore.get(i)))
					return false;
			}
		}
		if(hasFlag(matchFlags, MATCH_FLAGS) && !isEmpty(flags)) {
			for(ItemFlag flag : flags) {
				if(!meta.hasItemFlag(flag))
					return false;
			}
		}
		if(hasFlag(matchFlags, MATCH_ENCHANTMENTS) && !isEmpty(enchantments)) {
			boolean exactMatchLevel = hasFlag(matchFlags, EXACT_MATCH_ENCHANTMENT_LEVELS);
			for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
				Enchantment ench = entry.getKey();
				if(!meta.hasEnchant(ench))
					return false;
				int lvl = entry.getValue();
				if(exactMatchLevel) {
					if(lvl != meta.getEnchantLevel(ench))
						return false;
				} else {
					if(lvl > meta.getEnchantLevel(ench))
						return false;
				}
			}
		}
		if(hasFlag(matchFlags, MATCH_ATTRIBUTES) && !isEmpty(attributes)) {
			Collection<AttributeWrapper> otherAttributes = ItemUtil.getAttributes(item);
			for(AttributeWrapper myAttribute : attributes) {
				boolean found = false;
				for(AttributeWrapper otherAttribute : otherAttributes) {
					if(attributeCompare(myAttribute, otherAttribute)) {
						found = true;
						break;
					}
				}
				if(!found)
					return false;
			}
		}
		if(this.type == SHIELD && hasFlag(matchFlags, MATCH_PATTERNS)) {
			BlockStateMeta blockStateMeta = (BlockStateMeta)meta;
			Banner banner = (Banner)blockStateMeta.getBlockState();
			if(!patterns.equals(banner.getPatterns()))
				return false;
		} else if(isBanner(this.type) && hasFlag(matchFlags, MATCH_PATTERNS)) {
			BannerMeta bannerMeta = (BannerMeta)meta;
			if(!patterns.equals(bannerMeta.getPatterns()))
				return false;
		} else if(isReadableBook(this.type)) {
			BookMeta bookMeta = (BookMeta)meta;
			if(hasFlag(matchFlags, MATCH_WRITTEN_BOOK_FIELDS)) {
				if(title != null && !title.equals(bookMeta.getTitle()))
					return false;
				if(author != null && !author.equals(bookMeta.getAuthor()))
					return false;
				if(generation != null && generation != bookMeta.getGeneration())
					return false;
			}
			if(hasFlag(matchFlags, MATCH_WRITTEN_BOOK_PAGES)) {
				if(!getPages().equals(bookMeta.getPages()))
					return false;
			}
		} else if(this.type == ENCHANTED_BOOK && hasFlag(matchFlags, MATCH_STORED_ENCHANTMENTS) && !isEmpty(storedEnchantments)) {
			EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)meta;
			boolean exactMatchLevel = hasFlag(matchFlags, EXACT_MATCH_STORED_ENCHANTMENT_LEVELS);
			for(Map.Entry<Enchantment, Integer> entry : storedEnchantments.entrySet()) {
				Enchantment ench = entry.getKey();
				if(!bookMeta.hasStoredEnchant(ench))
					return false;
				int lvl = entry.getValue();
				if(exactMatchLevel) {
					if(lvl != bookMeta.getStoredEnchantLevel(ench))
						return false;
				} else {
					if(lvl > bookMeta.getStoredEnchantLevel(ench))
						return false;
				}
			}
		} else if(this.type == FIREWORK_STAR && hasFlag(matchFlags, MATCH_FIREWORK_EFFECTS)) {
			FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) meta;

			if(fireworkEffect != null && !fireworkEffect.equals(fireworkMeta.getEffect()))
				return false;
		} else if(this.type == FIREWORK_ROCKET && hasFlag(matchFlags, MATCH_FIREWORK_EFFECTS)) {
			FireworkMeta fireworkMeta = (FireworkMeta)meta;
			
			if(!isEmpty(fireworkEffects) && !fireworkEffects.equals(fireworkMeta.getEffects()))
				return false;
		} else if(isLeatherArmor(this.type) && hasFlag(matchFlags, MATCH_LEATHER_COLOR)) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta)meta;
			if(color != null && !color.equals(armorMeta.getColor()))
				return false;
		} else if(this.type == KNOWLEDGE_BOOK && hasFlag(matchFlags, MATCH_RECIPES)) {
			KnowledgeBookMeta bookMeta = (KnowledgeBookMeta)meta;
			if(!isEmpty(recipes) && !bookMeta.getRecipes().containsAll(recipes))
				return false;
		} else if(this.type == FILLED_MAP) {
			MapMeta mapMeta = (MapMeta)meta;
			if(mapId != mapMeta.getMapId())
				return false;
			if(hasFlag(matchFlags, MATCH_MAP_LOCATION_NAME) && locationName != null && !locationName.equals(mapMeta.getLocationName()))
				return false;
			if(hasFlag(matchFlags, MATCH_MAP_COLOR) && color != null && !color.equals(mapMeta.getColor()))
				return false;
			if(hasFlag(matchFlags, MATCH_MAP_SCALING) && scaling != mapMeta.isScaling())
				return false;
		} else if(canHavePotionEffects(this.type) ) {
			PotionMeta potionMeta = (PotionMeta)meta;
			
			if(hasFlag(matchFlags, MATCH_POTION_EFFECTS)) {
				if(basePotion != null && !basePotion.equals(potionMeta.getBasePotionData()))
					return false;

				if(!isEmpty(potionEffects) && !potionMeta.getCustomEffects().containsAll(potionEffects))
					return false;
			}
			if(hasFlag(matchFlags, MATCH_POTION_COLOR) && color != null && !color.equals(potionMeta.getColor()))
					return false;
		} else if(this.type == PLAYER_HEAD) {
			SkullMeta skullMeta = (SkullMeta)meta;
			if(skullOwner instanceof PlayerSkullOwner) {
				PlayerSkullOwner skullOwner = (PlayerSkullOwner)this.skullOwner;
				if(hasFlag(matchFlags, MATCH_PLAYER_HEAD_OWNER) && !skullOwner.getOwningPlayer().getUniqueId().equals(skullMeta.getOwningPlayer()))
					return false;
			} else {
				TexturedSkullOwner skullOwner = (TexturedSkullOwner)this.skullOwner;
				String texture = ItemUtil.getTextureValue(item);
				if(hasFlag(matchFlags, MATCH_PLAYER_HEAD_TEXTURE) && !skullOwner.getTexture().equals(texture))
					return false;
				if(hasFlag(matchFlags, MATCH_PLAYER_HEAD_OWNER) && !skullOwner.getUniqueId().equals(skullMeta.getOwningPlayer().getUniqueId()) && !skullOwner.getUniqueId().toString().equals(skullMeta.getOwner()))
					return false;
			}
		} else if(this.type == TROPICAL_FISH_BUCKET) {
			TropicalFishBucketMeta bucketMeta = (TropicalFishBucketMeta)meta;
			
			if(bucketMeta.hasVariant()) {
				if(hasFlag(matchFlags, MATCH_FISH_BUCKET_PATTERN_COLOR) && patternColor != null && patternColor != bucketMeta.getPatternColor())
					return false;
				if(hasFlag(matchFlags, MATCH_FISH_BUCKET_BODY_COLOR) && bodyColor != null && bodyColor != bucketMeta.getBodyColor())
					return false;
				if(hasFlag(matchFlags, MATCH_FISH_BUCKET_TYPE) && fish != null && fish != bucketMeta.getPattern())
					return false;
			} else {
				if(hasFlag(matchFlags, MATCH_FISH_BUCKET_PATTERN_COLOR) && patternColor != null)
					return false;
				if(hasFlag(matchFlags, MATCH_FISH_BUCKET_BODY_COLOR) && bodyColor != null)
					return false;
				if(hasFlag(matchFlags, MATCH_FISH_BUCKET_TYPE) && fish != null)
					return false;
			}
		}
		return true;
	}
	
	public boolean equals(final ItemStack item) {
		return equals(item, true);
	}
	
	@SuppressWarnings("deprecation")
	public boolean equals(final ItemStack item, final boolean matchCount) {
		if(this.type != item.getType())
			return false;
		if(matchCount && count != item.getAmount())
			return false;
		final ItemMeta meta = item.getItemMeta();
		if(unbreakable != meta.isUnbreakable())
			return false;
		if(!Objects.equals(name, meta.getDisplayName()))
			return false;
		if(isEmpty(lore)) {
			if(meta.hasLore())
				return false;
		} else if(!meta.hasLore() || !lore.equals(meta.getLore())) {
			return false;
		}
		if(!getFlags().equals(meta.getItemFlags())) {
			return false;
		}
		if(!getEnchantments().equals(meta.getEnchants()))
			return false;
		{
			Collection<AttributeWrapper> otherAttributes = ItemUtil.getAttributes(item);
			if(isEmpty(attributes)) {
				if(!otherAttributes.isEmpty())
					return false;
			} else {
				if(attributes.size() != otherAttributes.size())
					return false;
				
				for(AttributeWrapper myAttribute : attributes) {
					boolean found = false;
					for(AttributeWrapper otherAttribute : otherAttributes) {
						if(attributeCompare(myAttribute, otherAttribute)) {
							found = true;
							break;
						}
					}
					if(!found)
						return false;
				}
			}
		}
		if(this.type == SHIELD) {
			BlockStateMeta blockStateMeta = (BlockStateMeta)meta;
			Banner banner = (Banner)blockStateMeta.getBlockState();
			if(!patterns.equals(banner.getPatterns()))
				return false;
		} else if(isBanner(this.type)) {
			BannerMeta bannerMeta = (BannerMeta)meta;
			if(!patterns.equals(bannerMeta.getPatterns()))
				return false;
		} else if(isReadableBook(this.type)) {
			BookMeta bookMeta = (BookMeta)meta;
			if(!Objects.equals(title, bookMeta.getTitle()))
				return false;
			if(!Objects.equals(author, bookMeta.getAuthor()))
				return false;
			if(generation != bookMeta.getGeneration())
				return false;
			if(!getPages().equals(bookMeta.getPages()))
				return false;
		} else if(this.type == ENCHANTED_BOOK) {
			EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)meta;
			if(!getStoredEnchantments().equals(bookMeta.getStoredEnchants()))
				return false;
		} else if(this.type == FIREWORK_STAR) {
			FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) meta;
			if(!Objects.equals(fireworkEffect, fireworkMeta.getEffect()))
				return false;
		} else if(this.type == FIREWORK_ROCKET) {
			FireworkMeta fireworkMeta = (FireworkMeta)meta;
			if(!getFireworkEffects().equals(fireworkMeta.getEffects()))
				return false;
		} else if(isLeatherArmor(this.type)) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta)meta;
			if(color == null) {
				if(!Bukkit.getItemFactory().getDefaultLeatherColor().equals(armorMeta.getColor()))
					return false;
			} else if(!color.equals(armorMeta.getColor()))
				return false;
		} else if(this.type == KNOWLEDGE_BOOK) {
			KnowledgeBookMeta bookMeta = (KnowledgeBookMeta)meta;
			if(!bookMeta.getRecipes().containsAll(getRecipes())
					|| !getRecipes().containsAll(bookMeta.getRecipes()))
				return false;
		} else if(this.type == FILLED_MAP) {
			MapMeta mapMeta = (MapMeta)meta;
			if(mapId != mapMeta.getMapId())
				return false;
			if(!Objects.equals(locationName, mapMeta.getLocationName()))
				return false;
			if(color == null) {
				if(mapMeta.hasColor())
					return false;
			} else if(!mapMeta.hasColor() || !color.equals(mapMeta.getColor()))
				return false;
			if(scaling != mapMeta.isScaling())
				return false;
		} else if(canHavePotionEffects(this.type) ) {
			PotionMeta potionMeta = (PotionMeta)meta;
			if(basePotion == null) {
				PotionData potionData = potionMeta.getBasePotionData();
				if(potionData.isExtended() || potionData.isUpgraded() 
						|| potionData.getType() != PotionType.UNCRAFTABLE)
					return false;
			} else if(!basePotion.equals(potionMeta.getBasePotionData()))
				return false;
			if(!potionMeta.getCustomEffects().containsAll(getPotionEffects())
					|| !getPotionEffects().containsAll(potionMeta.getCustomEffects()))
				return false;
			if(color == null) {
				if(potionMeta.hasColor())
					return false;
			} else if(!potionMeta.hasColor() || !color.equals(potionMeta.getColor()))
				return false;
		} else if(this.type == PLAYER_HEAD) {
			SkullMeta skullMeta = (SkullMeta)meta;
			if(skullOwner instanceof PlayerSkullOwner) {
				PlayerSkullOwner skullOwner = (PlayerSkullOwner)this.skullOwner;
				if(!skullOwner.getOwningPlayer().getUniqueId().equals(skullMeta.getOwningPlayer()))
					return false;
			} else {
				TexturedSkullOwner skullOwner = (TexturedSkullOwner)this.skullOwner;
				String texture = ItemUtil.getTextureValue(item);
				if(!Objects.equals(texture, skullOwner.getTexture()))
					return false;
				if(!skullMeta.getOwningPlayer().getUniqueId().equals(skullOwner.getUniqueId()))
					return false;
				try {
					if(!skullOwner.getUniqueId().toString().equals(skullMeta.getOwner()))
						return false;
				} catch(NullPointerException e) {
					return false;
				}
			}
		} else if(this.type == TROPICAL_FISH_BUCKET) {
			TropicalFishBucketMeta bucketMeta = (TropicalFishBucketMeta)meta;
			if(bucketMeta.hasVariant()) {
				if(getPatternColor() != bucketMeta.getPatternColor())
					return false;
				if(getBodyColor() != bucketMeta.getBodyColor())
					return false;
				if(getFish() != bucketMeta.getPattern())
					return false;
			}
		}
		return true;
	}

	public boolean equals(final ItemStackModel item, final boolean matchCount) {
		if(this.type != item.type)
			return false;
		if(matchCount && count != item.count)
			return false;
		if(unbreakable != item.unbreakable)
			return false;
		if(!Objects.equals(name, item.name))
			return false;
		if(!getLore().equals(item.getLore()))
			return false;
		if(!getFlags().equals(item.getFlags()))
			return false;
		if(!getEnchantments().equals(item.getEnchantments()))
			return false;
		if(isEmpty(attributes)) {
			if(!isEmpty(item.attributes))
				return false;
		} else if(isEmpty(item.attributes)) {
			return false;
		} else {
			if(attributes.size() != item.attributes.size())
				return false;
			for(AttributeWrapper myAttribute : attributes) {
				boolean found = false;
				for(AttributeWrapper otherAttribute : item.attributes) {
					if(attributeCompare(myAttribute, otherAttribute)) {
						found = true;
						break;
					}
				}
				if(!found)
					return false;
			}
		}
		if(!getPatterns().equals(item.getPatterns()))
			return false;
		if(!Objects.equals(title, item.title))
			return false;
		if(!Objects.equals(author, item.author))
			return false;
		if(generation != item.generation)
			return false;
		if(!getPages().equals(item.getPages()))
			return false;
		if(!getStoredEnchantments().equals(item.getStoredEnchantments()))
			return false;
		if(!Objects.equals(fireworkEffect, item.fireworkEffect))
			return false;
		if(!getFireworkEffects().equals(item.getFireworkEffects()))
			return false;
		if(!Objects.equals(color,  item.color))
			return false;
		if(!getRecipes().equals(item.getRecipes()))
			return false;
		if(mapId != item.mapId)
			return false;
		if(!Objects.equals(locationName, item.locationName))
			return false;
		if(scaling != item.scaling)
			return false;
		if(!Objects.equals(basePotion, item.basePotion))
			return false;
		if(!getPotionEffects().containsAll(item.getPotionEffects())
				|| !item.potionEffects.containsAll(potionEffects))
			return false;
		if(!Objects.equals(skullOwner, item.skullOwner))
			return false;
		if(patternColor != item.patternColor)
			return false;
		if(bodyColor != item.bodyColor)
			return false;
		if(fish != item.fish)
			return false;
		return true;
	}
	
	private static boolean attributeCompare(AttributeWrapper a1, AttributeWrapper a2) {
		EnumSet<InternalSlot> s1 = EnumSet.noneOf(InternalSlot.class), s2 = EnumSet.noneOf(InternalSlot.class);
		s1.addAll(a1.getSlots());
		s2.addAll(a2.getSlots());		
		return a1.getAttribute() == a2.getAttribute()
				&& a1.getOperation() == a2.getOperation()
				&& Objects.equals(a1.getName(), a2.getName())
				&& a1.getAmount() == a2.getAmount()
				&& s1.equals(s2);
	}
	
	@Override
	public ItemStackModel clone() {
		try {
			ItemStackModel result = (ItemStackModel) super.clone();
			if(result.lore != null)
				result.lore = new TypedList<>(result.lore);
			if(result.flags != null)
				result.flags = result.flags.clone();
			if(result.enchantments != null)
				result.enchantments = new TypedMap<>(result.enchantments);
			if(result.attributes != null)
				result.attributes = new TypedList<>(result.attributes);
			if(result.patterns != null)
				result.patterns = new TypedList<>(result.patterns);
			if(result.pages != null)
				result.pages = new TypedList<>(result.pages);
			if(result.storedEnchantments != null)
				result.storedEnchantments = new TypedMap<>(result.storedEnchantments);
			if(result.fireworkEffects != null)
				result.fireworkEffects = new TypedList<>(result.fireworkEffects);
			if(result.recipes != null)
				result.recipes = new TypedSet<>(result.recipes);
			if(result.potionEffects != null)
				result.potionEffects = new TypedList<>(result.potionEffects);
			return result;
		} catch(CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] toArray(Collection<T> list, Class<T> type) {
		return list.toArray((T[])Array.newInstance(type, list.size()));
	}
	
	public static boolean isEmpty(Collection<?> c) {
		return c == null || c.isEmpty();
	}
	
	public static boolean isEmpty(Map<?,?> m) {
		return m == null || m.isEmpty();
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItemStack() {
		mergeCustomItem();
		ItemStack item = new ItemStack(this.type, count <= 0? 1 : count);
		ItemMeta meta = item.getItemMeta();
		
		if(name != null) {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		}
		
		if(!isEmpty(lore)) {
			for(int i = 0; i < lore.size(); i++) {
				lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
			}
			meta.setLore(lore);
		}
		
		meta.setUnbreakable(unbreakable);
		
		if(!isEmpty(flags)) {
			meta.addItemFlags(toArray(flags, ItemFlag.class));
		}
		
		if(!isEmpty(enchantments)) {
			try {
				for(Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
					meta.addEnchant(entry.getKey(), entry.getValue(), true);
				}
			} catch(NullPointerException e) {
				Bukkit.getLogger().log(Level.SEVERE, "NullPointerException in enchantments: " + enchantments, e);
			}
		}
		
		if(!isEmpty(attributes)) {
			item.setItemMeta(meta);
			for(AttributeWrapper attribute : attributes) {
				item = attribute.applyTo(item);
			}
			meta = item.getItemMeta();
		}
		
		if(isBanner(this.type)) {
			BannerMeta bannerMeta = (BannerMeta)meta;
			if(!isEmpty(patterns)) {
				bannerMeta.setPatterns(patterns);
			}
		} else if(this.type == SHIELD) {
			BlockStateMeta blockStateMeta = (BlockStateMeta)meta;
			Banner banner = (Banner)blockStateMeta.getBlockState();
			if(!isEmpty(patterns)) {
				banner.setPatterns(patterns);
			}
			if(shieldColor != null) {
				banner.setBaseColor(shieldColor);
			}
			blockStateMeta.setBlockState(banner);
		}
		
		if(isReadableBook(this.type)) {
			BookMeta bookMeta = (BookMeta)meta;
			
			if(title != null) {
				bookMeta.setTitle(title);
			}
			
			if(author != null) {
				bookMeta.setAuthor(author);
			}
	
			if(this.type == WRITTEN_BOOK && generation != null) {
				bookMeta.setGeneration(generation);
			}
			
			if(!isEmpty(pages)) {
				bookMeta.setPages(pages);
			}
		}
		
		if(this.type == ENCHANTED_BOOK && !isEmpty(storedEnchantments)) {
			EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)meta;
			
			for(Entry<Enchantment, Integer> entry : storedEnchantments.entrySet()) {
				bookMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
			}
		}
		
		if(this.type == FIREWORK_STAR && fireworkEffect != null) {
			FireworkEffectMeta fireworkMeta = (FireworkEffectMeta)meta;
			
			fireworkMeta.setEffect(fireworkEffect);
		}
		
		if(this.type == FIREWORK_ROCKET) {
			FireworkMeta fireworkMeta = (FireworkMeta)meta;
			
			fireworkMeta.setPower(power);
			
			if(!isEmpty(fireworkEffects)) {
				fireworkMeta.addEffects(fireworkEffects);
			}
		}
		
		if(this.type == KNOWLEDGE_BOOK && !isEmpty(recipes)) {
			KnowledgeBookMeta bookMeta = (KnowledgeBookMeta)meta;
			
			bookMeta.addRecipe(recipes.toArray());
		}
		
		if(isLeatherArmor(this.type) && color != null) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta)meta;
			
			armorMeta.setColor(color);
			
		} else if(this.type == FILLED_MAP) {
			MapMeta mapMeta = (MapMeta)meta;
			
			if(color != null) {
				mapMeta.setColor(color);
			}
			
			mapMeta.setMapId(mapId);
			mapMeta.setScaling(scaling);
			
			if(locationName != null) {
				mapMeta.setLocationName(locationName);
			}
			
		} else if(canHavePotionEffects(this.type) ) {
			PotionMeta potionMeta = (PotionMeta)meta;
			
			if(basePotion != null) {
				potionMeta.setBasePotionData(basePotion);
			}
			
			if(!isEmpty(potionEffects)) {
				for(PotionEffect potionEffect : potionEffects) {
					potionMeta.addCustomEffect(potionEffect, true);
				}
			}
			
			if(color != null) {
				potionMeta.setColor(color);
			}
		} else if(this.type == PLAYER_HEAD && skullOwner != null) {
			item.setItemMeta(meta);
			item = skullOwner.applyTo(item, (SkullMeta)meta);
			meta = item.getItemMeta();
		} else if(this.type == TROPICAL_FISH_BUCKET) {
			TropicalFishBucketMeta bucketMeta = (TropicalFishBucketMeta)meta;
			
			if(patternColor != null) {
				bucketMeta.setPatternColor(patternColor);
			}
			
			if(bodyColor != null) {
				bucketMeta.setBodyColor(bodyColor);
			}
			
			if(fish != null) {
				bucketMeta.setPattern(fish);
			}
		}
		
		item.setItemMeta(meta);
		return item;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(obj instanceof ItemStack)
			return this.equals((ItemStack) obj);
		if(!(obj instanceof ItemStackModel))
			return false;
		return equals(obj);
	}
	
	@Override
	public int hashCode() {
		return type.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append('{')
		  .append("item=").append(type.getKey()).append(',')
		  .append("count=").append(count).append(',')
		  .append("name=").append(stringify(name)).append(',')
		  .append("lore=[").append(StringUtils.join(getLore(), ",", ItemStackModel::stringify)).append("],")
		  .append("unbreakable=").append(unbreakable).append(',')
		  .append("flags=[").append(StringUtils.join(getFlags(), ",", flag -> flag.name().toLowerCase())).append("],")
		  .append("enchantments={").append(StringUtils.join(getEnchantments().entrySet(), ",", ItemStackModel::enchantmentString)).append("},")
		  .append("attributes=[").append(StringUtils.join(getAttributes(), ",", ItemStackModel::attributeString)).append("],")
		  .append("patterns=[").append(StringUtils.join(getPatterns(), ",", pattern -> String.format("{id=%s,color=%s}", pattern.getPattern().getIdentifier(), pattern.getColor().name().toLowerCase()))).append("],")
		  .append("shieldColor=").append(shieldColor == null? "null" : shieldColor.name().toLowerCase()).append(',')
		  .append("title=").append(stringify(title)).append(',')
		  .append("author=").append(stringify(author)).append(',')
		  .append("pages=[").append(StringUtils.join(getPages(), ",", ItemStackModel::stringify)).append("],")
		  .append("generation=").append(generation == null? "null" : generation.name().toLowerCase()).append(',')
		  .append("storedEnchantments={").append(StringUtils.join(getStoredEnchantments().entrySet(), ",", ItemStackModel::enchantmentString)).append("},")
		  .append("fireworkEffect=").append(fireworkEffectString(fireworkEffect)).append(',')
		  .append("power=").append(power).append(',')
		  .append("fireworkEffects=[").append(StringUtils.join(getFireworkEffects(), ",", ItemStackModel::fireworkEffectString)).append("],")
		  .append("recipes=").append(getRecipes()).append(',')
		  .append("color=").append(colorString(color)).append(',')
		  .append("mapId=").append(mapId).append(',')
		  .append("scaling=").append(scaling).append(',')
		  .append("locationName=").append(stringify(locationName)).append(',')
		  .append("basePotion=").append(basePotion == null? "null" : String.format("{type=%s,extended=%b,upgraded=%b}", basePotion.getType().name().toLowerCase(), basePotion.isExtended(), basePotion.isUpgraded())).append(',')
		  .append("potionEffects=[").append(StringUtils.join(getPotionEffects(), ",", ItemStackModel::potionEffectString)).append("],")
		  .append("skullOwner=").append(skullOwner).append(',')
		  .append("patternColor=").append(patternColor == null? "null" : patternColor.name().toLowerCase()).append(',')
		  .append("bodyColor=").append(bodyColor == null? "null" : bodyColor.name().toLowerCase()).append(',')
		  .append("fish=").append(fish == null? "null" : fish.name().toLowerCase()).append('}');
		return sb.toString();
	}
	
	private static String potionEffectString(PotionEffect effect) {
		if(effect == null) return "null";
		return PotionEffectTypeTypeAdapter.byType.get(effect.getType()) + (effect.isAmbient()? ":(" : ":") + effect.getDuration() + "t-x" + effect.getAmplifier() + (effect.isAmbient()? ")" : ""); 
	}
	
	private static String fireworkEffectString(FireworkEffect effect) {
		if(effect == null) return "null";
		return String.format("{type=%s,flicker=%b,trail=%b,colors=[%s],fadeColors=[%s]}", 
				effect.getType().name().toLowerCase(), 
				effect.hasFlicker(),
				effect.hasTrail(),
				StringUtils.join(effect.getColors(), ",", ItemStackModel::colorString),
				StringUtils.join(effect.getFadeColors(), ",", ItemStackModel::colorString));
	}
	
	private static String colorString(Color color) {
		return color == null? "null" : String.format("#%06x", color.asRGB());
	}
	
	private static String enchantmentString(Map.Entry<Enchantment, Integer> entry) {
		return entry.getKey().getKey() + "=" + entry.getValue();
	}
	
	private static String attributeString(AttributeWrapper attr) {
		return String.format("{attribute=%s,operation=%s,name=%s,amount=%s,slots=[%s]}", 
				attr.getAttribute().getInternal(), 
				attr.getOperation().name().toLowerCase(), 
				stringify(attr.getName()), 
				attr.getAmount(), 
				StringUtils.join(attr.getSlots(), ",", slot -> slot.getInternal()));
	}
	
	private static String stringify(String str) {
		if(str == null) return "null";
		str = StringEscapeUtils.escapeJava(str);
		for(int i = 0, charCount; i < str.length(); i += charCount) {
			int c = str.codePointAt(i);
			if(Character.isWhitespace(c) || c == ',')
				return '"' + str + '"';
			charCount = Character.charCount(c);
		}
		return str;
	}
	
	public static boolean canHavePotionEffects(Material material) {
		switch(material) {
		case POTION:
		case SPLASH_POTION:
		case LINGERING_POTION:
		case TIPPED_ARROW:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isReadableBook(Material material) {
		switch(material) {
		case WRITABLE_BOOK:
		case WRITTEN_BOOK:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isLeatherArmor(Material material) {
		switch(material) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isBanner(Material material) {
		switch(material) {
		case WHITE_BANNER:
		case ORANGE_BANNER:
		case MAGENTA_BANNER:
		case LIGHT_BLUE_BANNER:
		case YELLOW_BANNER:
		case LIME_BANNER:
		case PINK_BANNER:
		case GRAY_BANNER:
		case LIGHT_GRAY_BANNER:
		case CYAN_BANNER:
		case PURPLE_BANNER:
		case BLUE_BANNER:
		case BROWN_BANNER:
		case GREEN_BANNER:
		case RED_BANNER:
		case BLACK_BANNER:
			return true;
		default:
			return false;
		}
	}
	
	public static final List<String> ENCHANTMENT_NAMES;
	public static final List<NamespacedKey> ENCHANTMENT_KEYS;
	public static final List<Enchantment> ENCHANTMENT_INSTANCES;
	
	static {
		ENCHANTMENT_NAMES = Collections.unmodifiableList(Arrays.asList(new String[] {
				"Protection",
				"Fire Protection",
				"Feather Falling",
				"Blast Protection",
				"Projectile Protection",
				"Thorns",
				"Respiration",
				"Depth Strider",
				"Aqua Affinity",
				"Sharpness",
				"Smite",
				"Bane of Arthropods",
				"Knockback",
				"Fire Aspect",
				"Looting",
				"Efficiency",
				"Silk Touch",
				"Unbreaking",
				"Fortune",
				"Power",
				"Punch",
				"Flame",
				"Infinity",
				"Luck of the Sea",
				"Lure",
				"Frost Walker",
				"Mending",
				"Curse of Binding",
				"Curse of Vanishing",
				"Impaling",
				"Riptide",
				"Loyalty",
				"Channeling",
				"Sweeping Edge",
				/*"Multishot",
				"Piercing",
				"Quick Charge"*/
		}));
		ENCHANTMENT_KEYS = Collections.unmodifiableList(Arrays.asList(new NamespacedKey[] {
				NamespacedKey.minecraft("protection"),
				NamespacedKey.minecraft("fire_protection"),
				NamespacedKey.minecraft("feather_falling"),
				NamespacedKey.minecraft("blast_protection"),
				NamespacedKey.minecraft("projectile_protection"),
				NamespacedKey.minecraft("thorns"),
				NamespacedKey.minecraft("respiration"),
				NamespacedKey.minecraft("depth_strider"),
				NamespacedKey.minecraft("aqua_affinity"),
				NamespacedKey.minecraft("sharpness"),
				NamespacedKey.minecraft("smite"),
				NamespacedKey.minecraft("bane_of_arthropods"),
				NamespacedKey.minecraft("knockback"),
				NamespacedKey.minecraft("fire_aspect"),
				NamespacedKey.minecraft("looting"),
				NamespacedKey.minecraft("efficiency"),
				NamespacedKey.minecraft("silk_touch"),
				NamespacedKey.minecraft("unbreaking"),
				NamespacedKey.minecraft("fortune"),
				NamespacedKey.minecraft("power"),
				NamespacedKey.minecraft("punch"),
				NamespacedKey.minecraft("flame"),
				NamespacedKey.minecraft("infinity"),
				NamespacedKey.minecraft("luck_of_the_sea"),
				NamespacedKey.minecraft("lure"),
				NamespacedKey.minecraft("frost_walker"),
				NamespacedKey.minecraft("mending"),
				NamespacedKey.minecraft("binding_curse"),
				NamespacedKey.minecraft("vanishing_curse"),
				NamespacedKey.minecraft("impaling"),
				NamespacedKey.minecraft("riptide"),
				NamespacedKey.minecraft("loyalty"),
				NamespacedKey.minecraft("channeling"),
				NamespacedKey.minecraft("sweeping"),
				/*NamespacedKey.minecraft("multishot"),
				NamespacedKey.minecraft("piercing"),
				NamespacedKey.minecraft("quick_charge"),*/
		}));
		ENCHANTMENT_INSTANCES = Collections.unmodifiableList(Arrays.asList(new Enchantment[] {
				Enchantment.PROTECTION_ENVIRONMENTAL,
				Enchantment.PROTECTION_FIRE,
				Enchantment.PROTECTION_FALL,
				Enchantment.PROTECTION_EXPLOSIONS,
				Enchantment.PROTECTION_PROJECTILE,
				Enchantment.THORNS,
				Enchantment.OXYGEN,
				Enchantment.DEPTH_STRIDER,
				Enchantment.WATER_WORKER,
				Enchantment.DAMAGE_ALL,
				Enchantment.DAMAGE_UNDEAD,
				Enchantment.DAMAGE_ARTHROPODS,
				Enchantment.KNOCKBACK,
				Enchantment.FIRE_ASPECT,
				Enchantment.LOOT_BONUS_MOBS,
				Enchantment.DIG_SPEED,
				Enchantment.SILK_TOUCH,
				Enchantment.DURABILITY,
				Enchantment.LOOT_BONUS_BLOCKS,
				Enchantment.ARROW_DAMAGE,
				Enchantment.ARROW_KNOCKBACK,
				Enchantment.ARROW_FIRE,
				Enchantment.ARROW_INFINITE,
				Enchantment.LUCK,
				Enchantment.LURE,
				Enchantment.FROST_WALKER,
				Enchantment.MENDING,
				Enchantment.BINDING_CURSE,
				Enchantment.VANISHING_CURSE,
				Enchantment.IMPALING,
				Enchantment.RIPTIDE,
				Enchantment.LOYALTY,
				Enchantment.CHANNELING,
				Enchantment.SWEEPING_EDGE
		}));
	}
	
	/*public static void ajustLoreForEnchantments(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		boolean needsEnchantmentLore = false;
		boolean hasEnchantmentLore = false;
		
		// needsEnchantmentLore
		needsEnchantmentLore = meta.hasEnchants() && meta.getEnchants().values().stream().max(Integer::compareTo).get() > 10;
		
		// hasEnchantmentLore
		if(meta.hasLore()) {
			
		} else {
			hasEnchantmentLore = false;
		}
	}
	
	private static boolean isEnchantmentLoreLine(String line) {
		if(!line.startsWith("\u00a77") || line.length() == 2)
			return false;
		for(String name : ENCHANTMENT_NAMES) {
			if(line.startsWith(name, 2)) {
				if()
			}
		}
	}*/
}