package com.raptor.plugins.config;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Represents a {@linkplain Properties} object as an instance of {@linkplain FileConfiguration}.
 */
public class PropertiesConfiguration extends FileConfiguration {
	protected static final String COMMENT_PREFIX = "#";

	public PropertiesConfiguration() {}
	
	public PropertiesConfiguration(Properties properties) {
		loadFromProperties(properties);
	}
	
	public Properties toProperties() {
		Properties props = new Properties();
		for(String key : this.getKeys(true)) {
			props.setProperty(key, String.valueOf(this.get(key)));
		}
		return props;
	}
	
	@Override
	public String saveToString() {
		Properties props = this.toProperties();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			props.store(baos, buildHeader());
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.WARNING, "IOException occurred when saving a PropertiesConfiguration to string", e);
		}
		
		try {
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			Bukkit.getLogger().log(Level.SEVERE, "unsupported encoding: UTF-8", e);
			return baos.toString();
		}
	}

	@Override
	public void loadFromString(String contents) throws InvalidConfigurationException {
		Properties properties = new Properties();
		try {
			properties.load(new BufferedInputStream(new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8))));
		} catch (IOException e) {
			throw new InvalidConfigurationException(e);
		} catch(IllegalArgumentException e) {
			throw new InvalidConfigurationException(e.getMessage(), e);
		}
		
		String header = parseHeader(contents);
		if(header.length() > 0) {
			options().header(header);
		}
		
		if(!properties.isEmpty()) {
			loadFromProperties(properties);
		}
	}

	public void loadFromProperties(Properties props) {
		for(Map.Entry<Object, Object> entry : props.entrySet()) {
			this.set(String.valueOf(entry), entry.getValue());
		}
	}
	
	protected String parseHeader(String contents) {
		StringBuilder header = new StringBuilder();
		
		try(Scanner scan = new Scanner(contents)) {
			boolean wasCommentLine = true;
			while(wasCommentLine && scan.hasNext()) {
				String line = scan.nextLine().trim();
				if(line.startsWith(COMMENT_PREFIX)) {
					header.append(line, 1, line.length());
				} else {
					wasCommentLine = line.isEmpty();
				}
			}
		}
		
		return header.toString();
	}
	
	@Override
	protected String buildHeader() {
		String header = options().header();
		
		if(header == null)
			return "";
		
		StringBuilder b = new StringBuilder();
		try(Scanner scan = new Scanner(header)) {
			while(scan.hasNext()) {
				b.append(COMMENT_PREFIX)
				 .append(scan.nextLine())
				 .append('\n');
			}
		}
		
		return b.toString();
	}

	public static PropertiesConfiguration loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");

        PropertiesConfiguration config = new PropertiesConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }

        return config;
	}
	
	public static PropertiesConfiguration loadConfiguration(Reader reader) {
		Validate.notNull(reader, "Stream cannot be null");

        PropertiesConfiguration config = new PropertiesConfiguration();

        try {
            config.load(reader);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
        }

        return config;
	}
}
