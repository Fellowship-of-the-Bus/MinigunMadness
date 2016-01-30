package com.github.fellowship_of_the_bus
package mgm
import java.util.logging.{Level, Logger}
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input, Image}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import state._
// import game._
import lib.util.Native
import lib.game.GameConfig
import state.ui.ControllerInput

class MinigunMadness(gamename: String) extends StateBasedGame(gamename) {
  def initStatesList(gc: GameContainer) = {
    gc.setShowFPS(true)
    addState(Menu)
    // addState(Battle)
    // addState(Options)
    val controllerInput = new ControllerInput(gc,this)
  }
}

object MinigunMadness extends App {
  def makeImg(loc: String) = new Image(loc)

  GameConfig.Width = 1280
  GameConfig.Height = 1024
  GameConfig.FrameRate = 60

  try {
    import GameConfig._
    Native.loadLibraryFromJar()
    val appgc = new AppGameContainer(new MinigunMadness("Minigun Madness"))
    appgc.setDisplayMode(Width, Height, false)
    appgc.setTargetFrameRate(FrameRate)
    appgc.setVSync(true)
    appgc.start()
  } catch {
    case ex: SlickException => Logger.getLogger(MinigunMadness.getClass.getName()).log(Level.SEVERE, null, ex)
    case t: Throwable =>
      println("Library path is: " + System.getProperty("java.library.path"))
      t.printStackTrace
  }


}
