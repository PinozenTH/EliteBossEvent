# 🙏🏻 NOTE: This plugin has been made by a single developer it's may experience alot of bugs, so please test it before push it on production!


# EliteBossEvent
Minecraft plugin that enhances gameplay by introducing a timed event where a random \**(vanilla-mob + mythicmob)* is summoned near a random player every X minutes

## Requirements

Minecraft Version 1.20.x+

Java Version 21

[Mythic Mobs](https://mythiccraft.io/index.php?pages/official-mythicmobs-download/&version=5.6.2#google_vignette) need to be install to make it work!

## Optional Dependencies

- Luckperms
- PlugmanX

## Commands

- /ebe reload *"Reload Config file"*
- /ebe start [optional:now] *"Start event (now = start event immediately)"*

## Configurations

### config.yml
``` yaml
debug:
  # recommended to set this to false if you don't know what you're doing
  enabled: false
  # recommended to set event-delay value to 60 or higher for better performance
  change-delay-to-sec: false
  # bypass-permission for debug mode
  bypass-permission: false

language: en-us.yml

# Mythic mobs boss name list
mythicMob-bosses-name: [
    "ExampleBossA",
    "ExampleBossB",
    "ExampleBossC",
]

# Normal mythic mobs mob name list
# Note: if you don't want to include normal mobs, just leave it empty and set include-normal-mobs to true
mythicMob-mini-boss-name: [
    "MythicMiniBossA",
    "MythicMiniBossB",
    "MythicMiniBossC",
]

# get entity name from https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html
vanilla-entity-types: [
    "CREEPER",
    "PIGLIN",
    "HOGLIN",
    "WITHER_SKELETON",
    "ZOMBIE",
    "SKELETON"
]

# Spawn configuration
summon-rules:
  # [VANILLA, MYTHICMOB, MIXED]
  mob-type: mixed
  # Worlds where the boss will be spawn
  spawn-worlds: [
      "world",
      "world_nether",
      "world_the_end"
  ]
  # Radius of the spawn area (default: 5)
  distance: 5
  # Minimum players required to start the event (default: 3)
  min-players: 3
  # Maximum players allowed to get tick per area (set to 0 for online players size / 2)
  max-players: 0
  # Max spawning count per picked player (default: 8)
  max-mobs-spawn-count-per-player: 8
  # Max boss spawn count per world (default: 1)
  max-boss-spawn-count-per-world: 1
  # Check level requirement (default: false)
# (WIP) check-level-requirement: false
  # Level requirement for player when boss is about to spawn (default: 1)
# (WIP) level-requirement: 1
  # Check permission requirement (default: false)
  check-permission-requirement: false
  # Boss spawn chance (default: 0.1)
  boss-spawn-chance: 0.1
  # delay in minutes before the event starts (default: 10)
  event-delay: 10
  # what y level for starting pick a player and  spawning mobs (default: 64)
  Y-level: 64
  # which block to whitelist for spawning mobs (default: ["grass_block"])
  whitelist-block: ["grass_block"]
```

### lang.yml

``` yaml
# Plugins
prefix: "&8[&6EliteBossEvent&8]"
reload: "EliteBossEvent has been reloaded!"
enable: "EliteBossEvent has been Enabled!"
disable: "EliteBossEvent has been Disabled!"
warn_low_delay: "Event delay is less than 1, setting to default value of 10 minutes."

# Commands
no_permission: "You do not have permission to use this command!"
invalid_command: "Invalid command!"

# Elite Boss Event
elite_event_started: "Elite Boss Event has started!"
elite_event_stopped: "Elite Boss Event has stopped!"
elite_event_starting: "Mob will be summoned in <count> seconds!"
no_player_ticked: "No player ticked, mob will not be summoned!"
summon_mob: "Mob has been summoned!"
summon_notify: "Mob will be summoned at a certain player <cooldown> seconds!"
warning_player: "Please avoid a player with a glowing effect!"
boss_spawned: "Boss has been spawned at <location>"
mini_boss_spawned: "Mini-boss has been spawned at <location>"

# Debug Mode
debug_mode_enabled: "Debug mode is enabled!"

# Mythic Mobs
hooked_to_mythic_mobs: "Hooked into MythicMobs!"
checking_provided_mythic_mobs: "Checking provided MythicMobs..."
no_mythic_mobs: "MythicMobs not found! Disabling plugin..."
not_valid_mythic_mob: "&c is not a valid MythicMob!"
registered_mythic_mob: "&a✓ Registered MythicMob: &e"
null_mythic_mob: "&c is null, make sure that mobs is exist!"
```

