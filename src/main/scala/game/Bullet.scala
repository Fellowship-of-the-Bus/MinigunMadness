package com.github.fellowship_of_the_bus
package mgm
package game

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.geom.{Rectangle}

import lib.ui.{Drawable}
import lib.game.GameConfig.{Width,Height}
import lib.game.GameConfig
import lib.util.{TickTimer,TimerListener,FireN}
import lib.math.clamp
import scala.math._

class Bullet(xc: Float, yc: Float, angle: Float, var playerNum: Int) extends GameObject(xc, yc) {
  val rad = -angle * (math.Pi / 180f).toFloat
  val speed = 15f
  val xVel = (speed * math.cos(rad).toFloat)
  val yVel = speed * math.sin(rad).toFloat

  val velocity: (Float, Float) = (xVel, yVel)

  var scaleFactor = 0.1f

  val width = images(Bullet).width*scaleFactor // To do: base width on game size
  val height = images(Bullet).height*scaleFactor


  val image = images(Bullet).copy
  image.setCenterOfRotation(width/2, height/2)
  image.setRotation(-angle+180)
  image.scaleFactor = scaleFactor



  val shape = new Rectangle(0,0,width,height)
  def mesh = shape

  override def move() {
    // Check Collsion
    // If collides with player (with number != playerNum): deal damage and deactivate
    // If collides with platform/Edge of map: deactivate
    // Else:
    if (x < 0 || x > GameConfig.Width || y < 0 || y > GameConfig.Height) {
      inactivate
    }
    x = x + xVel
    y = y + yVel
  }

  def draw(g:Graphics) = {
    if (active) {
      image.draw(x, y)
      var (prevx, prevy): (Float, Float) = (0-1,-1)
      for (i <- 0 until mesh.getPointCount()) {
        val px = mesh.getPoint(i)(0)
        val py = mesh.getPoint(i)(1)

        if (prevx == -1 && prevy == -1) {
          prevx = px
          prevy = py
        } else {
          g.drawGradientLine(x+prevx, y+prevy, Color.green, x+px, y+py, Color.green)
          prevx = px
          prevy = py
        }
      }
    }
  }
}
