package com.github.fellowship_of_the_bus
package mgm
package state
package ui

import lib.game.GameConfig.{Width,Height}
import lib.slick2d.ui.{Button, Drawable, ImageButton, Pane, TextBox}

import org.newdawn.slick.{GameContainer, Graphics, Color,Input}
import org.newdawn.slick.state.{StateBasedGame}

import game._

object PlayerHUD {
  implicit val color: Color = new Color(0.8f, 0.8f, 0.9f, 1f)
}
import PlayerHUD.color

class PlayerHUD(g: Game, pnum: Int) extends Pane(0, 0, 0, 0) {
  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    def player = g.playerList(pnum)
    val (width, height) = (player.width, player.height/5)
    val hp = new Bar(() => (player.x+2, player.y - 2*height), width, height, player.maxHp, () => player.hp,  (Color.green, Color.yellow, Color.red))
    val jetpack = new Bar(() => (player.x+2, player.y - height), width, height, player.maxFuel, () => player.fuel, (Color.orange, new Color(255, 165, 0), new Color(248, 128, 23)))
    setIsVisible(() => player.active)
    addChildren(hp, jetpack)
    super.init(gc, sbg)
  }
}
