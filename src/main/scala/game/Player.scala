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
import scala.math._

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
  val imageList = num match {
    case 0 => List(images(Player1Walk), images(Player1Jetpack))
    case 1 => List(images(Player2Walk), images(Player2Jetpack))
    case _ => List(images(Player1Jetpack), images(Player2Jetpack))
  }

  var imageIndex = 0

  imageList(0).scaleFactor = Width/(25f * 800f)
  imageList(1).scaleFactor = Width/(25f * 800f)
  def image = imageList(imageIndex)

  def maxHp = base.maxHp
  var hp: Int = maxHp.toInt
  def attack = base.attack
  def speed = base.speed
  var gunAngle: Float = 90

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
  val minigun = images(Minigun).copy()
  minigun.scaleFactor = Width/(25f * 2273 / 1.3f )
  minigun.setCenterOfRotation(minigun.width*1f/5f, minigun.height/2)
  def draw() = {
    if (active) {
      image.draw(x,y,facingRight)

      minigun.setRotation(gunAngle)
      minigun.draw(x + image.width/2 - minigun.width*1f/5f, y + image.height/2 - minigun.height/2)

    }
  }

  def update(delta: Int) = {
    val amt =
      if (jetpackOn) -base.fuelConsumption
      else base.fuelRecovery
    fuel = clamp(fuel+amt, 0, maxFuel)
    if (fuel == 0) imageIndex = 0
    image.update(delta)
  }

  def shoot() = {
    val (additionalx, additionaly) = (minigun.width*4f/5f * cos((gunAngle*Pi)/180f), minigun.width* 4f/5f*sin((gunAngle*Pi)/180f))
    new Bullet((x + width/2f + additionalx).toInt, (y + height/2f + additionaly).toInt, gunAngle, num)
  }

  def takeDamage(damage: Int) = {
    hp -= damage
    if (hp <= 0) {
      inactivate
    }
  }
}
