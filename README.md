# Plugin Pointz -> Convertisseur + Boutique en jeux

## Description
Le plugin Pointz permet **grossièrement** de lier votre serveur au site (boutique).
- Verifier premièrement que votre base de donnée est liée !
- Ainsi que de verifier si le plugin **Pointz** est installé sur votre Site-Web

## Plugins Nécessaires :relaxed:

- Le plugin mineweb est disponible [ici](https://github.com/MineWeb/Plugin-Pointz)
- Le plugin spigot est disponible [ici](https://www.spigotmc.org/resources/pointz-mineweb-cms.62187/)
- Il suffit de lier la base de donnée au plugin et configurer ensuite via le panel admin.

# Fichiers :smiley:

### config.yml
```yml
#
# MySQL settings.
#
database:
    host: "localhost"
    username: "root"
    password: ""
    database: "mineweb"
    #default = 3306
    port: 3306
```

### messages.yml
```yml
#
# Messages fr_FR.
# The placeholder {prefix} can be used everywhere,
# but do not delete placeholder {1}
#
prefix: "&7[&c&lPointz&7]"
# Title /help
command-title: "{prefix} &7Liste des commandes"
# Check command /help messages
command-check: "&8&l- &a/pointz check  "
command-check-desc: "&7- Regarder le nombre de points boutique que l'on a."
# Send command /help messages
command-send: "&8&l- &a/pointz send <player> <number>  "
command-send-desc: "&7- Envoyer ses points a un joueur."
# Manage command /help messages
command-admin-desc: "&7- admin."
command-set: "&8&l- &a/pointz set <player> <number>  "
command-add: "&8&l- &a/pointz add <player> <number>  "
command-del: "&8&l- &a/pointz del <player> <number>  "
# pshop /help messages
command-shop: "&8&l- &a/pshop  "
command-shop-desc: "&7- Ouvre le shop IG."
# pconverter /help messages
command-converter: "&8&l- &a/pconverter  "
command-converter-desc: "&7- Ouvre le convertisseur de point boutique."
#
# Requierment messages.
#
no-permission: "{prefix} &3Tu n'as pas la permission"
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
menu-shop-success-web: "{prefix} Tu viens de payer {1}points et tu viens de recevoir ton achat !"
```
