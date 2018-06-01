package com.github.fellowship_of_the_bus
package mgm
import java.util.logging.{Level, Logger}
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input, Image}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import state._
// import game._
import lib.util.Native
import lib.game.GameConfig

class MinigunMadness(gamename: String) extends StateBasedGame(gamename) {
  def initStatesList(gc: GameContainer) = {
    gc.setShowFPS(true)
    addState(Menu)
    addState(Battle)
    addState(Settings)
    addState(Options)
  }
}

object MinigunMadness extends App {
  def makeImg(loc: String) = new Image(loc)
  GameConfig.FrameRate = 60

  def calculateScreenSize(width: Int, height: Int): (Int, Int) = {
    // calculate maximum screen size using desired aspect ratio and known width and height.
    val aspect = 5.0f/4.0f

    val newWidth = (height * aspect).toInt
    if (width < newWidth) {
      (width, (width/aspect).toInt)
    } else {
      (newWidth, height)
    }
  }

  try {
    import GameConfig._
    Native.loadLibraryFromJar()
    val appgc = new AppGameContainer(new MinigunMadness("Minigun Madness"))
    val HeightPercent = 0.9
    val (w, h) = calculateScreenSize(appgc.getScreenWidth, (appgc.getScreenHeight * HeightPercent).toInt)
    Width = w
    Height = h
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
