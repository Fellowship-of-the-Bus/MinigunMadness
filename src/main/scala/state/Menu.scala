package com.github.fellowship_of_the_bus
package mgm
package state

import lib.slick2d.ui.{Button, ToggleButton}
import lib.game.GameConfig
import lib.game.GameConfig.{Width,Height}

import state.ui.ControllerInput


import org.newdawn.slick.{GameContainer, Graphics, Color, Input, KeyListener}
import org.newdawn.slick.state.{BasicGameState => SlickBasicGameState, StateBasedGame}

trait BasicGameState extends SlickBasicGameState {
  val startY = 200 // Height/2
  val centerx = Width/2-Button.width/2
  val padding = 30

  implicit var input: Input = null
  implicit var SBGame: StateBasedGame = null
  implicit val id = getID

  def init(gc: GameContainer, game: StateBasedGame): Unit = {
    input = gc.getInput
    SBGame = game
    gc.getGraphics.setBackground(Color.cyan)
  }

  val background = images(Background)
  val fotb = images(FotBLogo)
  // val logo = images(Logo)

  {
    background.scaleFactor = Width/background.width
    fotb.scaleFactor = 1
    // logo.scaleFactor = 1
  }
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    background.draw(0,0)
    fotb.draw(Width/2-fotb.getWidth/2, 3*Height/4)
    // logo.draw(Width/2-logo.getWidth/2, 200)
  }

  def update(gc: GameContainer, game: StateBasedGame, delta: Int): Unit = {}
}


import com.github.fellowship_of_the_bus.lib.slick2d.ui.{Button, UIElement}
import org.newdawn.slick.{Color, Input}
import org.newdawn.slick.state.{StateBasedGame}

object ModeButton {
  val width = Button.width
  val height = Button.height
  val cornerRadius = Button.cornerRadius

  def apply(title: String, modes: List[Mode], x: Float, y: Float)
           (implicit input: Input, state: Int, game: StateBasedGame): ModeButton = {
    val act = () => game.getCurrentStateID == state
    val b = new ModeButton(title, modes, x, y, modes.length*width, height, act)
    b.setInput(input)
    b
  }

  case class Mode(val text: String, val action: () => Unit)
}

class ModeButton(title: String, modes: List[ModeButton.Mode], val x: Float, val y: Float, val width: Float, val height: Float,
  canClick: () => Boolean) extends UIElement {

  val buttonSpacing = width/modes.length
  val buttons: List[Button] = for ((m, i) <- modes.zipWithIndex )
    yield new Button(m.text, x+i*buttonSpacing, y, buttonSpacing, height, () => if (canClick()) m.action())

  override def setInput(input: Input) = {
    for (b <- buttons) {
      b.setInput(input)
    }
  }

  def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    val font = gc.getDefaultFont
    g.drawString(title, x-font.getWidth(title), y)
    for (b <- buttons) {
      b.draw(gc, sbg, g)
    }
  }

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int): Unit = {
    for (b <- buttons) {
      b.update(gc, sbg, delta)
    }
  }
}

object Menu extends BasicGameState {
  lazy val choices = List(
    Button("New Game (A/X)", centerx, startY, () => SBGame.enterState(Mode.BattleID)),
    Button("Options", centerx, startY+padding, () => SBGame.enterState(Mode.OptionsID))),
    Button("Quit (B/O)", centerx, startY+2*padding, () => System.exit(0)))

  override def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    super.render(gc, game, g)
    for (item <- choices) {
      item.render(g)
    }
  }

  def getID() = Mode.MenuID
}

// pre-battle settings screen to toggle options
object Settings extends BasicGameState {
  val respawnDelay = 60*5 // delay of 5 seconds

  var respawn = 0

  lazy val choices = {
    import ModeButton.Mode
    List(
      ModeButton("Respawn Mode: ",
        List(
          Mode("Immediate", () => respawn = 0),
          Mode("Delay", () => respawn = respawnDelay),
          Mode("Round", () => respawn = -1)), centerx, startY),
      ModeButton("Player 1",
        List(
          Mode("Keyboard", () => ()),
        ), centerx, startY+padding),
      Button("Start Game", centerx, startY+5*padding, () => SBGame.enterState(mgm.Mode.BattleID)),
      Button("Back", centerx, startY+6*padding, () => SBGame.enterState(mgm.Mode.MenuID))
    )
  }

  override def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    super.render(gc, game, g)
    for (item <- choices) {
      item.render(gc, game, g)
    }
  }

  def getID() = Mode.SettingsID
}

object Options extends BasicGameState {
  var keyboardPlayer = true

  lazy val choices = List(
    ToggleButton("Toggle Keyboard Player", centerx, startY, () => keyboardPlayer = !keyboardPlayer, () => keyboardPlayer),
    Button("Back", centerx, startY+padding, () => SBGame.enterState(Mode.MenuID)))

  override def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    super.render(gc, game, g)
    for ( item <- choices ) {
      item.render(g)
    }
  }

  def getID() = Mode.OptionsID
}
