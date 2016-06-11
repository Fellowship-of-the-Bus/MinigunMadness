package com.github.fellowship_of_the_bus
package mgm
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color, Input}
import org.newdawn.slick.state.{StateBasedGame}
import org.newdawn.slick.util.InputAdapter

import scala.math._

class ControllerInput(g: game.Game, gc: GameContainer, sbg: StateBasedGame) extends InputAdapter() {
  var input : Input = gc.getInput
  //val game = g

  import lib.game.GameConfig.{OS,MacOS,Windows}
  lazy val BUTTON_A = OS match {
    case MacOS => 15
    case _ => 1
  }
  lazy val BUTTON_B = OS match {
    case MacOS => 14
    case _ => 2
  }
  lazy val BUTTON_X = OS match {
    case MacOS => 16
    case _ => 3
  }
  lazy val BUTTON_Y = OS match {
    case MacOS => 13
    case _ => 4
  }
  lazy val BUTTON_LB = OS match {
    case MacOS => 11
    case _ => 5
  }
  lazy val BUTTON_RB = OS match {
    case MacOS => 12
    case _ => 6
  }
  lazy val BUTTON_BACK = OS match {
    case MacOS => 1
    case _ => 7
  }
  lazy val BUTTON_START = OS match {
    case MacOS => 4
    case _ => 8
  }
  lazy val BUTTON_LS = OS match {
    case MacOS => 2
    case _ => 9
  }
  lazy val BUTTON_RS = OS match {
    case MacOS => 3
    case _ => 10
  }
  lazy val BUTTON_LT = OS match {
    case MacOS => 9
    case _ => 0
  }
  lazy val BUTTON_RT = OS match {
    case MacOS => 10
    case _ => 0
  }

  private var controllers: Vector[(Int, Int)] = Vector()
  input.addControllerListener(this)
  val controllerCount = input.getControllerCount()
  for (i <- 0 until controllerCount) {
    if (input.getAxisCount(i) >= 2) {
      controllers = controllers :+ ((i, controllers.length))
    }
  }
  if (Menu.keyboardPlayer) {
    input.addKeyListener(this)
    g.setPlayers(controllers.length + 1)
  } else {
    g.setPlayers(controllers.length)
  }

  override def controllerButtonPressed(controller: Int, button: Int) = {
    if (button == BUTTON_START) {
      gc.setPaused(!gc.isPaused)
    }
    if (!gc.isPaused) {
      if (sbg.getCurrentStateID == Mode.MenuID) {
        if (button == BUTTON_A) {
          sbg.enterState(Mode.BattleID)
        }
        else if (button == BUTTON_B) {
          System.exit(0)
        }
      } else if (sbg.getCurrentStateID == Mode.BattleID) {
        val player = g.playerList(controller)
        if (player.active) {
          if (button == BUTTON_LB || button == BUTTON_LT) {
            player.jetpackOn = true
          }
          if (button == BUTTON_RB || button == BUTTON_RT) {
            player.shooting = true
          }
        }
        if (g.isGameOver) {
          if (button == BUTTON_BACK) {
            sbg.enterState(Mode.MenuID)
            Battle.reset(gc, sbg)
            input.removeControllerListener(this)
          }
        }
      }
    }
  }

  override def controllerButtonReleased(controller: Int, button: Int) = {
    if (sbg.getCurrentStateID == Mode.BattleID) {
      val player = g.playerList(controller)
      if (button == BUTTON_LB || button == BUTTON_LT) {
        player.jetpackOn = false
        player.imageIndex = 0
      }
      if (button == BUTTON_RB || button == BUTTON_RT) {
        player.shooting = false
      }
    }
  }

  lazy val AXIS_X = OS match {
    case MacOS => 0
    case _ => 1
  }
  lazy val AXIS_Y = OS match {
    case MacOS => 1
    case _ => 0
  }

  lazy val RIGHT_AXIS_X = OS match {
    case MacOS => 2
    case _ => 3
  }
  lazy val RIGHT_AXIS_Y = OS match {
    case MacOS => 3
    case _ => 2
  }

