> Projet archivé, abandonné par manque de temps

# Faylisia Core Plugin
Plugin utilisé pour créer le serveur Faylisia

> Le plugin est conçu de manière à rendre la création d'objets, d'entités et d'enchantements **rapide** et **facile**.
>> Lors de la création de contenue dans le plugin veuillez mettre des commentaires en **anglais** pour décrire les actions faites

> Si vous avez besoin d'aide / une suggestion / besoin de plus de méthodes pour rendre le code facile : me mp sur discord ou aller faire un ticket sur le serveur discord

### Fonctionalités en place :

- **[Objets customisés](#Objets)**
  - [Categories](#Categories)
  - [Objets classiques](#Objets)
  - [Armes](#Armes)
  - [Armures](#Armures)
  - [Armes magiques (avec abilité)](#Armes-Magiques)
- **[Enchantements customisés](#Enchantements)**
- **[Entités customisées](#Entités)**
  - [Loots customisable](#Loots)
  - [Classes de monstres](#Classes-De-Monstres)
  - [Les Recettes](#Les_Recettes)
    - [Les 'Crafting Recipes'](#Les-Crafting-Recipes)

## Concepts utilisés dans le plugin

### Les Handlers

Les handlers sont des méthodes regroupées dans la class [`Handlers`](src/main/java/fr/blockincraft/faylisia/items/event/Handlers.java), ce sont des fonctions similaires aux events, elles sont appelées lors d'une action comme le calcul d'une stat pour un joueur en particulier.  
Ils sont utilisés pour créer les abilités, les bonus d'armures et autres.

Il y a deux sortes d'handlers :  
**Les handlers avec valeurs**, lorsqu'on les utilise, on leur envoie une valeur en plus des autres paramètres et ils renvoient une valeur du même type.  
**Les handlers sans valeurs**, on les appelle juste, ils ne renvoient aucune valeur.

Pour créer un nouveau handler, il faut se rendre dans la class [`Handlers`](src/main/java/fr/blockincraft/faylisia/items/event/Handlers.java) et créer une nouvelle fonction avec un nom et des paramètres.

> Le dernier paramètre doit être un `boolean` exprimant si l'objet se trouve dans un slot d'armure du joueur, l'avant-dernier paramètre doit être un `boolean` exprimant si l'objet se trouve dans la main principale du joueur.  
> Et s'il s'agit d'un handler avec valeurs, l'avant avant-dernier paramètre doit être la valeur actuelle.

Exemple d'handlers :

```java
package fr.blockincraft.faylisia.items.event;

import org.bukkit.entity.Player;

public class Handlers {
    // Tous les handler doivent appeler cette fonction lorsqu'ils sont appelés
  default void onHandlerCall() {

  }
    
  // Un handler avec valeurs
  // On retrouve bien les trois paramètres obligatoires à la fin
  default long getDamage(Player player, long value, boolean inHand, boolean inArmorSlot) {
      onHandlerCall();
      return value;
  }
  
  // Un handler sans valeurs
  // On retrouve bien les deux paramètres obligatoires à la fin
  default void onInteract(Player player, Block clickedBlock, boolean isRightClick, EquipmentSlot hand, boolean inHand, boolean inArmorSlot) {
      onHandlerCall();
  }
}
```

Pour appelé un handler il faut utiliser ces fonctions :

```java
package com.organisation.test;

import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.utils.HandlersUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class Test {
  /**
   * Pour un handler avec valeurs comme le {@link Handlers#getDamage(Player, long, boolean, boolean)}
   */
  public static long getDamageWithHandlers(CustomPlayerDTO customPlayer, long damage) {
    // On récupère le joueur via le `CustomPlayerDTO`
    Player player = Bukkit.getPlayer(customPlayer.getPlayer());

    // On verifi que le joueur n'est pas null
    if (player == null) return damage;

    // On applique les handlers à la valeur
    return HandlersUtils.getValueWithHandlers(customPlayer, "getDamage", damage, long.class, new HandlersUtils.Parameter[]{
            new HandlersUtils.Parameter(Bukkit.getPlayer(customPlayer.getPlayer()), Player.class)
            // L'ordre des paramètres est important
            // Les trois derniers paramètres sont automatiquement créés, c'est pour cela qu'ils sont obligatoires
    });
  }

  /**
   * Pour un handler sans valeurs comme le {@link Handlers#onInteract(Player, Block, boolean, EquipmentSlot, boolean, boolean)}
   */
  public static void onInteract(CustomPlayerDTO customPlayer, Block clickedBlock, boolean isRightClick, EquipmentSlot hand) {
    // On récupère le joueur via le `CustomPlayerDTO`
    Player player = Bukkit.getPlayer(customPlayer.getPlayer());

    // On verifi que le joueur n'est pas null
    if (player == null) return;

    // On appelle les handlers
    HandlersUtils.callHandlers(customPlayer, "onInteract", new HandlersUtils.Parameter[]{
            new HandlersUtils.Parameter(Bukkit.getPlayer(customPlayer.getPlayer()), Player.class),
            new HandlersUtils.Parameter(clickedBlock, Block.class),
            new HandlersUtils.Parameter(isRightClick, boolean.class),
            new HandlersUtils.Parameter(hand, EquipmentSlot.class)
            // L'ordre des paramètres est important
            // Les deux derniers paramètres sont automatiquement créés, c'est pour cela qu'ils sont obligatoires
    });
  }
}
```

## Creation de contenue

***

### Objets

Les objects customisés sont réalisés avec une hiérarchie, lors de la création d'un objet customisé, il est important de respecter cette hiéarchie.  
> Par exemple un `AbilityItem` est une sous class de `DamageItem` qui est une sous class de `CustomItem`, il faut donc d'abord appeler les methods de la class `AbilityItem` puis celle de la class `DamageItem`...

Les objets customisés doivent être créés dans la class [`Items`](src/main/java/fr/blockincraft/faylisia/items/Items.java).  
Exemple d'un objet customisé :

```java
package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.Rarity;
import org.bukkit.Material;

public class Items {
  // ...

  // On a créé un nouvel objet avec comme material un "diamant" et avec un identifiant unique "cool_diamond"
  public static final CustomItem coolDiamond = new CustomItem(Material.DIAMOND, "cool_diamond")
          .setName("Cool Diamond") // On a défini son nom, la couleur est choisie en fonction de la rareté de l'objet
          .setLore("&eA legendary diamond from", "&ethe coolest ore!") // On a défini une description en jaune, chaque texte représente une ligne
          .setEnchantable(false) // On a défini si l'objet peut être enchanté
          .setDisenchantable(false) // On a défini si l'objet peut être désenchanté
          .setCategory(Categories.COOL_DIAMOND) // On a défini sa catégorie
          .setRarity(Rarity.COSMIC); // On a défini sa rareté

  // ...

  // Ici on va ajouter un craft a l'item
  static {
    // ...

    coolDiamond.setRecipe();

    // ...
  }

  // Ici on va enregistrer l'item
  static {
    // ...

    coolDiamond.register();

    // ...
  }
}
```

#### Categories

Les catégories sont utilisées pour trier les objects customisés dans le menu regroupant tous les objets customisés.   

Les catégories doivent être créées dans l'énumération [`Categories`](src/main/java/fr/blockincraft/faylisia/items/management/Categories.java).  
Exemple d'une catégorie :

```java
package fr.blockincraft.faylisia.items.management;

import org.bukkit.Material;

public enum Categories {
  // Creation d'une catégorie avec comme material une "armure de cheval en diamant", un custom model data de -1 donc aucun custom model data et un nom "&d&lCool Diamond"
  COOL_DIAMOND(Material.DIAMOND_HORSE_ARMOR, -1, "&d&lCool Diamond",
          "&8The most cool category", // Description de la catégorie, chaque texte représente une ligne
          "&8of the game",
          "",
          "&8Contient %items% items" // Le paramètre "%items%" est remplacé par le nombre d'objets contenu dans la catégorie
  );

  // ... code de l'énumération
}
```

#### Armes

Les armes sont des objets customisés avec des stats et des dégâts.
Exemple d'arme :

```java
package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.Rarity;
import org.bukkit.Material;

public class Items {
  // ...

  // On a créé un nouvel objet avec comme material une "épée en diamant" et avec un identifiant unique "cool_diamond_sword"
  public static final WeaponItem coolDiamondSword = (WeaponItem) new WeaponItem(Material.DIAMOND_SWORD, "cool_diamond_sword")
          .setDamage(50) // On a défini les dégats de l'arme
          .setName("Cool Diamond Sword") // On a défini son nom, la couleur est choisie en fonction de la rareté de l'objet
          .setLore("&eA beautiful sword forged", "&ewith the coolest ore!") // On a défini une description en jaune, chaque texte représente une ligne
          .setEnchantable(true) // On a défini si l'objet peut être enchanté
          .setDisenchantable(true) // On a défini si l'objet peut être désenchanté
          .setCategory(Categories.COOL_DIAMOND) // On a défini sa catégorie
          .setRarity(Rarity.MYSTICAL); // On a défini sa rareté
  
  // ...

  // Ici on va ajouter un craft a l'item
  static {
    // ...

    coolDiamond.setRecipe();

    // ...
  }

  // Ici on va enregistrer l'item
  static {
    // ...

    coolDiamond.register();

    // ...
  }
}
```

### Armures

Les armures sont des objets customisés avec des stats et un [Armor Set](#Armor-Set) qui peut être null

#### Armor Set

Les armor sets représentent un ensemble de bonus donnés au joueur s'il équipe un nombre prédéfini d'objet avec le même armor set.

> Les bonus sont donnés en passant par les [Handlers](#Les-Handlers)

Exemple de piece d'armure et d'armor set :

```java
package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.Rarity;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Items {
  // ...

  // On a créé un nouvel armor set avec "cool_diamond_set" comme identifiant unique
  public static final ArmorSet coolDiamondSet = new ArmorSet("cool_diamond_set")
          // On met une liste de bonus
          .setBonus(
                  // Un premier bonus nommé "Berserk" qui est actif si le joueur
                  // équipe au moins deux pièces d'armure avec cet armor set
                  new ArmorSet.Bonus("Berserk", 2, new Handlers() {
                    // On recrée la fonction "calculateItemStat" de la class Handlers
                    // pour doublé la stat des items si la stat calculé est la Force et
                    // si la pièce d'armure est bien dans un slot d'armure
                    @Override
                    public double calculateItemStat(@NotNull Player player, @NotNull CustomItem customItem, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                      onHandlerCall();
                      return inArmorSlot && stat == Stats.STRENGTH ? value * 2 : value;
                    }

                    // On recrée la fonction "getDefaultStat" de la class Handlers
                    // pour doublé la stat par défaut si la stat calculé est la Force et
                    // si la pièce d'armure est bien dans un slot d'armure
                    @Override
                    public double getDefaultStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                      onHandlerCall();
                      return inArmorSlot && stat == Stats.STRENGTH ? value * 2 : value;
                    }
                  }, "&7Vous gagnez &c+100% &7de force", "&7quand il est actif." // Description du bonus
                  ),
                  // Un second bonus nommé "Hulk Power" qui est actif si le joueur
                  // équipe au moins quatre pièces d'armure avec cet armor set
                  new ArmorSet.Bonus("Hulk Power", 4, new Handlers() {
                    // On recrée la fonction "getDamage" de la class Handlers pour
                    // doublé les dégâts du joueur si la pièce d'armure est dans un slot d'armure
                    @Override
                    public long getDamage(@NotNull Player player, long damage, boolean inHand, boolean inArmorSlot) {
                      onHandlerCall();
                      return inArmorSlot ? damage * 2 : damage;
                    }
                  }, "&7Vos dégats sont augmentés", "&7de &c100%&7." // Description du bonus
                  )
          );

  // ...

  // Ici on va enregistrer l'armor set
  static {
    coolDiamondSet.register();
  }

  // ...

  // On a créé un nouvel objet avec comme material un "casque en cuir" et avec "cool_diamond_helmet" comme identifiant unique
  public static final ArmorItem coolDiamondHelmet = (ArmorItem) new ArmorItem(Material.LEATHER_HELMET, "cool_diamond_helmet")
          .setArmorSet(coolDiamondSet) // On a défini l'armor set avec celui que l'on a créé plus haut
          .setStat(Stats.STRENGTH, 7.5) // On ajoute de la force à l'objet
          .setStat(Stats.SPEED, 50) // On ajoute de la vitesse à l'objet
          .setStat(Stats.HEALTH, 50) // On ajoute de la vie à l'objet
          .setName("Cool Diamond Helmet") // On a défini son nom, la couleur est choisie en fonction de la rareté de l'objet
          .setLore("&eA unique helmet made with", "&ethe coolest ore!") // On a défini une description en jaune
          .setColor(0xa103fc) // On a défini la couleur de l'objet (variable utilisé que si le material est en cuir)
          .setEnchantable(true) // On a défini si l'objet peut être enchanté
          .setDisenchantable(true) // On a défini si l'objet peut être désenchanté
          .setCategory(Categories.COOL_DIAMOND) // On a défini sa catégorie
          .setRarity(Rarity.DEUS); // On a défini sa rareté

  // ...

  // Ici on va ajouter un craft a l'item
  static {
    // ...

    coolDiamondHelmet.setRecipe();

    // ...
  }

  // Ici on va enregistrer l'item
  static {
    // ...

    coolDiamondHelmet.register();

    // ...
  }
}
```

#### Armes Magiques

Les armes magiques sont des [Armes](#Armes) qui possède en plus une abilité.  
Exemple d'arme magique :

```java
package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.event.DamageType;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.Rarity;
import fr.blockincraft.faylisia.items.weapons.WeaponAbilityItem;
import fr.blockincraft.faylisia.utils.HandlersUtils;
import org.bukkit.Material;

public class Items {
  // ...

  // On a créé un nouvel objet avec comme material une "épée en fer" et avec "test_item" comme identifiant unique
  public static final WeaponAbilityItem testItem = (WeaponAbilityItem) new AbilityItem(Material.IRON_SWORD, "test_item")
          .setAbilityName("Boom") // On a défini le nom de l'abilité
          .setAbilityDesc("&7Inflige &c10x &7les dégats aux monstres", "&7Dans un rayon de 10 blocs") // On a défini la description de l'abilité
          // On a défini l'abilité de l'objet avec une fonction lambda (Il y aura toujours les mêmes 
          // paramètres "(player, clickedBlock, hand)"), pour les créer il y a des méthodes utiles
          // disponibles dans la class AbilitiesUtils. La fonction doit renvoyer si l'abilité est
          // annulée 'true' si elle l'est, 'false' si elle ne l'est pas
          .setAbility((player, clickedBlock, hand) -> {
            CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

            long damage = (long) (customPlayer.getDamage(false) * 10);

            AbilitiesUtils.getEntitiesInRadius(player.getLocation(), 10.0).forEach(customEntity -> {
              long damageIn = HandlersUtils.getValueWithHandlers(customPlayer, "onDamage", damage, long.class, new HandlersUtils.Parameter[]{
                      new HandlersUtils.Parameter(player, Player.class),
                      new HandlersUtils.Parameter(customEntity, CustomEntity.class),
                      new HandlersUtils.Parameter(DamageType.MAGIC_DAMAGE, DamageType.class)
              });

              PlayerUtils.spawnDamageIndicator(damage, false, player, customEntity.getEntity().getLocation());
              customEntity.takeDamage(damage, player);
            });
            
            return false;
          })
          .setCooldown(5) // On a défini le délai entre deux utilisations de l'abilité
          .setUseCost(20) // On a défini le coût d'énergie magique pour utiliser l'abilité
          .setDamage(20) // On a défini les dégats de l'objet
          .setStat(Stats.MAGICAL_RESERVE, 100) // On a ajouté de la reserve magique a l'objet
          .setName("Test Item") // On a défini le nom de l'objet, la couleur dépend de la rareté de l'objet
          .setCategory(Categories.COOL_DIAMOND) // On a défini la catégorie de l'objet
          .setRarity(Rarity.COSMIC); // On a défini la rareté de l'objet

  // ...

  // Ici on va ajouter un craft a l'item
  static {
    // ...

    testItem.setRecipe();

    // ...
  }

  // Ici on va enregistrer l'item
  static {
    // ...

    testItem.register();

    // ...
  }
}
```

#### Les Recettes

Les recettes ou recipes sont les manières de créer les objets, il en existe plusieurs types comme les crafting recipes qui s'utilisent dans un menu avec un pattern particuliés.  
Ils sont constitués avec des [`CustomItemStack`](src/main/java/fr/blockincraft/faylisia/items/CustomItemStack.java) qui peuvent être créé en faisant : `new CustomItemStack(type de l'item déclaré dans la class Items, quantité de l'item)`

##### Les Crafting Recipes

Les crafting recipes sont les recettes réalisables dans un menu de table de craft avec un pattern en 3x3.  
Pour en créer un, il y a plusieurs constructors pour créer des patterns différents, le 1x1 pour les minerais par exemple, le 1x2 pour les sticks, le 2x2 pour la table de craft, le 3x1 pour les épées, le 3x2 pour les portes/sceaux, le 3x3 pour les blocs de minerais.  
Lorsque le pattern est en 1x2 / 1x3 / 2x3 il faut définir la direction avec verticale pour une épée et horizontale pour un lit.  

Exemple de craft pour une épée et pour une table de craft :

```java
package com.organisation.test;

import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.Items;
import fr.blockincraft.faylisia.items.recipes.CraftingRecipe;

public class Test {
  // On créé un craft en 3x1 dans la direction verticale pour une épée avec des "coolDiamond" 
  // à la place des diamants et 4 "coolDiamondBlock" à la place du stick
  // Les retours à la ligne ne sont pas obligatoires mais cela est pratique pour comprendre
  // de manière visuel
  public static final CraftingRecipe swordRecipe = new CraftingRecipe(
          new CustomItemStack(Items.coolDiamond, 1),
          new CustomItemStack(Items.coolDiamond, 1),
          new CustomItemStack(Items.coolDiamondBlock, 4),
          CraftingRecipe.Direction.VERTICAL
  );
  
  // On a créé un craft en 2x2 pour une table de craft avec des "coolDiamondBlock" à la place 
  // des planches
  // Les retours à la ligne ne sont pas obligatoires, mais cela est pratique pour comprendre
  // de manière visuelle
  public static final CraftingRecipe craftingTableRecipe = new CraftingRecipe(
          new CustomItemStack(Items.coolDiamondBlock, 1), new CustomItemStack(Items.coolDiamondBlock, 1),
          new CustomItemStack(Items.coolDiamondBlock, 1), new CustomItemStack(Items.coolDiamondBlock, 1)
  );
}
```

***

### Enchantements

Les enchantements sont semblables aux enchantements vanilla, ils peuvent être appliqués sur un type d'item, ils possèdent des conflits, c'est-à-dire des enchantements avec lesquelles ils ne sont pas compatibles.  
Les enchantements sont créés avec une limite de niveau global et une limite de niveau lors de la fusion de deux items.  
Les enchantements doivent être créés dans la class [`CustomEnchantments`](src/main/java/fr/blockincraft/faylisia/items/enchantment/CustomEnchantments.java).  
Pour les rendre fonctionnels on utilise les [Handlers](#Les-Handlers) mais lorsqu'on les crée il faut utiliser une nouvelle class nommé [`EnchantmentHandlersIn`](src/main/java/fr/blockincraft/faylisia/items/enchantment/CustomEnchantments.java) avec le niveau en paramètre : `new EnchantmentHandlersIn(-1) {};`, lors de la création d'un model pour un enchantement, le niveau doit être défini à `-1`.  

Exemple d'enchantement :

```java
package fr.blockincraft.faylisia.items.enchantment;

public enum CustomEnchantments {
  // L'index sert à trier les enchantements pour l'affichage, l'enchantement 0 sera avant l'enchantement 1
  
  // On a créé un enchantement avec l'index 0 nommé "Protection", avec des handlers, on met ensuite son
  // niveau maximal à 4 et son niveau maximal de fusion à 4 aussi, on dit ensuite que l'enchantement peut
  // être appliqué seulement si l'item est un ArmorItem (ou class filles) puis on met un tableau vide car
  // on ne veut pas de conflits
  PROTECTION(0, "Protection", new EnchantmentHandlersIn(-1) {
      // On recrée la fonction "getDefaultStat" de la class Handlers
      // pour ajouter 25 de valeur par niveau aux stats si la stat 
      // calculé est la Force et si la pièce d'armure est bien dans
      // un slot d'armure
      @Override
      public double getDefaultStat(Player player, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
        return inArmorSlot && stat == Stats.DEFENSE ? value + 25 * level : value;
      }

      // On recrée la fonction "calculateItemStat" de la class Handlers
      // pour ajouter 25 de valeur par niveau aux stats si la stat calculé 
      // est la Défense et si la pièce d'armure est bien dans un slot d'armure
      @Override
      public double calculateItemStat(Player player, CustomItem customItem, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
        return inArmorSlot && stat == Stats.DEFENSE ? value + 25 * level : value;
      }
  }, 4, 4, new Class[]{ArmorItem.class}, new CustomEnchantments[0]); // Pour en ajouter un autre, il faut mettre une virgule à la place du point virgule et mettre le point virgule apres le dernier.
  
  // ... reste du code
}
```

***

### Entités

***
