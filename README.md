# Plugin Pointz -> Convertisseur + Boutique en jeux

You can find all jars on my [Jenkins](https://jenkins.revolis.fr/) !

## Description

Le plugin Pointz permet **grossièrement** de lier votre serveur au site (boutique).

- Vérifier premièrement que votre base de données est liée !
- Ainsi que de vérifier si le plugin **Pointz** est installé sur votre Site-Web

## Plugins Nécessaires :relaxed:

- Le plugin MineWeb est disponible [ici](https://github.com/MineWeb/Plugin-Pointz)
- Le plugin Spigot est disponible [ici](https://www.spigotmc.org/resources/pointz-mineweb-cms.62187/)
- Il suffit de lier la base de données au plugin et configurer ensuite via le panel admin.

# Fichiers :smiley:

### config.yml

```yml
hooks:
  #if plugin is load, if not, there options are useless
  placeholder-api: true
  mvdwplaceholder-api: true


api:
  website_url: "https://edensky.fr"
  #paste here your public key, get it on website (this is an example)
  private_key: "-----BEGIN PRIVATE KEY-----
MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDSxqe/XX3ROrvR
BrH7rR1WlfOjvzqY+3OKiVvr3WJRWID4lygSbDxz7X18pEIw+OyyEK3A1UijhvGL
.....
LzQvPzJ2nHBRRwjrXRo7cqEGBumELPdaPvNtTf5mCjqDTMnheCHoWd56+wvL85ii
OpxYOcxxb3hUv0KYII2b2g==
-----END PRIVATE KEY-----"

 
```

### messages.yml

```yml
#
# Messages fr_FR.
# The placeholder {prefix} can be used everywhere,
# but do not delete placeholder {1}
#
prefix: "&7[&c&lPointz&7]"

messages:
  commands:
    incorrect_usage: "&fCorrect Usage : /{0}"
    no_permission: "&fCommande inconnue."
    help:
      - "&7&m------------------&8[&6Help Panel&8]&7&m------------------"
      - "{!pointz.command.send}&6/pointz send <player> <number> &eSend money to player"
      - "{!pointz.command.manage.set}&6/pointz set <player> <number> &eManage Player"
      - "{!pointz.command.manage.add}&6/pointz add <player> <number> &eManage Player"
      - "{!pointz.command.manage.del}&6/pointz del <player> <number> &eManage Player"
      - "{!pointz.command.check}&6/pointz check &eLook at the number of store points you have."
      - "{!pointz.command.gui.converter}&6/pointz converter &eOpens the shop point Converter."
      - "{!pointz.command.gui.shop}&6/pointz shop &eOpen the IG Shop."
      - "&7&m----------------------------------------------"

#
# Requierment messages.
#
no-register: "{prefix} Le joueur n'est pas inscrit sur le site"
no-register-own: "{prefix} Tu n'es pas inscrit sur le site"
not-connected: "{prefix} Le joueur n'est pas connecté !"
no-require-money: "{prefix} Tu n'as pas assez d'argent !"
no-player: "{prefix} &cSeulement les joueurs InGame peuvent exécuter cette commande."
#
only-number: "{prefix} &cSeul les nombres sont acceptés"
positive-number: "{prefix} &cIl faut un nombre supérieur à 0 !"
syntax-error: "{prefix} Erreur de syntaxe"
#
# Send command messages.
#
send-old: "{prefix} Tu avais {1} points sur la boutique"
send-new: "{prefix} Tu as maintenant {1} points sur la boutique"
send-cible: "{prefix} {1} vient de te donner {2} points sur la boutique"
#
# Check command message.
#
check-command: "{prefix} Tu as {1} points sur la boutique"
#
# Manage commands messages.
#
command-set-own: "{prefix} Tu viens de mettre {1} points sur la boutique a {2} !"
command-set-other: "{prefix} {1} vient de te mettre {2} points sur la boutique !"

command-add-own: "{prefix} Tu viens de donner {1} points sur la boutique a {2} !"
command-add-other: "{prefix} {1} vient de te donner {2} points sur la boutique !"

command-del-own: "{prefix} Tu viens d'enlever {1} points sur la boutique a {2} !"
command-del-other: "{prefix} {1} vient de t'enlever {2} points sur la boutique !"
#
# pconverter, pshop successmessages.
#
menu-converter-success-ig: "{prefix} Tu viens de payer {1} points sur la boutique in game"
menu-converter-success-web: "{prefix} Tu as maintenant {1} points sur la boutique"
menu-shop-success-ig: "{prefix} Tu viens de payer {1}$ et tu viens de recevoir ton achat !"
menu-shop-success-web: "{prefix} Tu viens de payer {1} points et tu viens de recevoir ton achat !"
```
