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
      for(p <- game.playerList) {
        p.update(delta)
      }
    }
  }
  val background = images(Background)
  def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    ui.render(gc, sbg, g)
    background.draw(0,0,Width,Height)

    val lightBlue = new Color(150,150,255,0)
    g.setBackground(lightBlue)


    if (game.isGameOver) {
      g.setColor(new Color(255, 0, 0, (0.5 * 255).asInstanceOf[Int]))
      g.fillRect(0, 0, Width, Height)
      // images(GameOverID).draw(0,0)
    }

    for (p <- game.playerList) {
      p.draw()
    }
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
