package com.github.fellowship_of_the_bus
package mgm
package state
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input, TrueTypeFont}
import org.newdawn.slick.state.{BasicGameState => SlickBasicGameState, StateBasedGame}
import java.awt.Font

import game._
import lib.slick2d.ui.{Image,Pane}
import lib.game.GameConfig.{Width,Height}
import ui._


object Battle extends SlickBasicGameState {
  var game: Game = null

  val font = new TrueTypeFont(new Font("Verdana", Font.BOLD, 20), true)

  val playerColor = Array(
    new Color(255, 0, 0),
    new Color(0, 0, 255),
    new Color(0, 255, 0),
    new Color(255, 255, 255),
    new Color(0, 0, 0) // draw
  )
  val tint = new Color(255, 255, 255, (0.5 * 255).asInstanceOf[Int])

  var score = Array[Int]()

  var ui: Pane = null

  var controllerInput: ControllerInput = null

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int) = {
    if (! gc.isPaused) {
      if (controllerInput != null) {
        controllerInput.update()
      }
      for(p <- game.playerList) {
        p.update(delta,game)
      }
      for(bullet <- game.bulletList) {
        bullet.move()
      }
      for (platform <- game.platformList) {
        platform.move();
        if (platform.active) {
          if (platform.y > Height) {
            platform.inactivate();
            game.platformList = game.genPlatform((platform.x / game.areaDimension).toInt, -1)::game.platformList
          }
          for(p <- game.playerList) {
            if (game.collision(p, platform)) {
              p.move(0, 1)
            }
          }
        }
      }
      game.update(gc, sbg, delta)
      ui.update(gc, sbg, delta)
    }
  }
  val background = images(Background)
  background.scaleFactor = Width/ background.width
  def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    background.draw(0,0)
    ui.render(gc, sbg, g)

    for (player <- game.playerList) {
      player.draw()
    }
    for (platform <- game.platformList) {
      platform.draw(g)
    }
    for(bullet <- game.bulletList) {
      bullet.draw(g)
    }

    for (idx <- 0 until score.length) {
      font.drawString(idx*Width/4, 0, s"${score(idx)}", playerColor(idx))
    }

    if (game.isGameOver) {
      g.setColor(playerColor(game.winner).multiply(tint))
      g.fillRect(0, 0, Width, Height)
      // images(GameOverID).draw(0,0)
    }
  }

  def init(gc: GameContainer, sbg: StateBasedGame) = {
    reset(gc, sbg)
    score = new Array[Int](game.playerList.length)
  }

  def reset(gc: GameContainer, sbg: StateBasedGame) = {
    game = new Game
    ui = new Pane(0, 0, 0, 0)(Color.white)
    if (controllerInput != null) controllerInput.removeListeners()
    controllerInput = new ControllerInput(game, gc, sbg)
    ui.addChildren(game.playerList.toList.map(x => new PlayerHUD(game, x.num)))
    ui.setState(getID)
    ui.resetGame(game)
    ui.init(gc, sbg)
  }

  def getID() = Mode.BattleID
}
