package com.github.fellowship_of_the_bus
package mgm
package game

import lib.game.{IDMap, IDFactory}
import rapture.json._

import org.newdawn.slick.{GameContainer, Graphics}
import org.newdawn.slick.geom.{Rectangle}

import lib.ui.{Drawable}
import lib.game.GameConfig.{Width,Height}
import lib.game.GameConfig
import lib.util.{TickTimer,TimerListener,FireN}
import lib.math.clamp

sealed trait PlayerID
case object HumanPlayer extends PlayerID
case class PlayerAttributes(
  maxHp: Float,
  attack: Float,
  speed: Float,
  maxFuel: Float,
  jetpackSpeed: Float,
  fuelConsumption: Float,
  fuelRecovery: Float
)

object PlayerID {
  implicit object Factory extends IDFactory[PlayerID] {
    val ids = Vector(HumanPlayer)
  }
  implicit lazy val extractor =
    Json.extractor[String].map(Factory.fromString(_))
}

case object players extends IDMap[PlayerID, PlayerAttributes]("data/player.json")

class Player(xc: Float, yc: Float, base: PlayerAttributes, val num: Int) extends GameObject(xc, yc) {
  val image = num match {
    case 0 => images(Player1Walk)
    case 1 => images(Player2Walk)
    case _ => images(Player1Jetpack)
  }
  image.scaleFactor = Width/(25f * 800f)

  def maxHp = base.maxHp
  var hp: Int = maxHp.toInt
  def attack = base.attack
  def speed = base.speed
  var gunAngle: Float = 45

  def maxFuel = base.maxFuel
  var fuel: Float = base.maxFuel
  def jetpackSpeed = base.jetpackSpeed
  var jetpackOn = false
  def jetpackVelocity: (Float, Float) = {
    if (fuel > 0) {
      (jetpackSpeed, jetpackSpeed)
    } else {
      velocity
    }
  }

  var shooting = false

  lazy val height = image.getHeight
  lazy val width = image.getWidth
  def velocity: (Float, Float) = (speed, speed)

  val shape = new Rectangle(0,0,width,height)
  def facingRight = (gunAngle <= 90 || gunAngle >= 270)
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
    val amt =
      if (jetpackOn) -base.fuelConsumption
      else base.fuelRecovery
    fuel = clamp(fuel+amt, 0, maxFuel)
    image.update(delta)
  }

  def shoot() = {
    new Bullet(x + width/2, y + height/2, gunAngle, num)
  }

  def takeDamage(damage: Int) = {
    hp -= damage
    if (hp <= 0) {
      inactivate
    }
  }
}
