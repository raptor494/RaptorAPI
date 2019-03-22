# RaptorAPI
RaptorAPI is the base API for all of Raptor&apos;s 1.13.2 Spigot plugins.
### Features
- Support for JSON config files
- Easy load/save all types of files in your plugin&apos;s data folder
- Easy registration of event handlers and command executors
- Automatic reloading of event handlers
- Automatic GSON support for many commonly used types, such as `org.bukkit.Location`, `org.bukkit.World`, `java.util.UUID`, `net.md_5.bungee.api.chat.BaseComponent` (JSON text), and `org.bukkit.inventory.ItemStack`.
- Built-in plugin that loads items from RaptorAPI/items/\*.yml and allows giving them to yourself via the /customitem command.
- `InventoryUtils.removeFromInventory(Player, ItemStack...)`, which only removes the items if they can all be removed.
- `ItemUtils.isSpawnEgg(Material)`, `ItemUtils.isBanner(Material)`, and `ItemUtils.isBannerOrShield(Material)`.
- `StringUtils.format(BaseComponent, Object...)` which formats the JSON text using the {#} notation, where # is the argument index.
- `TypedList`, `TypedSet`, `TypedCollection`, and `TypedMap`, which provide runtime type-checking.
- `PermissionBasedCommandHelpTopic`, which provides a different help topic conent based on whether the viewer has a permission or not.
### Maven
I do not have a Maven repository for this set up yet, unfortunately. You&apos;ll have to download the jar from this repository&apos;s target folder, and add it like so:
```xml
<dependency>
		<groupId>com.raptor.plugins</groupId>
		<artifactId>RaptorAPI</artifactId>
		<version>0.1</version>
		<type>jar</type>
		<scope>system</scope>
		<systemPath>PATH-TO-RaptorAPI.jar</systemPath>
</dependency>
```
### Using
Instead of having your main plugin class extend `JavaPlugin`, make it extend `RaptorPlugin`.