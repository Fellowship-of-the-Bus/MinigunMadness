package com.github.fellowship_of_the_bus
package mgm
package state
package ui

import lib.game.GameConfig.{Width,Height}
import lib.ui.{Button, Drawable, ImageButton, Pane, TextBox}

import org.newdawn.slick.{GameContainer, Graphics, Color,Input}
import org.newdawn.slick.state.{StateBasedGame}

import game._

object PlayerHUD {
  implicit val color: Color = new Color(0.8f, 0.8f, 0.9f, 1f)
}
import PlayerHUD.color

class PlayerHUD(player: Player) extends Pane(0, 0, 0, 0) {
  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    val (width, height) = (player.width, player.height)
    val hp = new Bar(() => (player.x, player.y - height/10), width, height, player.maxHp, () => player.hp,  (Color.green, Color.yellow, Color.red))
    val jetpack = new Bar(() => (player.x, player.y - height/20), width, height, player.maxHp, () => player.hp, (Color.blue, Color.blue, Color.blue))
    setIsVisible(() => player.active)
    addChildren(hp, jetpack)
    super.init(gc, sbg)
  }
}
