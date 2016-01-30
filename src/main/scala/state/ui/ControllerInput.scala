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
  if (controllers.length == 0) {
    input.addKeyListener(this)
    g.setPlayers(1)
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
      /*} else {
        if (button == BUTTON_A) {
          game.players(controller).tryAttack(game)
        } else if (button == BUTTON_B) {
          if (game.players(controller).imgs.indexOf(game.players(controller).img) != -1) {
            game.players(controller).tryAttack2(game)
          }
        }*/
      } else if (sbg.getCurrentStateID == Mode.BattleID) {
        val player = g.playerList(controller)
        if (button == BUTTON_LB) {
          player.jetpackOn = true
        }
        if (button == BUTTON_RB) {
          player.shooting = true
        }
      }
    }
  }

  override def controllerButtonReleased(controller: Int, button: Int) = {
    if (sbg.getCurrentStateID == Mode.BattleID) {
      val player = g.playerList(controller)
      if (button == BUTTON_LB) {
        player.jetpackOn = false
      }
      if (button == BUTTON_RB) {

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
        val (xvel, yvel) =
          if (p.jetpackOn) p.jetpackVelocity
          else p.velocity
        val dx = (xvel * input.getAxisValue(cnum,AXIS_X)).toInt
        val dy = (yvel * input.getAxisValue(cnum,AXIS_Y)).toInt
        val (minx,miny) = g.collision(p,dx,dy)
        p.move(minx, miny)

        val anglex = input.getAxisValue(cnum, RIGHT_AXIS_X)
        val angley = input.getAxisValue(cnum, RIGHT_AXIS_Y)
        var angle = toDegrees(atan2(anglex,angley)) - 90
        if (angle < 0) angle += 360
        p.gunAngle = angle.toFloat;

        if (p.shooting) g.bulletList = g.playerList(pnum).shoot()::g.bulletList

      }

      if (controllers.length == 0) {
        // support single player if there are no controllers attached
        val p = g.playerList(0)
        val (xvel, yvel) = p.velocity
        val (minx, miny) = g.collision(p,(xvel * horizontal).toInt, (yvel * vertical).toInt)
        p.move(minx, miny)
      }
    }
  }

  var horizontal = 0
  var vertical = 0
  override def keyPressed(key: Int, c: Char) = {
    key match {
      // movement
      case Input.KEY_LEFT => horizontal += -1
      case Input.KEY_RIGHT => horizontal += 1
      case Input.KEY_UP => vertical += -1
      case Input.KEY_DOWN => vertical += 1

      // pause/unpause
      case Input.KEY_P => gc.setPaused(!gc.isPaused)

      //shoot
      case Input.KEY_SPACE => g.bulletList = g.playerList(0).shoot()::g.bulletList

      case _ => ()
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
      case Input.KEY_LEFT => horizontal -= -1
      case Input.KEY_RIGHT => horizontal -= 1
      case Input.KEY_UP => vertical -= -1
      case Input.KEY_DOWN => vertical -= 1
      case _ => ()
    }
  }
}
