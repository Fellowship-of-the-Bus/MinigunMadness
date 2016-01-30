package com.github.fellowship_of_the_bus
package mgm
package state
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import game._
import lib.ui.{Image,Pane}
import lib.game.GameConfig.{Width,Height}
import ui._

object Battle extends BasicGameState {
  var game = new Game

  val ui = new Pane(0, 0, Width, Height)(Color.white)

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int) = {
    if (! gc.isPaused) {
      game.update(gc, sbg, delta)
      ui.update(gc, sbg, delta)
    }
  }
  val p1 = Platform(50,100,tetris_l,0)
  val p2 = Platform(50,400,tetris_i,0)
  val p3 = Platform(400,100,tetris_j,0)
  val p4 = Platform(400,400,tetris_t,0)
  def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    ui.render(gc, sbg, g)

    val lightBlue = new Color(150,150,255,0)
    g.setBackground(lightBlue)


    if (game.isGameOver) {
      g.setColor(new Color(255, 0, 0, (0.5 * 255).asInstanceOf[Int]))
      g.fillRect(0, 0, Width, Height)
      // images(GameOverID).draw(0,0)
    }
    p1.draw(g)
    p2.draw(g)
    p3.draw(g)
    p4.draw(g)
  }

  def init(gc: GameContainer, sbg: StateBasedGame) = {
    // ui.addChildren(GameArea, HUD)
    ui.setState(getID)
    ui.resetGame(game)
    ui.init(gc, sbg)
    val controllerInput = new ControllerInput(game, gc, sbg)
  }

  def getID() = Mode.BattleID
}