  def update() = {
    if (!gc.isPaused) {
      for ((cnum,pnum) <- controllers) {
        val p = g.playerList(pnum)
        if (p.jetpackActive) p.imageIndex = 1

        val (xvel, yvel) = p.velocity
        val dx = (xvel * input.getAxisValue(cnum,AXIS_X))
        val dy =
          if (p.jetpackActive) (yvel * input.getAxisValue(cnum,AXIS_Y))
          else 0f
        val (minx,miny) = g.collision(p,dx,dy)
        p.move(minx, miny)
        p.onBlock = (miny < dy)

        val anglex = input.getAxisValue(cnum, RIGHT_AXIS_X)
        val angley = input.getAxisValue(cnum, RIGHT_AXIS_Y)
        if (anglex != 0 || angley != 0) {
          var angle = toDegrees(atan2(anglex,angley)) - 90
          if (angle < 0) angle += 360

          val diff = angle - p.gunAngle
          if (diff > 0 && diff < 180 || diff > -360 && diff < -180) p.gunAngle += p.gunTurnRate
          else p.gunAngle -= p.gunTurnRate
          p.gunAngle = (p.gunAngle + 360) % 360
        }

        if (p.shooting && p.active) g.bulletList = g.playerList(pnum).shoot()::g.bulletList

      }

      if (controllers.length == 0 || Menu.keyboardPlayer) {
        // support single player if there are no controllers attached
        val p = g.playerList(controllers.length)
        val (xvel, yvel) = p.velocity
        val dy =
          if (p.jetpackActive) yvel
          else 0f
        val (minx, miny) = g.collision(p,(xvel * horizontal), (dy * vertical))
        p.move(minx, miny)
        p.onBlock = (miny < (yvel * vertical))
        p.gunAngle += clockwise * 1
        p.gunAngle = ( p.gunAngle + 360 )% 360

        val xVec = input.getMouseX - p.x
        val yVec = input.getMouseY - p.y

        val anglex = xVec / math.sqrt((xVec*xVec) + (yVec*yVec))
        val angley = yVec / math.sqrt((xVec*xVec) + (yVec*yVec))
        if (anglex != 0 || angley != 0) {
          var angle = toDegrees(atan2(anglex,angley)) - 90
          if (angle < 0) angle += 360

          val diff = angle - p.gunAngle
          if (diff > 0 && diff < 180 || diff > -360 && diff < -180) p.gunAngle += p.gunTurnRate
          else p.gunAngle -= p.gunTurnRate
          p.gunAngle = (p.gunAngle + 360) % 360
        }

        p.shooting = input.isMouseButtonDown(0)
        if (p.shooting && p.active) g.bulletList = p.shoot()::g.bulletList
      }
    }
  }

  var horizontal = 0
  var vertical = 0
  var clockwise = 0
  override def keyPressed(key: Int, c: Char) = {
    key match {
      // movement
      case Input.KEY_A => horizontal += -1
      case Input.KEY_D => horizontal += 1
      case Input.KEY_W => vertical += -1
      case Input.KEY_S=> vertical += 1

      // pause/unpause
      case Input.KEY_P => gc.setPaused(!gc.isPaused)

      //jetpack
      case Input.KEY_SPACE => g.playerList(controllers.length).jetpackOn = true

      //rotate gun
      case Input.KEY_E => clockwise = 1
      case Input.KEY_R => clockwise = -1
      case Input.KEY_ESCAPE => if (g.isGameOver) {
            sbg.enterState(Mode.MenuID)
            Battle.reset(gc, sbg)
          }

      case _ => clockwise = 0
    }

    if (!gc.isPaused) {
      //val player = game.players(0)
      key match {
      /*  // punch/confirm button
        case Input.KEY_A =>
          if (sbg.getCurrentStateID == Mode.MenuID) {
            sbg.enterState(Mode.BattleID)
          } else {
            //player.tryAttack(game)
          }

        // kick/cancel button
        case Input.KEY_S =>
          if (sbg.getCurrentStateID == Mode.MenuID) {
            System.exit(0)
          } else if (player.imgs.contains(player.img)) {
            player.tryAttack2(game)
          }
*/
        case _ => ()
      }
    }
  }

  override def keyReleased(key: Int, c: Char) = {
    key match {
      case Input.KEY_A => horizontal -= -1
      case Input.KEY_D => horizontal -= 1
      case Input.KEY_W => vertical -= -1
      case Input.KEY_S => vertical -= 1
      case Input.KEY_SPACE => g.playerList(controllers.length).jetpackOn = false
      case _ => ()
    }
  }

  def removeListeners() = {
    input.removeListener(this)
  }
}
