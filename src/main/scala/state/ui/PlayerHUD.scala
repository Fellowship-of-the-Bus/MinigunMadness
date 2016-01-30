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

class PlayerHUD(x: Float, y: Float, width: Float, height: Float, player: Player, playerColor: Color) extends Pane(x, y, width, height) {
  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    val hp = new Lifebar(0, 0, player)
    setIsVisible(() => player.active)
    addChildren(hp)
    super.init(gc, sbg)
  }
}
