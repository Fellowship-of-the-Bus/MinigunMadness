package com.github.fellowship_of_the_bus
package mgm
package state
package ui

import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import lib.ui.{UIElement,AbstractUIElement}

import mgm.game.{GameObject,Player}

class Lifebar(x: Float, y: Float, width: Float, height: Float, obj: Player) extends AbstractUIElement(x, y, width, height) {
  def this(x: Float, y: Float, obj: Player) = this(x+2, y, 317, 20, obj)

  def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    val color = g.getColor

    val hp = obj.hp
    val maxHp = obj.maxHp

    g.setColor(Color.black)
    g.drawRect(x, y, width, height)

    val lower = maxHp/3
    val upper = 2*maxHp/3

    if (hp > upper) {
      g.setColor(Color.green)
    } else if (hp < lower) {
      g.setColor(Color.red)
    } else {
      g.setColor(Color.yellow)
    }
    g.fillRect(x, y, width * hp/maxHp, height)

    g.setColor(color)
  }

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int): Unit = ()
}
