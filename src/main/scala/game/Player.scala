package com.github.fellowship_of_the_bus
package mgm
package game

import lib.game.{IDMap, IDFactory}
import rapture.json._
// import rapture.json.jsonBackends.jackson._

import org.newdawn.slick.{GameContainer, Graphics}
import org.newdawn.slick.geom.{Rectangle}

import lib.ui.{Drawable}
import lib.game.GameConfig.{Width,Height}
import lib.game.GameConfig
import lib.util.{TickTimer,TimerListener,FireN}
import lib.math.clamp

sealed trait PlayerID
case object HumanPlayer extends PlayerID
case class PlayerAttributes(maxHp: Float, attack: Float, speed: Float)

object PlayerID {
  implicit object Factory extends IDFactory[PlayerID] {
    val ids = Vector(HumanPlayer)
  }
  implicit lazy val extractor =
    Json.extractor[String].map(Factory.fromString(_))
}

case object players extends IDMap[PlayerID, PlayerAttributes]("data/player.json")

class Player(xc: Float, yc: Float, base: PlayerAttributes, num: Int) extends GameObject(xc, yc) {
  val image = num match {
    case 0 => images(Player1Walk)
    case 1 => images(Player2Walk)
    case _ => images(Player1Jetpack)
  }
  image.scaleFactor = 0.2f

  def maxHp = base.maxHp
  var hp: Float = maxHp
  def attack = base.attack
  def speed = base.speed

  lazy val height = image.getHeight
  lazy val width = image.getWidth
  def velocity: (Float, Float) = (1.0f, 1.0f)

  val shape = new Rectangle(0,0,width,height)
  var facingRight = true
  def mesh = shape

  def move(xamt: Float, yamt: Float) = {
     x += xamt
     y += yamt
     x = clamp(x, 0, Width-width)
     y = clamp(y, 0, Height-height)
  }

  def draw() = {
    image.draw(x,y,facingRight)
  }

  def update(delta: Int) = {
    image.update(delta)
  }

}
