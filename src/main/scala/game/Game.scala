package com.github.fellowship_of_the_bus
package mgm
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}
import org.newdawn.slick.geom.{Polygon, Transform}

import lib.game.GameConfig.{Height,Width}
import lib.util.rand
import lib.util.{TickTimer,TimerListener,RepeatForever}
import scala.math.{abs, max}

class Game(val maxScore: Double) extends lib.slick2d.game.Game with TimerListener {

  val maxPlayers = 4
  var playerList: Array[Player] = (for {
    i <- 0 until maxPlayers
    p = new Player(0, 0, players(HumanPlayer), i)
    _ = p.onDeath = onDeathCallback _
  } yield p).toArray
  var stock: Array[Double] = Array.fill(maxPlayers)(state.Settings.stock)
  var score: Array[Int] = Array.fill(maxPlayers)(0)
  var currentScore = 0

  var platformList: List[Platform] = List()
  var bulletList: List[Bullet] = List()

  var colOffsets: Array[Float] = new Array[Float](5)
  colOffsets(0) = 0f
  colOffsets(1) = -0.5f
  colOffsets(2) = 0f
  colOffsets(3) = -0.5f
  colOffsets(4) = 0f
  val nColumns = 5
  val nRows = 4
  val areaDimension = Width/5
  for (i <- 0 until nColumns) {
    for (k <- -1 until nRows) {
      if (k == 1)
        platformList = Platform(i * areaDimension, (k + colOffsets(i)) * areaDimension,TetrisT,0)::platformList
      else
        platformList = genPlatform(i, k)::platformList
    }
  }
  colOffsets(1) = 0f
  colOffsets(3) = 0f

  def setRespawnLocation(player: Player): Unit = {
    val xval = player.num * areaDimension
    var highestVal = Height.toFloat
    var highestPlat: Platform = null
    // filter out platforms not in the correct column
    for (platform <- platformList; if (platform.x >= xval-0.005 && platform.x <= xval+0.005)) {
      if (platform.y < highestVal) {
        highestVal = platform.y
        highestPlat = platform
      }
    }
    if (highestPlat != null) {
      player.x = (0.25f + player.num) * areaDimension
      val (_, y) = highestPlat.topLeftCoord()
      player.y = highestPlat.y + highestPlat.height
    }
  }

  for (p <- playerList) {
    p.x = (0.25f + p.num) * areaDimension
    p.y = areaDimension.toFloat * (1f + (-0.5f*(p.num % 2)))
  }

  val respawnDelay = 60*3 // respawn after 3 seconds
  def respawnPending = respawnTimer.ticking()
  def canRespawn(player: Player) = stock(player.num) > 0
  val respawnTimer = new TimerListener{}
  def respawn(player: Player) = {
    playerList(player.num) = new Player(0, 0, player.base, player.num)
    setRespawnLocation(playerList(player.num))
    playerList(player.num).onDeath = onDeathCallback _
  }
  def onDeathCallback(player: Player): Unit = {
    stock(player.num) -= 1
    if (player.mostRecentAttacker != -1) {
      // update score counter for attacker and currentScore if attacker
      // is the one with the highest score
      score(player.mostRecentAttacker) += 1
      currentScore = max(currentScore, score(player.mostRecentAttacker))
    }
    if (!player.active && canRespawn(player)) {
      respawnTimer += new TickTimer(respawnDelay, () => respawn(player))
    }
  }

  this += new TickTimer(120, cleanup _, RepeatForever)

  def cleanup() = {
    bulletList = bulletList.filter(_.active)
    platformList = platformList.filter(_.active)
  }

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    super.tick(delta)
    respawnTimer.tick(delta)

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
          player.takeDamage(bullet)
          bullet.inactivate
        }
      }
    }

    for (player <- playerList) {
      val alivePlayers = playerList.filter(_.active).length
      if (! isGameOver && ((alivePlayers <= 1 && ! respawnPending) || currentScore >= maxScore)) {
        gameOver()
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

  def genPlatform(column: Int, row: Int) = {
    val typeNum = rand(5)
    val platformType = typeNum match {
      case 0 => TetrisI
      case 1 => TetrisJ
      case 2 => TetrisL
      case 3 => TetrisT
      case 4 => TetrisEmpty
    }
    Platform(column * areaDimension,
     (row + colOffsets(column)) * areaDimension,
      platformType, rand(4) * 90)
  }

  def collision(go: GameObject, dx: Float, dy: Float) = {
    var minx = dx
    var miny = dy
    for (platform <- platformList) {
      val (vx, vy): (Float, Float) = platform.collision(go, (dx, dy))
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

  var winner = Array[Player]()
  override def gameOver() = {
    super.gameOver()
    for (p <- playerList; if (p.active)) {
      // gain a point for surviving until the end
      score(p.num) += 1
    }
    currentScore = score.max
    winner = playerList.filter(p => score(p.num) >= currentScore)
  }
}
