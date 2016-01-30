package com.github.fellowship_of_the_bus
package mgm
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import lib.game.GameConfig.{Height,Width}
import lib.util.rand
import lib.util.{TickTimer,TimerListener,RepeatForever}
import scala.math.abs

class Game extends lib.game.Game with TimerListener {
  val maxPlayers = 4
  var playerList: Array[Player] = null

  var platformList: List[Platform] = List()
  platformList = Platform(0,0,TetrisI, 180)::platformList
  platformList = Platform(0, 300, TetrisJ, 270)::platformList
  platformList = Platform(300, 0, TetrisL, 180)::platformList
  platformList = Platform(300, 300, TetrisT, 180)::platformList


  def setPlayers(nplayers: Int) = {
    playerList = new Array[Player](nplayers)
    for (i <- 0 until nplayers) {
      playerList(i) = new Player(i*100, i*100, players(HumanPlayer), i)
    }
  }

  addTimer(new TickTimer(240, cleanup _, RepeatForever))

  //var projectiles = List[Projectile]()

  def cleanup() = {
    //projectiles = projectiles.filter(_.active)
  }


  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    super.update(delta)

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
}
