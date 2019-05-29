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
prefix: "&7[&c&lPointz&7] "

command-title: "&7Liste des commandes"
#Start help
command-check: "&8&l- &a/pointz check  "
command-check-desc: "&7- Regarder le nombre de point boutique que l'on a."

command-send: "&8&l- &a/pointz send <player> <nombre>  "
command-send-desc: "&7- Envoyer ses points a un joueur."

command-admin-desc: "&7- admin."
command-set: "&8&l- &a/pointz set <player> <nombre>  "
command-add: "&8&l- &a/pointz add <player> <nombre>  "
command-del: "&8&l- &a/pointz del <player> <nombre>  "

command-shop: "&8&l- &a/pshop  "
command-shop-desc: "&7- Ouvre le shop IG."

command-gui: "&8&l- &a/pconverter  "
command-gui-desc: "&7- Ouvre le convertisseur de point boutique."

#Stop help
no-permission: "&3Tu n'as pas la permission"
no-register: "Le joueur n'est pas inscrit sur le site"
no-register-own: "Tu n'es pas inscrit sur le site"
not-connected: "Le joueurs n'est pas co !"

no-require-money: "Tu n'as pas assez d'argent !"


only-number: "Seul les nombres sont acceptés"

#keep {0}, {1}, {2}
check-command: "Tu as {1} points sur la boutique"

command-set-own: "Tu viens de mettre {0} points sur la boutique a {1} !"
command-set-other: "{0} vient de te mettre {1} points sur la boutique !"

command-add-own: "Tu viens de donner {0} points sur la boutique a {1} !"
command-add-other: "{0} vient de te donner {1} points sur la boutique !"

command-del-own: "Tu viens de t'enlever {0} points sur la boutique a {1} !"
command-del-other: "{0} vient de te t'enlever {1} points sur la boutique !"

send-old: "Tu avais {1} points sur la boutique"
send-new: "Tu as maintenant {1} points sur la boutique"
send-cible: "{0} vient de te donner {1} points sur la boutique"

#pgui gui success
menu-gui-success-ig: "Tu viens de payer {1} points sur la boutique in game"
menu-gui-success-web: "Tu as maintenant {1} points sur la boutique"

#pshop gui success
menu-shop-success-ig: "Tu viens de payer {1}$ et tu viens de recevoir ton achat !"
menu-shop-success-web: "Tu viens de payer {1}points et tu viens de recevoir ton achat !"
```
