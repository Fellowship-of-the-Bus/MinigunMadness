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
import lib.util.{TickTimer,TimerListener,RepeatForever}
import lib.math.clamp
import scala.math._
import mgm.state.ui.{Controller, AI}

class TrainingDummy(getPlayer: () => Player, g: Game) extends AI {
  def player: Player = getPlayer()

  def getClosestPlayer(): Option[Player] = {
    import java.lang.Math.{hypot => distance}
    val (x1, y1) = player.centerCoord()
    val distanceList = for {
      p <- g.playerList
      if p != player
      if p.active
      (x2, y2) = p.centerCoord()
      dist = distance(x2-x1, y2-y1)
    } yield (p, dist)
    if (distanceList.isEmpty) {
      return None
    }
    val (p: Player, _) = distanceList.minBy(_._2)
    Some(p)
  }

  val shotTime = 20
  val shootTimer = new TimerListener{}
  shootTimer += new TickTimer(shotTime, () => {
    player.shooting = ! player.shooting
  }, RepeatForever)

  override def update(delta: Int) = if (player.active) {
    // simplest, but not most effective, if ai is always using jetpack
    if (player.y > Height/2) player.jetpackOn = true
    else player.jetpackOn = false

    // find closest player
    val closest = getClosestPlayer()

    // aim at closest player
    val (cx: Float, cy: Float) = closest.map(_.topLeftCoord()).getOrElse((0f, 0f))
    player.turnGunToward(cx, cy)

    // shoot in burst
    shootTimer.tick(delta)
    if (player.shooting && player.active) g.bulletList = player.shoot()::g.bulletList

    // move towards closest player
    player.moveBy(g, player.num+1, -(player.num+1))
  }
}
