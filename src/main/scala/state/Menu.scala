package com.github.fellowship_of_the_bus
package mgm
package state

import lib.slick2d.ui.{Button, ToggleButton, UIElement, InteractableUIElement}
import lib.game.GameConfig
import lib.game.GameConfig.{Width,Height}

import state.ui.{ControllerInput, Controller, ControllerManager}

import org.newdawn.slick.{GameContainer, Graphics, Color, Input, KeyListener}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}


object MenuState {
  val background = images(Background)
  val fotb = images(FotBLogo)
  // val logo = images(Logo)
  val arrow = images(SelectArrow)

  background.scaleFactor = Width/background.width
  fotb.scaleFactor = 1
  arrow.scaleFactor = Button.height/arrow.height
  // logo.scaleFactor = 1
  implicit val keyMap: ControllerInput.KeyboardMapping =
    ControllerInput.Implicits.defaultKeyboardMapping + ((Input.KEY_ENTER -> ControllerInput.Action1))
}

trait MenuState extends BasicGameState {
  import MenuState._

  val startY = 200 // Height/2
  val centerx = Width/2-Button.width/2
  val padding = 30

  implicit var input: Input = null
  implicit var SBGame: StateBasedGame = null
  implicit val id = getID

  private var currentOption = 0

  def choices: List[InteractableUIElement]
  def ui: List[UIElement] = choices

  def confirm(): Unit = choices(currentOption).doAction()
  def next(): Unit = currentOption = (currentOption+1)%choices.length
  def previous(): Unit = currentOption = (currentOption+choices.length-1)%choices.length

  private var manager: ControllerManager = null
  def init(gc: GameContainer, game: StateBasedGame): Unit = {
    input = gc.getInput
    SBGame = game
    gc.getGraphics.setBackground(Color.cyan)

    import ControllerInput.Implicits.defaultGamepadMapping
    manager = new ControllerManager(gc, game)
    for (ctrl <- manager.controllers) {
      ctrl.registerControlScheme(new MenuController(this))
    }
  }

  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    background.draw(0,0)
    fotb.draw(Width/2-fotb.getWidth/2, 3*Height/4)
    // logo.draw(Width/2-logo.getWidth/2, 200)

    for (item <- choices) {
      item.render(gc, game, g)
    }

    // draw selection arrow next to highlighted choice
    arrow.draw(choices(currentOption).x-arrow.width, choices(currentOption).y)
  }

  def update(gc: GameContainer, game: StateBasedGame, delta: Int): Unit = {
    manager.update(delta)
  }
}

object Menu extends MenuState {
  lazy val choices = List(
    Button("New Game (A/X)", centerx, startY, () => SBGame.enterState(Mode.BattleID)),
    Button("Options", centerx, startY+padding, () => SBGame.enterState(Mode.OptionsID)),
    Button("Quit (B/O)", centerx, startY+2*padding, () => System.exit(0)))

  def getID() = Mode.MenuID
}

// pre-battle settings screen to toggle options
object Settings extends MenuState {
  var doRespawn = true
  var maxScore  = 99
  var stock = Double.PositiveInfinity

  lazy val choices = List(
    Button("Respawn Mode: ", centerx, startY, () => stock = 0),
    Button("Player 1", centerx, startY+padding, () => ()),
    Button("Start Game", centerx, startY+5*padding, () => SBGame.enterState(mgm.Mode.BattleID)),
    Button("Back", centerx, startY+6*padding, () => SBGame.enterState(mgm.Mode.MenuID)))

  def getID() = Mode.SettingsID
}

object Options extends MenuState {
  var keyboardPlayer = true
  var aiPlayer = true

  lazy val choices = List(
    ToggleButton("Toggle Keyboard Player", centerx, startY, () => keyboardPlayer = !keyboardPlayer, () => keyboardPlayer),
    ToggleButton("Toggle AI Player(s)", centerx, startY+padding, () => aiPlayer = !aiPlayer, () => aiPlayer),
    Button("Back", centerx, startY+2*padding, () => SBGame.enterState(Mode.MenuID)))

  def getID() = Mode.OptionsID
}

class MenuController(state: MenuState) extends Controller {
  private var timer = 0
  private val maxTimer = 15

  private var dir = 0

  override def update(delta: Int): Unit = {
    if (timer > 0) {
      timer -= 1
    }
    if (timer == 0) {
      if (dir > 0) {
        state.next()
        timer = maxTimer
      } else if (dir < 0) {
        state.previous()
        timer = maxTimer
      }
    }
  }

  override def axis1(x: Float, y: Float): Unit = {
    val threshold = 0.25f
    if (y > threshold) dir = 1
    else if (y < -threshold) dir = -1
    else dir = 0
  }

  override def button1Pressed(): Unit = state.confirm()
}
