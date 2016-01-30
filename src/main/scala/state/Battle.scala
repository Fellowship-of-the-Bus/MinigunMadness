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

  val ui = new Pane(0, 0, 0, 0)(Color.white)

  var controllerInput: ControllerInput = null

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int) = {
    if (! gc.isPaused) {
      game.update(gc, sbg, delta)
      ui.update(gc, sbg, delta)
      if (controllerInput != null) {
        controllerInput.update();
      }
      for(p <- game.playerList) {
        p.update(delta)
      }
    }
  }
  val background = images(Background)
  val platform = Platform(450,450,TetrisT,0)
  def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    background.draw(0,0,Width,Height)
    ui.render(gc, sbg, g)

    if (game.isGameOver) {
      g.setColor(new Color(255, 0, 0, (0.5 * 255).asInstanceOf[Int]))
      g.fillRect(0, 0, Width, Height)
      // images(GameOverID).draw(0,0)
    }
    for (player <- game.playerList) {
      player.draw()
    }
    for (platform <- game.platformList) {
      platform.draw()
    }
  }

  def init(gc: GameContainer, sbg: StateBasedGame) = {
    ui.addChildren(game.playerList.toList.map(new PlayerHUD(_)))
    ui.setState(getID)
    ui.resetGame(game)
    ui.init(gc, sbg)
    controllerInput = new ControllerInput(game, gc, sbg)
  }

  def getID() = Mode.BattleID
}
