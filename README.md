# Minigun Madness
This game was originally created for the University of Waterloo Games Institute Jam (Winter 2016)

![Fellowship of the Bus][logo]

## Installation
There are two recommended options for running this game.

1. Download one of the pre-built executable JAR files under [releases]. Run it by either double-clicking the JAR file or entering the command `java -jar <jar-name>` on the command line.
2. Build from source. The easiest way to do so is to use [sbt]:
    1. Install sbt.
    2. Run sbt from the command line in the project root directory.
    3. At the prompt, type `run`.

## Controls
This game can be controlled via keyboard or gamepad (compatible with both Playstation 3 and Xbox 360 controllers). The controls are given as follows (PS3/X360/Keyboard):

* Pause - Start/Start/Enter
* Return to main menu - Select/Back/Esc
* Shoot - R1/RB/Left-Click
* Jetpack - L1/LB/Space
* Move - Left Stick or D-pad/Left Stick or D-pad/Arrow Keys or WASD Keys
* Turn gun - Right Stick/Right Stick/Mouse

## How to Play
Minigun Madness is an arena-style competitive shoot 'em up game. The theme of the game is "sacrifice", with a "tribal alien" aesthetic.

You play as one of four minigun-wielding combatants, and your goal is to get the highest score. Each player comes equipped with a jetpack, allowing faster, unconstrained movement as long as there is remaining fuel. While shooting, your gun turns more slowly, so constantly holding the fire button is rarely an effective strategy.

The game takes place in a constantly changing arena, with platforms continuously moving down the screen towards the pit of doom. If a player falls off the bottom of the screen into the pit, they die. Each player has a set amount of health, which is depleted by taking hits from other players' bullets.

[![video image]][video]

There are two different modes of playing: stock and score. In a stock match, each player has a set number of lives. Stock matches have the option of 1, 3, 5, 10, 30, or 99 stock. In a score match, each player has unlimited lives, and competes to reach a target score. Score matches have the option of 5, 10, 30, or 99 target points. After dying, a player's stock is decreased by 1 and they are respawned after 3 seconds if they have remaining lives.

![Intense multiplayer player action][action]

Each time a player dies, the score of the last player to damage them increases by 1. If a player is alive at the end of the game, their score increases by 1.

This game supports multiplayer for up to 4 players using controllers, with one player able to use a mouse and keyboard. The game also supports a very basic AI player for any unoccupied slots. Minigun Madness is best played with a group of 3 friends for hectic "versus" action.

## Credits

### Programming
* [Rob Schluntz]
* [Travis Bartlett]
* [Kevin Wu]
* [Abraham Dubrisingh]

### Art
* Abraham Dubrisingh, using [SumoPaint](https://www.sumopaint.com/)

### Special Thanks
* [Egyptian Nights](http://www.fontspace.com/jonathan-s-harris/egyptian-nights) font created by Jonathan S. Harris
* [Ringbearer](http://www.fontspace.com/pete-klassen/ringbearer) font created by Pete Klassen
<!-- * This game was heavily inspired by games such as  and other classic shoot 'em ups -->

[Rob Schluntz]: https://github.com/saitou1024
[Abraham Dubrisingh]: https://github.com/Greatrabe
[Kevin Wu]: https://github.com/smashkevin
[Erin Blackmere]: https://github.com/erin2kb
[Travis Bartlett]: https://github.com/kjifs

[action]: images/action.png
[video]: https://www.youtube.com/watch?v=AbDVLvt-qY8
[video image]: images/video.png

[logo]: src/main/resources/img/FotB-Logo.png
[releases]: ../../releases
[sbt]: http://www.scala-sbt.org/
