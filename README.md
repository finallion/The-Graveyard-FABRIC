# Welcome to the Graveyard Wiki!

## Fabric:
Versions: 1.16.4, 1.16.5, 1.17, 1.17.1, 1.18, 1.18.1, 1.18.2, 1.19-1.19.4, 1.20-1.20.1
Up-to-date versions: 1.19.2, 1.19.4, 1.20-1.20.1

## Configuration: 
The Graveyard comes with a config file, located in .minecraft/config/graveyard folder.

### Structures:
Each structure can be enabled/disabled in the config file.
Changing the frequency, spacing or separation of structures requires the use of a datapack.
Changing the generation biomes of structures requires the use of a datapack.

### Mobs:
Ghouls, Revenants, Nightmares and Skeleton Creepers spawn naturally. Their spawn weight and group size can be configured in the config file.
Each mob can be set to burn/not burn in sunlight and to take damage from wither roses in the config file.
Changing the spawn biomes of naturally spawning mobs requires the use of a datapack.

### Horde:
An random event happening at night, where a large group of ghouls and revenants OR undead illagers spawn.
The size of the spawning group and its frequency can be set in the config file. It can also be disabled.

## Boss:
### Corrupted Champion (Lich):

#### How to find and to summon the boss:
Collect the three bone staff pieces (Ominous Bone Staff Fragment) from the Ruins structure. Every Ruin has a unique fragment (upper, middle and lower bone staff fragment). These structures spawn in forests and are 1) a broken tower (head fragment) 2) a bloody hill (middle fragment) and 3) a campsite (lower fragment)
All three staff pieces can also sometimes be bought by the Nameless Hanged located at the Dead Tree structure for a price of 64 Corruption.
Obtain a bone dagger from an Acolyte, or craft it yourself.
Hold a glass bottle in your offhand and start killing villagers with the bone dagger. You'll get a Vial of Blood. Fill the vial to the limit.
Find the Lich Prison structure, a large floating island above the oceans.
Wait until it is night, place the bone staff pieces (from upper to lower) on the dark corrupted deepslate blocks in front of the altar (they should fairly stand out).
Pour the Vial of Blood into the altar.

#### How to fight the boss:
Phase 1. The Champion has its coat on and relies on using magic to attack you. During some of these attacks the Champion will have a disc surrounding it, which will make it immune to attacks.
Phase 2. The coat is gone and its time to hunt. The Champion will be immune during its hunt. It will blind you and teleport you randomly. Key in this phase is to survive and stay away from the Champion. A loud cracking noise indicates when the Champion comes closer. After a certain amount of time the hunt is over and the Champion can be attacked.
Phase 3. The Champion is basically defeated, but still dangerous. Be fast to take it down.

The Corrupted Champion will drop one of 5 staffs (see Ghouling).

The Corrupted Champion can heavily be modified in the config file (damage, health, phases, attacks, summonable items etc.)

## Ghouling
The Ghouling is a mob summoned from the Bone Staff, dropped by the Corrupted Champion. It is loyal to whomever summoned it. It is not possible, once summoned, to give the staff to another player for him to use. The staff is bound to the player who used it first.
The Ghouling follows and teleports to its master.
Once dead the Ghouling can be resummoned by its master.

### Commands:
Right-click with staff on Ghouling: a disc appears and the ghouling will stay in place.
Shift-Right-click: the Ghouling will teleport to you.
Right-click on entity: the Ghouling will attack this entity.

Right-click with a coffin or sarcophagus on the Ghouling will equip it onto the Ghouling. Only the owner can access the storage then.

### Troubleshooting:
If you lost your ghouling or something similar: in your .minecraft/saves/YOUR_WORLD_NAME is a file called "graveyardGhoulingUUIDmapping.txt". It will contain a list of all pairs (UUID of ghouling - UUID of player). You can delete the line matching your player UUID (on multiplayer) or the file itself (on singleplayer). This will reset the corresponding staff (line deleted) or all staffs (file deleted), meaning that every staff can summon a Ghouling again, but every existant Ghouling will no longer obey any orders.

