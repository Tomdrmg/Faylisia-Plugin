# Faylisia Core Plugin
Plugin utilisé pour créer le serveur Faylisia

> Le plugin est conçu de manière à rendre la création d'objets, d'entités et d'enchantements **rapide** est **facile**.
>> Lors de la création de contenue dans le plugin veuillez mettre des commentaires en **anglais** pour décrire les actions faites

### Fonctionalités en place :

- **[Objets customisés](#Objets)**
  - [Categories](#Categories)
  - [Armes](#Armes)
  - [Armures](#Armures)
  - [Armes magiques (avec abilité)](#Armes_magiques)
  - [Objets classiques](#Objets)
- **[Enchantements customisés](#Enchantements)**
- **[Entités customisées](#Entités)**
  - [Loots customisable](#Loots)
  - [Classes de monstres](#Classes_de_monstres)

## Concepts utilisés dans le plugin

### Les Handlers

Les handlers sont des méthodes regroupées dans la class `Handlers`, ce sont des fonctions similaires aux events, elles sont appelées lors d'une action comme le calcul d'une stat pour un joueur en particulier.  
Ils sont utilisés pour créer les abilités, les bonus d'armures et autres.

Il y a deux sortes d'handlers :  
**Les handlers avec valeurs**, lorsqu'on les utilise, on leur envoie une valeur en plus des autres paramètres et ils renvoient une valeur du même type.  
**Les handlers sans valeurs**, on les appelle juste, ils ne renvoient aucune valeur.

Pour créer un nouveau handler, il faut se rendre dans la class `Handlers` et créer une nouvelle fonction avec un nom et des paramètres.

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

Les objets customisés doivent être créés dans la class `Items`.  
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

Les catégories doivent être créées dans l'énumération `Categories`.  
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

Les armes sont des objets customisés avec des stats et des dégats

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

***

### Enchantements

***

### Entités

***