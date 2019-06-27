package com.raptor.plugins.util;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public final class StringUtils extends org.apache.commons.lang.StringUtils {
	
	public static BaseComponent format(BaseComponent component, Object... args) {
		boolean changed = false;		
		if(component instanceof TextComponent) {
			TextComponent textComponent = (TextComponent) component;
			String text = format0(textComponent.getText(), args);
			String insertion = textComponent.getInsertion() == null? null : format0(textComponent.getInsertion(), args);
			
			if(!Objects.equals(text, textComponent.getText())) {
				changed = true;
				component = textComponent = new TextComponent(textComponent);
				textComponent.setText(text);
			}
			
			if(!Objects.equals(insertion, textComponent.getInsertion())) {
				if(!changed) {
					changed = true;
					component = textComponent = new TextComponent(textComponent);
				}
				textComponent.setInsertion(insertion);
			}
		}
		
		if(component.getExtra() != null) {
			List<BaseComponent> extra = component.getExtra();
			if(!extra.isEmpty()) {
				for(int i = 0; i < extra.size(); i++) {
					BaseComponent element = extra.get(i);
					BaseComponent newElement = format(element, args);
					if(element != newElement) {
						if(!changed) {
							changed = true;
							component = component.duplicate();
							extra = component.getExtra();
						}
						extra.set(i, newElement);
					}
				}
			}
		}
		
		if(component.getClickEvent() != null) {
			ClickEvent event = component.getClickEvent();
			String value = format0(event.getValue(), args);
			if(!value.equals(event.getValue())) {
				if(!changed) {
					changed = true;
					component = component.duplicate();
				}
				component.setClickEvent(new ClickEvent(event.getAction(), value));
			}
		}
		
		if(component.getHoverEvent() != null) {
			HoverEvent event = component.getHoverEvent();
			BaseComponent[] values = event.getValue();
			for(int i = 0; i < values.length; i++) {
				BaseComponent element = values[i];
				BaseComponent newElement = format(element, args);
				if(element != newElement) {
					if(!changed) {
						changed = true;
						component = component.duplicate();
						values = (event = component.getHoverEvent()).getValue();
					}
					values[i] = newElement;
				}
			}
		}
		
		return component;
	}
	
	private static final Pattern FORMAT_PATTERN = Pattern.compile("\\{\\{|\\}\\}|\\{(?<idx>\\d+)(?<conv>(\\$(?<flags>[-+<># 0,(]*)(?<width>([1-9]\\d*)?)(?<precision>(\\.\\d+)?))?(?<type>([bBhHsScCdoxXeEfgGaA]|[tT][aAbBcCdDeFhHIjklmMSLNpQrRsTyYzZ])?))\\}");
	private static String format0(String text, Object[] args) {
		StringBuffer sb = new StringBuffer(text.length());
		Matcher m = FORMAT_PATTERN.matcher(text);
		while(m.find()) {
			if(m.group().equals("{{") || m.group().equals("}}")) {
				m.appendReplacement(sb, "$0");
			} else {
				final int idx = Integer.parseUnsignedInt(m.group("idx"));
				if(idx >= args.length) {
					m.appendReplacement(sb, "$0");
				} else {
					Object value = args[idx];
					String replacement;
					String conv = m.group("conv");
					boolean leftJustify = false, rightJustify = false;
					if(isEmpty(conv)) {
						replacement = String.valueOf(value);
					} else {
						String flags;
						if(conv.startsWith("$")) {
							conv = conv.substring(1);
							flags = m.group("flags");
							if(!isEmpty(flags)) {
								if(flags.contains("<")) {
									leftJustify = true;
									flags = flags.replaceFirst("<", flags.contains("-")? "" : "-").replace("<", "");
								}
								if(flags.contains(">")) {
									rightJustify = true;
									flags = flags.replace(">", "");
								}
							}
						} else {
							flags = "";
						}
						if(isEmpty(m.group("width")) || !leftJustify || !rightJustify) {
							replacement = String.format("%" + flags + defaultString(m.group("width")) + defaultString(m.group("precision")) + defaultIfEmpty(defaultString(m.group("type")), "s"), value);
						} else {
							assert leftJustify && rightJustify;
							int width = Integer.parseUnsignedInt(m.group("width"));
							replacement = center(String.format("%" + flags + "1" + defaultString(m.group("precision")) + defaultIfEmpty(defaultString(m.group("type")), "s"), value), width);
						}
					}
					m.appendReplacement(sb, replacement.replace("\\", "\\\\").replace("$", "\\$"));
				}
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * <p>
	 * Checks if the String contains only uppercase characters.
	 * </p>
	 *
	 * <p>
	 * <code>null</code> will return <code>false</code>. An empty String
	 * (length()=0) will return <code>false</code>.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isAllUpperCase(null)   = false
	 * StringUtils.isAllUpperCase("")     = false
	 * StringUtils.isAllUpperCase("  ")   = true
	 * StringUtils.isAllUpperCase("ABC")  = true
	 * StringUtils.isAllUpperCase("aBC")  = false
	 * StringUtils.isAllUpperCase("AB_C") = true
	 * </pre>
	 *
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if all alphabetic characters are uppercase, and is
	 *         non-null
	 * @since 2.5
	 */
	public static boolean isAllUpperCase(String str) {
		if(isEmpty(str)) {
			return false;
		}
		int sz = str.length();
		for(int i = 0; i < sz; i++) {
			char c = str.charAt(i);
			if(c != Character.toUpperCase(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Checks if the String contains only lowercase characters.
	 * </p>
	 *
	 * <p>
	 * <code>null</code> will return <code>false</code>. An empty String
	 * (length()=0) will return <code>false</code>.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isAllLowerCase(null)   = false
	 * StringUtils.isAllLowerCase("")     = false
	 * StringUtils.isAllLowerCase("  ")   = true
	 * StringUtils.isAllLowerCase("abc")  = true
	 * StringUtils.isAllLowerCase("abC")  = false
	 * StringUtils.isAllLowerCase("ab_c") = true
	 * </pre>
	 *
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if all alphabetic characters are lowercase, and is
	 *         non-null
	 * @since 2.5
	 */
	public static boolean isAllLowerCase(String str) {
		if(str == null || isEmpty(str)) {
			return false;
		}
		int sz = str.length();
		for(int i = 0; i < sz; i++) {
			char c = str.charAt(i);
			if(c != Character.toLowerCase(c)) {
				return false;
			}
		}
		return true;
	}
	
	/**
     * <p>Check if a String ends with any of an array of specified strings.</p>
     *
     * <pre>
     * StringUtils.endsWithAny(null, null)      = false
     * StringUtils.endsWithAny(null, new String[] {"abc"})  = false
     * StringUtils.endsWithAny("abcxyz", null)     = false
     * StringUtils.endsWithAny("abcxyz", new String[] {""}) = true
     * StringUtils.endsWithAny("abcxyz", new String[] {"xyz"}) = true
     * StringUtils.endsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
     * </pre>
     *
     * @param string  the String to check, may be null
     * @param searchStrings the Strings to find, may be null or empty
     * @return <code>true</code> if the String ends with any of the the prefixes, case insensitive, or
     *  both <code>null</code>
     * @since 2.6
     */
    public static boolean endsWithAny(String string, String... searchStrings) {
    	return org.apache.commons.lang.StringUtils.endsWithAny(string, searchStrings);
    }
    
    /**
     * <p>Joins the elements of the provided <code>Collection</code> into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").</p>
     *
     * <p>See the examples here: {@link #join(Object[],String)}. </p>
     *
     * @param collection  the <code>Collection</code> of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null iterator input
     * @since 2.3
     */
    public static <T> String join(T[] objs, String separator, Function<? super T, String> toStringFunction) {
    	StringBuilder b = new StringBuilder();
    	for(T obj : objs) {
    		if(b.length() != 0)
    			b.append(separator);
    		b.append(toStringFunction.apply(obj));
    	}
    	return b.toString();
    }
    
    /**
     * <p>Joins the elements of the provided <code>Collection</code> into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").</p>
     *
     * <p>See the examples here: {@link #join(Object[],String)}. </p>
     *
     * @param collection  the <code>Collection</code> of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null iterator input
     * @since 2.3
     */
    public static <T> String join(Collection<? extends T> objs, String separator, Function<? super T, String> toStringFunction) {
    	StringBuilder b = new StringBuilder();
    	for(T obj : objs) {
    		if(b.length() != 0)
    			b.append(separator);
    		b.append(toStringFunction.apply(obj));
    	}
    	return b.toString();
    }
    
 	private static final Matcher PREFIX_COLOR = Pattern.compile("^(?i)(" + ChatColor.COLOR_CHAR + "[0-9A-FK-OR])+").matcher("");

 	public static String getPrefixColors(String str) {
 		if(PREFIX_COLOR.reset(str).find())
 			return PREFIX_COLOR.group();
 		
 		return "";
 	}
 	
 	private static final Matcher POSTFIX_COLOR = Pattern.compile("(?i)(" + ChatColor.COLOR_CHAR + "[0-9A-FK-OR])+$").matcher("");
 	
 	public static String getPostfixColors(String str) {
 		if(POSTFIX_COLOR.reset(str).find())
 			return POSTFIX_COLOR.group();
 		
 		return "";
 	}
 	
 	/**
 	 * @return {@code true} if the string contains the color code character, {@linkplain ChatColor#COLOR_CHAR}.
 	 */
 	public static boolean hasColor(String str) {
		return str.indexOf(ChatColor.COLOR_CHAR) >= 0;
	}
 	
 	/**
 	 * Converts an integer to it's Roman Numeral form.
 	 * Negative numbers get prefixed with a minus sign ('-').
 	 * Zero just returns {@code "0"}.
 	 * 
 	 * @param input the number to convert
 	 * @return the Roman numeral
 	 */
 	public static String toRomanNumerals(int input) {
 		if (input < 0)
 			return "-" + toRomanNumerals(-input);
 		switch (input) {
 		case 0:
 			return "0";
 		case 1:
 			return "I";
 		case 2:
 			return "II";
 		case 3:
 			return "III";
 		case 4:
 			return "IV";
 		case 5:
 			return "V";
 		case 6:
 			return "VI";
 		case 7:
 			return "VII";
 		case 8:
 			return "VIII";
 		case 9:
 			return "IX";
 		case 10:
 			return "X";
 		}
 		
 		if (input > 10000)
 			return Integer.toString(input);
 		
 		StringBuilder s = new StringBuilder();
 		
 		while (input >= 1000) {
 			s.append("M");
 			input -= 1000;
 		}
 		while (input >= 900) {
 			s.append("CM");
 			input -= 900;
 		}
 		while (input >= 500) {
 			s.append("D");
 			input -= 500;
 		}
 		while (input >= 400) {
 			s.append("CD");
 			input -= 400;
 		}
 		while (input >= 100) {
 			s.append("C");
 			input -= 100;
 		}
 		while (input >= 90) {
 			s.append("XC");
 			input -= 90;
 		}
 		while (input >= 50) {
 			s.append("L");
 			input -= 50;
 		}
 		while (input >= 40) {
 			s.append("XL");
 			input -= 40;
 		}
 		while (input >= 10) {
 			s.append("X");
 			input -= 10;
 		}
 		while (input >= 9) {
 			s.append("IX");
 			input -= 9;
 		}
 		while (input >= 5) {
 			s.append("V");
 			input -= 5;
 		}
 		while (input >= 4) {
 			s.append("IV");
 			input -= 4;
 		}
 		while (input >= 1) {
 			s.append("I");
 			input -= 1;
 		}
 		return s.toString();
 	}
 	
 	private StringUtils() {
		throw new UnsupportedOperationException("StringUtils cannot be instantiated!");
	}
}
