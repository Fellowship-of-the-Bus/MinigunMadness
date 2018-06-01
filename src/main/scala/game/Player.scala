package com.github.fellowship_of_the_bus
package mgm
package game

import lib.game.{IDMap, IDFactory}
import rapture.json._
import rapture.json.jsonBackends.jackson._

import org.newdawn.slick.{GameContainer, Graphics}
import org.newdawn.slick.geom.{Rectangle}

import lib.slick2d.ui.{Drawable}
import lib.game.GameConfig.{Width,Height}
import lib.game.GameConfig
import lib.util.{TickTimer,TimerListener,FireN}
import lib.math.clamp
import scala.math._

sealed trait PlayerID
case object HumanPlayer extends PlayerID
case object AIPlayer extends PlayerID
case class PlayerAttributes(
  maxHp: Float,
  attack: Float,
  moveSpeed: Float,
  shotMoveSpeedPenalty: Float, // ratio applied to movement/jetpack speed when shooting
  maxFuel: Float,
  jetpackSpeed: Float,
  fuelConsumption: Float,
  fuelRecovery: Float,
  fuelThreshold: Float,       // Amount of fuel required to turn on jetpack
  gunTurnRate: Float,         // angle/frame that gun changes while not shooting
  shotGunTurnRate: Float      // angle/frame that gun changes while shooting
)

object PlayerAttributes {
  // not supposed to need this, but for some reason, the compiler isn't finding it
  implicit lazy val extractor: Extractor[PlayerAttributes, Json] =
    Json.extractor[Json].map {
      case json"""{ "maxHp": $maxHp, "attack": $attack, "moveSpeed": $moveSpeed,
        "shotMoveSpeedPenalty": $shotMoveSpeedPenalty, "maxFuel": $maxFuel,
        "jetpackSpeed": $jetpackSpeed, "fuelRecovery": $fuelRecovery, "fuelConsumption": $fuelConsumption,
        "fuelThreshold": $fuelThreshold, "gunTurnRate": $gunTurnRate, "shotGunTurnRate": $shotGunTurnRate }""" =>
        PlayerAttributes(
          maxHp.as[Float],
          attack.as[Float],
          moveSpeed.as[Float],
          shotMoveSpeedPenalty.as[Float],
          maxFuel.as[Float],
          jetpackSpeed.as[Float],
          fuelConsumption.as[Float],
          fuelRecovery.as[Float],
          fuelThreshold.as[Float],
          gunTurnRate.as[Float],
          shotGunTurnRate.as[Float]
        )
    }
}

object PlayerID {
  implicit object Factory extends IDFactory[PlayerID] {
    val ids = Vector(HumanPlayer, AIPlayer)
  }
  implicit lazy val extractor =
    Json.extractor[String].map(Factory.fromString(_))
}

case object players extends IDMap[PlayerID, PlayerAttributes]("data/player.json")

class Player(xc: Float, yc: Float, val base: PlayerAttributes, val num: Int) extends GameObject(xc, yc) {
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

  def gunTurnRate =
    if (shooting) base.shotGunTurnRate
    else base.gunTurnRate


  def maxHp = base.maxHp
  var hp: Int = maxHp.toInt
  def attack = base.attack
  def speed = base.moveSpeed * shotMoveSpeedPenalty
  var gunAngle: Float = 90

  def shotMoveSpeedPenalty =
    if (shooting) base.shotMoveSpeedPenalty
    else 1

  def maxFuel = base.maxFuel
  var fuel: Float = base.maxFuel
  def jetpackSpeed = base.jetpackSpeed
  var jetpackOn = false
  def fuelConsumption = base.fuelConsumption
  def fuelRecovery = base.fuelRecovery
  def fuelThreshold = base.fuelThreshold

  private def jetpackVelocity: (Float, Float) = {
    val v = jetpackSpeed*shotMoveSpeedPenalty
    (v, v)
  }

  // if jetpack is currently active, only need to have enough fuel for consumption, otherwise there is a startup cost
  var prevJetpackActive = false
  def jetpackActive = ((prevJetpackActive && jetpackOn && fuel >= fuelConsumption) || (jetpackOn && fuel >= fuelThreshold))

  var shooting = false

  lazy val height = image.getHeight
  lazy val width = image.getWidth
  var yvel = 0f
  def velocity: (Float, Float) =
    if (jetpackActive) jetpackVelocity
    else (speed, yvel)

  def facingRight = (gunAngle <= 90 || gunAngle >= 270)
  val mesh = new Rectangle(0,0,width,height)

  var onBlock = false

  val GravityAcceleration = 0.1f

  // player num for player that hit this most recently
  var mostRecentAttacker: Int = -1

  def move(xamt: Float, yamt: Float) = {
    x += xamt
    y += yamt
    x = clamp(x, 0, Width-width)
    y = max(y, 0)
    if (y > Height)
      inactivate() // fall off the bottom
    if (!onBlock && !jetpackActive) {
      yvel += GravityAcceleration
    } else {
      yvel = 0f
    }
  }
  val minigun = images(Minigun).copy()
  minigun.scaleFactor = Width/(25f * 2273 / 1.3f )
  minigun.setCenterOfRotation(minigun.width*1f/5f, minigun.height/2)
  def draw() = {
    if (active) {
      image.draw(x,y,facingRight)

      minigun.setRotation(-gunAngle)
      minigun.draw(x + image.width/2 - minigun.width*1f/5f, y + image.height/2 - minigun.height/2)
    }
  }

  def update(delta: Int, g: Game) = {
    val amt =
      if (jetpackActive) -fuelConsumption
      else fuelRecovery

    fuel = clamp(fuel+amt, 0, maxFuel)
    imageIndex = if (jetpackActive) 1 else 0

    prevJetpackActive = jetpackActive
    image.update(delta)
    if (!onBlock && !jetpackActive) {
      val (_,miny) = g.collision(this,0,yvel)
      if (miny < yvel) {
        onBlock = true
      }
      else move(0,miny)
    }
  }

  def shoot() = {
    val (additionalx, additionaly) = (minigun.width*4f/5f * cos((-gunAngle*Pi)/180f) - width/2f, minigun.width* 4f/5f*sin((-gunAngle*Pi)/180f))
    new Bullet((x + width/2f + additionalx).toInt, (y + height/2f + additionaly).toInt, gunAngle, num)
  }

  def takeDamage(bullet: Bullet) = {
    mostRecentAttacker = bullet.playerNum
    hp -= bullet.damage
    if (hp <= 0) {
      inactivate
    }
  }

  // callback upon death of character
  var onDeath = (player: Player) => ()
  override def inactivate(): Unit = {
    if (active) {
      super.inactivate()
      onDeath(this)
    }
  }
}
