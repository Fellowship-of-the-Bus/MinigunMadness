package com.github.fellowship_of_the_bus
package mgm
package state
package ui

import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import lib.ui.{UIElement,AbstractUIElement}

import mgm.game.{GameObject,Player}

class Bar(location: () => (Float, Float), wd: Float, ht: Float, maxValue: Float, value: () => Float, colors: (Color, Color, Color)) extends UIElement {
  lazy val width: Float = wd
  lazy val height: Float = ht/10
  def x = {
    val (x, _) = location()
    x+2
  }

  def y = {
    val (_, y) = location()
    y
  }

  def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    val color = g.getColor

    val v = value()

    g.setColor(Color.black)
    g.drawRect(x, y, width, height)

    val lower = maxValue/3
    val upper = 2*maxValue/3

    val (c1, c2, c3) = colors

    if (v > upper) {
      g.setColor(c1)
    } else if (v < lower) {
      g.setColor(c3)
    } else {
      g.setColor(c2)
    }
    g.fillRect(x, y, width * v/maxValue, height)
    println(s"drawing at $x $y")

    g.setColor(color)
  }

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int): Unit = ()
}
