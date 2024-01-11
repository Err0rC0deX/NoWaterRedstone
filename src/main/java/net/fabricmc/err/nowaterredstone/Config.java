package net.fabricmc.err.nowaterredstone;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public class Config {
	protected static Path propertiesFile;
	protected static Properties properties = new Properties();

	protected static Boolean enable = true;
	protected static Set<String> redstoneBlocks = new HashSet<>();

	public static String[] defaultRedstoneBlocks()
	{
		Set<String> blocks = new HashSet<>();
		blocks.add("minecraft:button");
		blocks.add("minecraft:lever");
		blocks.add("minecraft:redstone_wire");
		blocks.add("minecraft:repeater");
		blocks.add("minecraft:comparator");
		blocks.add("minecraft:redstone_torch");
		blocks.add("minecraft:redstone_wall_torch");
		return blocks.stream().filter(Objects::nonNull).toArray(String[]::new);
	}

	public static void initialize()
	{
		propertiesFile = FabricLoader.getInstance().getConfigDir().resolve(NoWaterRedstone.MODID).resolve(NoWaterRedstone.MODID + ".properties");
	}

	public static void load()
	{
		Boolean failed = false;
		try (InputStream stream = new FileInputStream(propertiesFile.toFile()))
		{
			properties.load(stream);
		}
		catch (FileNotFoundException exception)
		{
			NoWaterRedstone.LOGGER.info("No configuration found");
			failed = true;
		}
		catch (IOException exception)
		{
			NoWaterRedstone.LOGGER.warn("Failed to read configuration", exception);
		}

		if (failed) properties.clear();
		enable = getBool("enable", true);
		redstoneBlocks = getStringSet("redstoneBlocks", defaultRedstoneBlocks());

		if (failed)
		{
			NoWaterRedstone.LOGGER.info("Generating new configuration");
			write();
		}
	}

	public static void write()
	{
		propertiesFile.toFile().getParentFile().mkdirs();
		try (OutputStream output = new FileOutputStream(propertiesFile.toFile()))
		{
			properties.store(output, "Don't put comments; they get removed");
		}
		catch (IOException exception) {
			NoWaterRedstone.LOGGER.warn("Failed to write configuration", exception);
		}
	}

	public static Boolean enable()
	{
		return enable;
	}

	public static void enable(Boolean value)
	{
		if(enable != value)
		{
			enable = value;
			properties.setProperty("enable", enable ? "true" : "false");
			write();
		}
	}

	public static Set<String> redstoneBlocks()
	{
		return redstoneBlocks;
	}

	protected static String getString(String key, String defaultValue)
	{
		if (defaultValue == null) defaultValue = "";
		String value = properties.getProperty(key);
		if (value == null)
		{
			properties.setProperty(key, defaultValue);
			return defaultValue;
		}
		return value;
	}

	protected static boolean getBool(String key, boolean defaultValue)
	{
		String value = properties.getProperty(key);
		if (value == null)
		{
			properties.setProperty(key, defaultValue ? "true" : "false");
			return defaultValue;
		}
		return value.equalsIgnoreCase("true") || value.equals("1");
	}

	protected static int getInt(String key, int defaultValue)
	{
		String value = properties.getProperty(key);
		if (value == null)
		{
			properties.setProperty(key, String.valueOf(defaultValue));
			return defaultValue;
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException exception)
		{
			properties.setProperty(key, String.valueOf(defaultValue));
			return defaultValue;
		}
	}

	protected static double getDouble(String key, double defaultValue)
	{
		String value = properties.getProperty(key);
		if (value == null)
		{
			properties.setProperty(key, String.valueOf(defaultValue));
			return defaultValue;
		}
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException exception)
		{
			properties.setProperty(key, String.valueOf(defaultValue));
			return defaultValue;
		}
	}

	protected static Set<String> getStringSet(String key, String[] defaultValue)
	{
		String value = properties.getProperty(key);
		if (value == null)
		{
			properties.setProperty(key, joinString(defaultValue, ","));
			return new HashSet<>(Arrays.asList(defaultValue));
		}
		Set<String> set = new HashSet<>();
		String[] parts = value.split(",");
		for (String part : parts)
		{
			try
			{
				set.add(part.trim());
			}
			catch (NumberFormatException exception) {}
		}
		return set;
	}

	public static String joinString(String[] strings, String delimiter)
	{
		List<String> list = Arrays.asList(strings);
		if (list.isEmpty()) return "";
		StringBuilder buffer = new StringBuilder();
		Iterator<?> iter = list.iterator();
		while (iter.hasNext())
		{
			buffer.append(iter.next());
			if (iter.hasNext()) buffer.append(delimiter);
		}
		return buffer.toString();
	}
}
