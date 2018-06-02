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
import scala.math._


object Battle extends SlickBasicGameState {
  implicit val gamepadMapping = {
    import ControllerInput._
    import ControllerInput.Implicits._
    defaultGamepadMapping +
      (BUTTON_LB -> Action1) +
      (BUTTON_LT -> Action1) +
      (BUTTON_RB -> Action2) +
      (BUTTON_RT -> Action2)
  }


  var game: Game = null

  val font = new TrueTypeFont(new Font("Verdana", Font.BOLD, 20), true)

  val stockFormat = {
    import java.text._
    val symbols = new DecimalFormatSymbols()
    symbols.setInfinity("Inf")
    new DecimalFormat("####", symbols)
  }

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

  var controllerManager: ControllerManager = null

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int) = {
    if (! gc.isPaused) {
      if (controllerManager != null) {
        controllerManager.update(delta)
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

    font.drawString(0, 0, "Score: ", Color.gray)
    for (idx <- 0 until score.length) {
      font.drawString((idx+1)*Width/5, 0, s"${score(idx)}", playerColor(idx))
    }
    val y = font.getHeight("Score: 0123456789") // distance between text items
    font.drawString(0, y, "Stock: ", Color.gray)
    for (idx <- 0 until score.length) {
      font.drawString((idx+1)*Width/5, y, s"${stockFormat.format(game.stock(idx))}", playerColor(idx))
    }

    if (game.isGameOver) {
      g.setColor(playerColor(game.winner).multiply(tint))
      g.fillRect(0, 0, Width, Height)
      // images(GameOverID).draw(0,0)
    }
  }

  import ControllerInput.Implicits.defaultKeyboardMapping
  def init(gc: GameContainer, sbg: StateBasedGame) = {
    reset(gc, sbg)
    score = new Array[Int](game.playerList.length)
  }

  def reset(gc: GameContainer, sbg: StateBasedGame) = {
    implicit val id = getID()
    game = new Game
    ui = new Pane(0, 0, 0, 0)(Color.white)
    if (controllerManager != null) controllerManager.removeListeners()
    controllerManager = new ControllerManager(gc, sbg)
    for ((ctrl, pnum) <- controllerManager.controllers.zip(0 until game.playerList.length)) {
      ctrl.controller = new BattleController(() => game.playerList(pnum), game, gc, sbg)
    }
    ui.addChildren(game.playerList.toList.map(x => new PlayerHUD(game, x.num)))
    ui.setState(getID)
    ui.resetGame(game)
    ui.init(gc, sbg)
  }

  def getID() = Mode.BattleID
}

class BattleController(getPlayer: () => Player, g: Game, gc: GameContainer, sbg: StateBasedGame) extends Controller {
  def player: Player = getPlayer()

  override def pausePressed(): Unit = gc.setPaused(!gc.isPaused)

  def canAct(): Boolean = player.active && ! gc.isPaused

  def startJetpack(): Unit = if (canAct()) {
    player.jetpackOn = true
  }

  def startShoot(): Unit = if (canAct()) {
    player.shooting = true
  }

  def stopJetpack(): Unit = if (canAct()) {
    player.jetpackOn = false
    player.imageIndex = 0
  }

  def stopShoot(): Unit = if (canAct()) {
    player.shooting = false
  }

  override def button1Pressed(): Unit = startJetpack() // LB/LT
  override def button2Pressed(): Unit = startShoot() // RB/RT

  override def button1Released(): Unit = stopJetpack()
  override def button2Released(): Unit = stopShoot()

  override def backPressed(): Unit = if (g.isGameOver) {
    sbg.enterState(Mode.MenuID)
    Battle.reset(gc, sbg)
    // input.removeControllerListener(this)
  }

  // left stick
  var dx = 0f
  var dy = 0f
  override def axis1(x: Float, y: Float): Unit = if (canAct()) {
    // move player according to
    val (xvel, yvel) = player.velocity
    dx = (xvel * x)
    dy =
      if (player.jetpackActive) (yvel * y)
      else 0f
  }

  // right stick
  override def axis2(anglex: Float, angley: Float): Unit = if (canAct()) {
    player.turnGun(anglex, angley)
  }

  // mouse
  override def axis3(mouseX: Float, mouseY: Float): Unit = if (canAct()) {
    player.turnGunToward(mouseX, mouseY)
  }

  override def update(delta: Int): Unit = if (canAct()) {
    player.moveBy(g, dx, dy)
    if (player.shooting && player.active) g.bulletList = player.shoot()::g.bulletList
  }
}
