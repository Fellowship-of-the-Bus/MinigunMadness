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
  val imageList = num match {
    case 0 => List(images(Player1Walk), images(Player1Jetpack))
    case 1 => List(images(Player2Walk), images(Player2Jetpack))
    case 2 => List(images(Player3Walk), images(Player3Jetpack))
    case 3 => List(images(Player4Walk), images(Player4Jetpack))
    case _ => List(images(Player1Jetpack), images(Player2Jetpack))
  }

  {
    val scaleFactor = Width/(25f * 800f)
    imageList(0).scaleFactor = scaleFactor
    imageList(1).scaleFactor = scaleFactor
  }

  var imageIndex = 0

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
  def fuelConsumption = base.fuelConsumption
  def fuelRecovery = base.fuelRecovery
  def jetpackVelocity: (Float, Float) = {
    if (fuel > 0) {
      (jetpackSpeed, jetpackSpeed)
    } else {
      velocity
    }
  }
  def jetpackActive = jetpackOn && fuel >= fuelConsumption

  var shooting = false

  lazy val height = image.getHeight
  lazy val width = image.getWidth
  var xvel = speed
  var yvel = 0f
  def velocity: (Float, Float) = (xvel, yvel)

  val shape = new Rectangle(0,0,width,height)
  def facingRight = (gunAngle <= 90 || gunAngle >= 270)
  def mesh = shape

  var onBlock = false

  val GravityAcceleration = 0.1f

  def move(xamt: Float, yamt: Float) = {
    x += xamt
    y += yamt
    x = clamp(x, 0, Width-width)
    y = clamp(y, 0, Height-height)
    if (!onBlock && !jetpackOn) {
      yvel += GravityAcceleration
    } else {
      yvel = 0f
    }
  }

  def draw() = {
    if (active) image.draw(x,y,facingRight)
  }

  def update(delta: Int, g: Game) = {
    val amt =
      if (jetpackActive) -fuelConsumption
      else fuelRecovery

    fuel = clamp(fuel+amt, 0, maxFuel)
    if (jetpackOn && fuel < fuelConsumption) imageIndex = 0
    image.update(delta)
    if (!onBlock && !jetpackOn) {
      val (minx,miny) = g.collision(this,0,yvel.toInt)
      //if (miny < yvel) onBlock = true
      move(0,miny)
    }
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
