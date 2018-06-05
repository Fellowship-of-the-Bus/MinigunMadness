package com.github.fellowship_of_the_bus
package mgm
package game

import scala.math._
import java.lang.Math.hypot

import rapture.json._
import rapture.json.jsonBackends.jackson._

import org.newdawn.slick.{GameContainer, Graphics}
import org.newdawn.slick.geom.{Rectangle}

import lib.slick2d.ui.{Drawable}
import lib.game.{IDMap, IDFactory}
import lib.game.GameConfig.{Width,Height}
import lib.game.GameConfig
import lib.util.{TickTimer,TimerListener,RepeatForever}
import lib.math.clamp

import mgm.state.ui.{Controller, AI}

class TrainingDummy(getPlayer: () => Player, g: Game) extends AI {
  def player: Player = getPlayer()

  def getClosestPlayer(): Option[Player] = {
    val (x1, y1) = player.centerCoord()
    val distanceList = for {
      p <- g.playerList
      if p != player
      if p.active
      (x2, y2) = p.centerCoord()
      dist = hypot(x2-x1, y2-y1)
    } yield (p, dist)
    if (distanceList.isEmpty) {
      return None
    }
    val (p: Player, _) = distanceList.minBy(_._2)
    Some(p)
  }

  val shotTime = 20
  // add a little bit of variance to enemy movement to make them less synchronized
  val moveTime = 30+(Math.random()*10).toInt
  private val timer = new TimerListener{}
  timer += new TickTimer(shotTime, () => {
    player.shooting = ! player.shooting
  }, RepeatForever)
  timer += new TickTimer(moveTime, () => {
    val (x, y) = randomPoint(minDist)
    destx = x
    desty = y
  }, RepeatForever)

  def closestPoint(radius: Int): (Double, Double) = {
    // get closest point on circle centered at (closestx, closesty)
    val (px, py) = player.centerCoord
    val vx = px - closestx;
    val vy = py - closesty;
    val mag = hypot(vx, vy);
    (closestx + vx/mag*radius, closesty + vy/mag*radius)
  }

  def randomPoint(radius: Int): (Double, Double) = {
    // get a random point on circle centered at (closestx, closesty)
    val angle = Math.random()*Math.PI*2
    val x = Math.cos(angle)*radius
    val y = Math.sin(angle)*radius
    (closestx + x, closesty + x)
  }

  val minDist = 200 // try to keep distance of at least minDist
  // current destination, updated every moveTime ticks
  private var destx = 0.0
  private var desty = 0.0

  // coordinates of closest enemy
  private var closestx = 0f
  private var closesty = 0f

  override def update(delta: Int) = if (player.active) {
    // ai uses jetpack whenever they're below a treshold.
    // causes ai to bounce around a bit, but at least they don't die or waste the jetpack.
    if (player.y > Height/2) player.jetpackOn = true
    else player.jetpackOn = false

    // find closest player
    val closest = getClosestPlayer()

    // aim at closest player
    val (cx: Float, cy: Float) = closest.map(_.topLeftCoord()).getOrElse((0f, 0f))
    closestx = cx
    closesty = cy
    player.turnGunToward(closestx, closesty)

    // shoot in burst
    timer.tick(delta)
    if (player.shooting && player.active) g.bulletList = player.shoot()::g.bulletList

    // normalize destination vector because moveBy takes input in [-1, 1]
    val vx = destx-player.x
    val vy = desty-player.y
    val mag = hypot(vx, vy)
    if (mag != 0) {
      val mx = (vx/mag).toFloat
      val my = (vy/mag).toFloat
      // if AI wants to go up, it needs to use the jetpack
      if (my < 0) player.jetpackOn = true
      // move towards closest player
      player.moveBy(g, mx, my)
    }
  }

  override def toString() = s"Training Dummy for player ${player.num}"
}
