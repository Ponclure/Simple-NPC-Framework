# Simple NPC Framework [1.15 - 1.16] [Lightweight NPC Framework] 
[![CircleCI](https://img.shields.io/circleci/build/github/Ponclure/Simple-NPC-Framework?style=for-the-badge)](https://app.circleci.com/pipelines/github/Ponclure/Simple-NPC-Framework)
[![Issues](https://img.shields.io/github/issues/Ponclure/Simple-NPC-Framework?style=for-the-badge)](https://github.com/Ponclure/Simple-NPC-Framework/issues)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg?style=for-the-badge)](https://www.gnu.org/licenses/gpl-3.0)
[![Discord](https://img.shields.io/discord/775376080546693120.svg?style=for-the-badge)](https://discord.gg/d7qfcUwhex)
[![Repository Size](https://img.shields.io/github/languages/code-size/Ponclure/Simple-NPC-Framework?style=for-the-badge)](https://github.com/Ponclure/Simple-NPC-Framework)
[![Lines of Code](https://img.shields.io/tokei/lines/github/Ponclure/Simple-NPC-Framework?style=for-the-badge)](https://github.com/Ponclure/Simple-NPC-Framework)

## Introduction
Tired of coding NPC's? Annoyed to find that there are little to no outdated tutorials that you find over the internet on how you code these fake players? Need something lightweight? Well then Simple NPC Framework is the framework you should use. Although the Citizens API is very useful, I find it very heavy and bulky in terms of size, so we have decided to create a more lightweight framework for developers to use.

Simple NPC Framework is a fork of the original known lightweight framework `NPCLib`, a very useful API. After the developer decided to discontinue development for the API, we have decided to take over and continue to add support for the newer Minecraft versions that will be coming out. We will not support older versions, as `NPCLib` can be used for those versions and we want to respect the authors.

## Developer API
If you want to use this API, we recommend you to `clone` the repository and run `./gradlew build SimpleNPCFramework-API:publishToMavenLocal` to install the repository to your local dependencies.

**The library uses internal server classes directly, and to compile it you need to run BuildTools for 1.15.2 and 1.16.4 (not .5) so they are installed in your local repository**

To start off, define an instance of this library using the constructor which accepts `Plugin` as its parameters like so:

`SimpleNPCFramework framework = new SimpleNPCFramework(plugin);`

Do not create multiple instances of `SimpleNPCFramework` in many classes! Instead use dependency injection or create a getter method in your main plugin class to reference the same instance of the library.

In order to create your first NPC, we will use the `SimpleNPCFramework#createNPC()` method, which can accept either a `List<String>` of lines which would be a hologram on top of the NPC, or nothing if you want a plain NPC. As an example:
```java
// NPC with no Hologram
NPC normalNPC = framework.createNPC();

// NPC with a Hologram
NPC hologramNPC = framework.createNPC(Arrays.asList("Line 1", "Line 2");
```

The other methods that can be used to customize your NPC can be found [here](https://github.com/Ponclure/Simple-NPC-Framework/blob/master/api/src/main/java/com/github/ponclure/simplenpcframework/api/NPC.java) with descriptions of what each method performs, however some of the most commonly used include:

| Method                               | What it Does                                                |
| ------------------------------------ | ----------------------------------------------------------- |
| `NPC#setLocation(Location location)` | Sets the location of an NPC                                 |
| `NPC#create()`                       | Creates the NPC                                             |
| `NPC#destroy()`                      | Destroys the NPC and removes it from the internal registry. |
| `NPC#show(Player player)`            | Sets NPC visible to specific Player                         |
| `NPC#hide(Player player)`            | Sets NPC not visible to Specific Player                     |
| `NPC#getId()`                        | Returns the assigned ID for the NPC                         |

As for a code example:
```java
NPC npc = framework.createNPC(Arrays.asList("Hello!")); // Initialize NPC
npc.setLocation(new Location(0, 0, 0)); // Set Location to (0, 0, 0)
npc.show(Bukkit.getPlayer("PulseBeat_02")); // Set Visible to Player
npc.create(); // Create the NPC
System.out.println(npc.getId()); // Print out the ID asociated with the NPC
```

In order to add skins for your NPC, we have added a `Skin` class which you can use `NPC#setSkin(Skin skin)` for. `Skin` takes in two parameters in the constructor, a `String` value and a `String` signature for the skin. More information about what these two values represent can be found [here](https://wiki.vg/Mojang_API#UUID_-.3E_Profile_.2B_Skin.2FCape).

For your convienance, we have added a class called `AsyncSkinFetcher`.
```java
AsyncSkinFetcher.fetchSkinFromIdAsync(int id, skin -> {
    // Create your NPC.
})
```
OR
```
AsyncSkinFetcher.fetchSkinFromUuidAsync(UUID uuid, skin -> {
    // Create your NPC.
})
```
It has a `MineSkin` feature where it can accept an integer for its arguments like ([https://mineskin.org/725954](https://mineskin.org/725954) has ID 725954), or you can just pass in a UUID and it will retrieve the skin using the identifier. It should be noted that the method `fetchSkinFromUsernameAsync` is deprecated for a reason, as the player name can change many times. Thus, the UUID is the ideal option you should use.

In addition from the skins and methods of NPC's, there are also events your plugin can listen to as well. These events include a `NPCShowEvent`, `NPCHideEvent`, and `NPCInteractEvent`.

As for examples, you can take a look at this class I written [here](https://github.com/Ponclure/Simple-NPC-Framework/blob/master/api/src/main/java/examples/NPCUsageExample.java) which stores the person who created NPC's and sends them as a message to the player if they are interacted.

## Conclusion
And that's pretty much it. We will be adding more features as we go on, but check out our organization's website located at [https://ponclure.github.io](https://ponclure.github.io), which contains all of our projects and source code made by collaborative developers. I hope this API is useful!
