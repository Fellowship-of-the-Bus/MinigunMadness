package com.github.fellowship_of_the_bus
package mgm
package game

import lib.game.{IDMap, IDFactory}
import rapture.json._
// import rapture.json.jsonBackends.jackson._

import org.newdawn.slick.{GameContainer, Graphics}

import lib.ui.{Drawable}
import lib.game.GameConfig.{Width}
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

case object players extends IDMap[PlayerID, PlayerAttributes]("data/players.json")

class Player(xc: Float, yc: Float, base: PlayerAttributes) extends GameObject(xc, yc) {
  def maxHp = base.maxHp
  var hp: Float = maxHp
  def attack = base.attack
  def speed = base.speed

  def height = 1.0f
  def width = 1.0f
  def velocity: (Float, Float) = (1.0f, 1.0f)


  def move(xamt: Float, yamt: Float) = {
    // super.move(xamt, yamt)
    // x = clamp(x, 0, Width-width)
    // y = clamp(y, 0, GameArea.height-height)
  }


}
