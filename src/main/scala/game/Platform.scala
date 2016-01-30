package com.github.fellowship_of_the_bus
package mgm
package game
import java.util.logging.{Level, Logger}
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import lib.util.Native
import lib.game.GameConfig
import lib.game.TopLeftCoordinates

import lib.ui._

trait platformType
case object tetris_l extends platformType
case object tetris_t extends platformType
case object tetris_s extends platformType
case object tetris_z extends platformType
case object tetris_o extends platformType
case object tetris_i extends platformType
case object tetris_j extends platformType



object Platform {
  def apply(xc: Int, yc: Int, shape: platformType, rotation: Int) = {
    var platform = new Platform(xc, yc, shape, rotation)
    platform
  } 
}

class Platform(xc: Int, yc: Int, var shape: platformType, var rotation: Int) extends GameObject(xc, yc) {
  // for now
  def width = 300
  def height = 400
  def velocity = (0,0)
  val ID = shape match {
      case tetris_i => IBlock
      case tetris_j => JBlock
      case tetris_l => LBlock
      case tetris_t => TBlock
      case _ => TBlock
    }
  def draw(g: Graphics) = {
    var image = images(ID)
    image.setCenterOfRotation(100, 100)
    image.setRotation(rotation)
    image.draw(x,y)
  }
}