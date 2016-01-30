package com.github.fellowship_of_the_bus
package mgm
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import lib.game.GameConfig.{Height,Width}
import lib.util.rand
import lib.util.{TickTimer,TimerListener,RepeatForever}

class Game extends lib.game.Game with TimerListener {
  val maxPlayers = 4
  val playerList = new Array[Player](maxPlayers)
  for (i <- 0 until maxPlayers) {
    playerList(i) = new Player(i*100, i*100, players(HumanPlayer), i)
  }

  def setPlayers(nplayers: Int) = {
    for (i <- nplayers until maxPlayers) {
      playerList(i).inactivate
    }
  }

  addTimer(new TickTimer(240, cleanup _, RepeatForever))

  //var projectiles = List[Projectile]()

  def cleanup() = {
    //projectiles = projectiles.filter(_.active)
  }


  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    super.update(delta)
    /*for (p <- projectiles; if (p.active)) {
      p.update(delta, this)
    }
    for (p <- players; if (p.active)) {
      p.update(delta, this)
    }*/
  }
}
