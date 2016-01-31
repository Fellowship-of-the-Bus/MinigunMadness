package com.github.fellowship_of_the_bus
package mgm
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}
import org.newdawn.slick.geom.{Polygon, Transform}

import lib.game.GameConfig.{Height,Width}
import lib.util.rand
import lib.util.{TickTimer,TimerListener,RepeatForever}
import scala.math.abs

class Game extends lib.game.Game with TimerListener {
  val maxPlayers = 4
  var playerList: Array[Player] = null

  var platformList: List[Platform] = List()
  var bulletList: List[Bullet] = List()

  val nColumns = 5
  val nRows = 4
  val areaDimension = Width/5
  for (i <- 0 until nColumns) {
    for (k <- 0 until nRows) {
      if (k == 1)
        platformList = Platform(i * areaDimension, k * areaDimension,TetrisT,0)::platformList
      else
        platformList = genPlatform(i, k)::platformList
    }
  }
  // platformList = Platform(0,0,TetrisI, 180)::platformList
  // platformList = Platform(0, 300, TetrisJ, 270)::platformList
  // platformList = Platform(300, 0, TetrisL, 180)::platformList
  // platformList = Platform(300, 300, TetrisT, 180)::platformList

  def setPlayers(nplayers: Int) = {
    playerList = new Array[Player](nplayers)
    for (i <- 0 until nplayers) {
      playerList(i) = new Player((0.5f + i) * areaDimension, areaDimension, players(HumanPlayer), i)
    }
  }

  addTimer(new TickTimer(240, cleanup _, RepeatForever))

  //var projectiles = List[Projectile]()

  def cleanup() = {
    //projectiles = projectiles.filter(_.active)
    bulletList = bulletList.filter(_.active)
  }


  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    super.update(delta)

    for (bullet <- bulletList.filter(_.active)) {
      // val (dx, dy) = collision(bullet, bullet.xVel.toInt, bullet.yVel.toInt)
      // if (dx != bullet.xVel || dy != bullet.yVel) {
      //   bullet.inactivate
      // }
      for (platform <- platformList) {
        if (collision(bullet, platform)) {
          bullet.inactivate
        }
      }
      for (player <- playerList.filter(_.active)) {
        if (bullet.playerNum != player.num && collision(bullet, player)) {
          player.takeDamage(1)
          bullet.inactivate
        }
      }
    }


    // var minx: Int = x
    // var miny: Int = y
    // for (platform <- platformList) {
    //   val (vx, vy): (Int, Int) = platform.collision(playerList(0), (x, y))
    //   if (abs(vx) < abs(minx)) {
    //     minx = vx
    //   }
    //   if (abs(vy) < abs(miny)) {
    //     miny = vy
    //   }
    // }
    // playerList(0).move(minx, miny)

    /*for (p <- projectiles; if (p.active)) {
      p.update(delta, this)
    }
    for (p <- players; if (p.active)) {
      p.update(delta, this)
    }*/
  }

  def genPlatform(row: Int, column: Int) = {
    val typeNum = rand(4)
    val platformType = typeNum match {
      case 0 => 
       TetrisI
      case 1 =>
       TetrisJ
      case 2 =>
       TetrisL
      case 3 =>
       TetrisT
    }
    Platform(row * areaDimension, column * areaDimension, platformType, rand(4) * 90)
  }

  def collision(go: GameObject, dx: Int, dy: Int) = {
    var minx = dx
    var miny = dy
    for (platform <- platformList) {
      val (vx, vy): (Int, Int) = platform.collision(go, (dx, dy))
      if (abs(vx) < abs(minx)) {
        minx = vx
      }
      if (abs(vy) < abs(miny)) {
        miny = vy
      }
    }
    (minx, miny)
  }

  def collision(go1: GameObject, go2: GameObject): Boolean = {
    //translate meshes to actual location
    val go1Mesh = go1.mesh.transform(Transform.createTranslateTransform(go1.x, go1.y))
    val go2Mesh = go2.mesh.transform(Transform.createTranslateTransform(go2.x, go2.y))
    return go1Mesh.contains(go2Mesh)||go2Mesh.contains(go1Mesh)||go1Mesh.intersects(go2Mesh)
    //normalize game object 2
    // val go2NormalizedX = go2.x - go1.x
    // val go2NormalizedY = go2.y - go1.y
    // val go2NormalizedMesh = go2.mesh.transform(Transform.createTranslateTransform(go2NormalizedX, go2NormalizedY))
    // return go1.mesh.intersects(go2NormalizedMesh)
  }

  var winner = -1
  override def gameOver() = {
    super.gameOver()
    for (p <- playerList; if (p.active)) {
      winner = p.num
    }
  }
}
