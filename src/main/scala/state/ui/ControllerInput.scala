package com.github.fellowship_of_the_bus
package mgm
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color, Input, ControllerListener, KeyListener, MouseListener}
import org.newdawn.slick.state.{StateBasedGame}
import org.newdawn.slick.util.InputAdapter

import scala.math._

// TODO: refactor this file into the fotb library

/** Set of constants and helper functions to make setting up controller input easier.
 * Provides constants for controller buttons, which is omitted from Slick's Input class for
 * some reason.
 */
object ControllerInput {
  import lib.game.GameConfig.{OS,MacOS,Windows}
  /** Xbox A button or PlayStation Cross button */
  lazy val BUTTON_A = OS match {
    case MacOS => 15
    case _ => 1
  }
  /** Xbox B button or PlayStation Circle button */
  lazy val BUTTON_B = OS match {
    case MacOS => 14
    case _ => 2
  }
  /** Xbox X button or PlayStation Square button */
  lazy val BUTTON_X = OS match {
    case MacOS => 16
    case _ => 3
  }
  /** Xbox Y button or PlayStation Triangle button */
  lazy val BUTTON_Y = OS match {
    case MacOS => 13
    case _ => 4
  }
  /** Xbox Left Bumper button or PlayStation L1 button */
  lazy val BUTTON_LB = OS match {
    case MacOS => 11
    case _ => 5
  }
  /** Xbox Right Bumper button or PlayStation R1 button */
  lazy val BUTTON_RB = OS match {
    case MacOS => 12
    case _ => 6
  }
  /** Xbox Back button or PlayStation Select button */
  lazy val BUTTON_BACK = OS match {
    case MacOS => 1
    case _ => 7
  }
  /** Xbox Start button or PlayStation Start button */
  lazy val BUTTON_START = OS match {
    case MacOS => 4
    case _ => 8
  }
  /** Xbox Left Stick button or PlayStation L3 button */
  lazy val BUTTON_LS = OS match {
    case MacOS => 2
    case _ => 9
  }
  /** Xbox Right Stick button or PlayStation R3 button */
  lazy val BUTTON_RS = OS match {
    case MacOS => 3
    case _ => 10
  }
  /** Xbox Left Trigger button or PlayStation L2 button */
  lazy val BUTTON_LT = OS match {
    case MacOS => 9
    case _ => 0
  }
  /** Xbox Right Trigger button or PlayStation R2 button */
  lazy val BUTTON_RT = OS match {
    case MacOS => 10
    case _ => 0
  }

  /** Left stick axis X */
  lazy val AXIS_X = OS match {
    case MacOS => 0
    case _ => 1
  }
  /** Left stick axis Y */
  lazy val AXIS_Y = OS match {
    case MacOS => 1
    case _ => 0
  }

  /** Right stick axis X */
  lazy val RIGHT_AXIS_X = OS match {
    case MacOS => 2
    case _ => 3
  }

  /** Right stick axis Y */
  lazy val RIGHT_AXIS_Y = OS match {
    case MacOS => 3
    case _ => 2
  }

  /** D-pad up button */
  lazy val BUTTON_UP = OS match {
    case MacOS => 5
    case _ => -1
  }

  /** D-pad left button */
  lazy val BUTTON_LEFT = OS match {
    case MacOS => 8
    case _ => -1
  }

  /** D-pad down button */
  lazy val BUTTON_DOWN = OS match {
    case MacOS => 7
    case _ => -1
  }

  /** D-pad right button */
  lazy val BUTTON_RIGHT = OS match {
    case MacOS => 6
    case _ => -1
  }

  // steal some unused values
  /** Dpad and Left axis controls. This is a dummy id for use in GamepadMapping. */
  val DPAD_LEFT_AXIS = 9990
  /** Arrow and WASD axis controls. This is a dummy id for use in KeyboardMapping. */
  val ARROW_WASD = 9991
  /** WASD axis controls. This is a dummy id for use in KeyboardMapping. */
  val WASD = 9992
  /** Arrow key axis controls. This is a dummy id for use in KeyboardMapping. */
  val ARROW_KEYS = 9993
  /** Dpad axis controls. This is a dummy id for use in GamepadMapping. */
  val DPAD = 9994
  /** Mouse left button. This is a dummy id for use in KeyboardMapping. */
  val MOUSE_LEFT = 9995
  /** Mouse right button. This is a dummy id for use in KeyboardMapping. */
  val MOUSE_RIGHT = 9996
  /** Mouse relative axis controls. This is a dummy id for use in KeyboardMapping. */
  val MOUSE_RELATIVE_AXIS = 9997
  /** Left axis controls. This is a dummy id for use in GamepadMapping. */
  val LEFT_AXIS = 9998
  /** Right axis controls. This is a dummy id for use in GamepadMapping. */
  val RIGHT_AXIS = 9999

  /** For mapping keys, buttons, etc. to action callbacks, providing for code reuse
   * and control remapping possibilities.
   * Axes provide x and y values, while actions fire on button presses.
   * It is possible to map buttons to axes and vice-versa.
   */
  sealed trait ActionType
  case class Axis1(x: Float, y: Float) extends ActionType
  case class Axis2(x: Float, y: Float) extends ActionType
  case class Axis3(x: Float, y: Float) extends ActionType
  object Action1 extends ActionType
  object Action2 extends ActionType
  object Action3 extends ActionType
  object Action4 extends ActionType
  object Action5 extends ActionType
  object Action6 extends ActionType
  object Action7 extends ActionType
  object Action8 extends ActionType
  object Action9 extends ActionType
  object Action10 extends ActionType
  object Action11 extends ActionType
  object Action12 extends ActionType
  object Pause extends ActionType
  object Back extends ActionType
  object NoAction extends ActionType

  abstract class ControlMapping(buttonMapping: Map[Int, ActionType], axisMapping: Map[Int, (SlickController) => ActionType]) {
    def apply(button: Int): ActionType = buttonMapping(button)
    def apply(ctrl: SlickController, axis: Int): ActionType = axisMapping(axis)(ctrl)
    def foreach(f: ((SlickController) => ActionType) => Unit): Unit = {
      for ((axis, callback)<- axisMapping) {
        f(callback)
      }
    }
  }
  /** Maps gamepad controls into actions */
  class GamepadMapping(buttonMapping: Map[Int, ActionType], axisMapping: Map[Int, (SlickController) => ActionType])
  extends ControlMapping(buttonMapping, axisMapping) {
    def +(binding: (Int, ActionType)): GamepadMapping = new GamepadMapping(buttonMapping + binding, axisMapping)
  }
  /** Maps mouse and keyboard controls into actions */
  class KeyboardMapping(buttonMapping: Map[Int, ActionType], axisMapping: Map[Int, (SlickController) => ActionType])
  extends ControlMapping(buttonMapping, axisMapping) {
    def +(binding: (Int, ActionType)): KeyboardMapping = new KeyboardMapping(buttonMapping + binding, axisMapping)
  }

  def getAxis(axis: Int, ctrl: SlickController, makeAxis: (Float, Float) => ActionType): ActionType = {
    val (xval: Float, yval: Float) = ctrl.getAxisValue(axis)
    makeAxis(xval, yval)
  }

  /** Turns left stick into an axis, given an axis-creating factory function */
  def getLeftAxis(ctrl: SlickController, makeAxis: (Float, Float) => ActionType): ActionType = {
    getAxis(LEFT_AXIS, ctrl, makeAxis)
  }

  /** Turns right stick into an axis, given an axis-creating factory function */
  def getRightAxis(ctrl: SlickController, makeAxis: (Float, Float) => ActionType): ActionType = {
    getAxis(RIGHT_AXIS, ctrl, makeAxis)
  }

  /** Turns mouse coordinates into an axis, given an axis-creating factory function */
  def getMouseRelativeAxis(ctrl: SlickController, makeAxis: (Float, Float) => ActionType): ActionType = {
    getAxis(MOUSE_RELATIVE_AXIS, ctrl, makeAxis)
  }

  type DigitalAxis = List[List[Int]]
  def getDigitalAxis(values: DigitalAxis, isPressed: (Int) => Boolean): (Float, Float) = {
    def getAxis(pos: List[Int], neg: List[Int]): Float = {
      def pred(l: List[Int]): Boolean = l.exists(isPressed)
      if (pred(pos)) 1f
      else if (pred(neg)) -1f
      else 0f
    }
    (getAxis(values(0), values(1)), getAxis(values(2), values(3)))
  }

  /** Turns a set of buttons in axis ordering into a simple (-1, 0, 1) axis */
  def getButtonAxis(ctrl: SlickController, buttons: DigitalAxis): (Float, Float) = {
    getDigitalAxis(buttons, button => ctrl.isPressed(button))
  }

  /** Turns a set of keys in axis ordering into a simple (-1, 0, 1) axis */
  def getKeyAxis(ctrl: SlickController, keys: DigitalAxis): (Float, Float) = {
    getDigitalAxis(keys, key => ctrl.isPressed(key))
  }

  /** wasd keys in axis ordering */
  val WASDKeys = List(
    List(Input.KEY_D),
    List(Input.KEY_A),
    List(Input.KEY_S),
    List(Input.KEY_W),
  )

  /** Arrow keys in axis ordering */
  val ArrowKeys = List(
    List(Input.KEY_RIGHT),
    List(Input.KEY_LEFT),
    List(Input.KEY_DOWN),
    List(Input.KEY_UP),
  )

  /** Arrow and wasd keys in axis ordering */
  val ArrowWASDKeys = for (
    (l1, l2) <- WASDKeys.zip(ArrowKeys)
  ) yield l1 ++ l2

  /** D-pad buttons in axis ordering */
  val DPADButtons = List(
    List(BUTTON_RIGHT),
    List(BUTTON_LEFT),
    List(BUTTON_DOWN),
    List(BUTTON_UP),
  )

  // some common axis types
  /** use dpad to control Axis 1 */
  def dpadLeftAxis(ctrl: SlickController): ActionType = {
    val (xval, yval) = getButtonAxis(ctrl, DPADButtons)
    new Axis1(xval, yval)
  }

  /** use arrow or wasd keys to control Axis 1 */
  def arrowWasdLeftAxis(ctrl: SlickController): ActionType = {
    val (xval, yval) = getKeyAxis(ctrl, ArrowWASDKeys)
    new Axis1(xval, yval)
  }

  /** use relative mouse coordinates to control Axis 3 */
  def mouseRelativeAxis(ctrl: SlickController): ActionType = {
    getMouseRelativeAxis(ctrl, (x, y) => new Axis3(x, y))
  }

  /** default: no Axis */
  def noAxis(ctrl: SlickController): ActionType = NoAction

  /** use left stick to control Axis 1*/
  def leftStickAxis(ctrl: SlickController): ActionType = {
    getLeftAxis(ctrl, (x, y) => new Axis1(x, y))
  }

  /** use right stick to control Axis 2 */
  def rightStickAxis(ctrl: SlickController): ActionType = {
    getRightAxis(ctrl, (x, y) => new Axis2(x, y))
  }

  /** use dpad or left stick to control Axis 1 */
  def dpadLeftStickAxis(ctrl: SlickController): ActionType = {
    val (xval, yval) = getButtonAxis(ctrl, DPADButtons)
    if (xval == 0 && yval == 0) leftStickAxis(ctrl)
    else new Axis1(xval, yval)
  }

  /** Implicit values for default keyboard and gamepad layouts */
  object Implicits {
    implicit val defaultGamepadMapping = new GamepadMapping(Map(
      BUTTON_A -> Action1,
      BUTTON_B -> Action2,
      BUTTON_X -> Action3,
      BUTTON_Y -> Action4,
      BUTTON_LB -> Action5,
      BUTTON_RB -> Action6,
      BUTTON_LT -> Action7,
      BUTTON_RT -> Action8,
      BUTTON_START -> Pause,
      BUTTON_BACK -> Back,
    ).withDefaultValue(NoAction), Map(
      DPAD_LEFT_AXIS -> dpadLeftStickAxis _,
      RIGHT_AXIS -> rightStickAxis _,
    ).withDefaultValue(noAxis _))

    implicit val defaultKeyboardMapping = new KeyboardMapping(Map(
      Input.KEY_SPACE -> Action1,
      MOUSE_LEFT -> Action2,
      Input.KEY_ENTER -> Pause,
      Input.KEY_ESCAPE -> Back,
    ).withDefaultValue(NoAction), Map(
      ARROW_WASD -> arrowWasdLeftAxis _,
      MOUSE_RELATIVE_AXIS -> mouseRelativeAxis _
    ).withDefaultValue(noAxis _))
  }
}

/** Holds and manages all of the controllers for a game. Distributes calls to members functions as appropriate. */
class ControllerManager(gc: GameContainer, sbg: StateBasedGame)(implicit state: Int, padMap: ControllerInput.GamepadMapping, keyMap: ControllerInput.KeyboardMapping) {
  private var controls: Vector[SlickController] = Vector()
  private var input: Input = null

  setInput(gc.getInput)

  def setInput(in: Input) = {
    if (input != null) {
      for (controller <- controls) {
        controller.setInput(null)
      }
    }
    input = in

    val controllerCount = input.getControllerCount()
    controls = Vector()
    for (i <- 0 until controllerCount) {
      if (input.getAxisCount(i) >= 2) { // valid playstation or xbox controller
        controls = controls :+ new SlickGamepadController(i, input, sbg)
      }
    }
    controls = controls :+ new SlickKeyboardController(input, sbg)
  }
  }

  def update(delta: Int): Unit = {
    for (controller <- controls) {
      controller.update(delta)
    }
  }

  def controllers() = controls

  def removeListeners() = for (ctrl <- controls) {
    ctrl.setInput(null)
  }
}

/** Wrapper for a controller with an interface amenable to Slick. */
trait SlickController {
  protected def state: Int
  protected def sbg: StateBasedGame
  protected var input: Input
  protected var controller: Controller = null
  protected def addListener(input: Input): Unit
  protected def removeListener(input: Input): Unit

  def setInput(in: Input): Unit = {
    if (input != null) removeListener(input)
    input = in
    if (input != null) addListener(input)
  }

  def inputStarted(): Unit = {}
  def inputEnded(): Unit = {}

  /** Accepts input only on the current state */
  def isAcceptingInput(): Boolean = state == sbg.getCurrentStateID && controller != null

  protected def mapping: ControllerInput.ControlMapping
  def update(delta: Int): Unit = if (controller != null) {
    for (axis <- mapping) {
      controller.pressed(axis(this))
    }
    controller.update(delta)
  }
  def registerControlScheme(control: Controller): Unit = controller = control

  // unfortunately, Slick's Input class appears to be buggy, or else just outright chooses to be unhelpful.
  // Checking if a button is pressed in the controllerButtonPressed callback always yields false, so instead
  // of relying on Slick here we have to build our own table to track which buttons are currently pressed
  protected var pressed = new Array[Boolean](32)
  protected def setPressed(idx: Int, value: Boolean): Unit = {
    if (idx >= pressed.length) { // array is too small, realloc
      val newArray = new Array[Boolean](idx+1)
      for (idx <- 0 until pressed.length) {
        newArray(idx) = pressed(idx)
      }
      pressed = newArray
    }
    pressed(idx) = value
  }
  /** true if the button or key is currently pressed */
  def isPressed(idx: Int): Boolean = {
    if (idx < pressed.length) pressed(idx)
    else false
  }

  /** */
  def getAxisValue(axis: Int): (Float, Float)
}

// wrapper for a single controller that interacts with slick2d input
class SlickGamepadController(cnum: Int, protected var input: Input, val sbg: StateBasedGame)(implicit val state: Int, protected val mapping: ControllerInput.GamepadMapping) extends SlickController with ControllerListener {
  import ControllerInput._
  input.addControllerListener(this)

  def controllerButtonPressed(cn: Int, button: Int): Unit = if (cn == cnum) {
    setPressed(button, true)
    controller.pressed(mapping(button))
  }

  def controllerButtonReleased(cn: Int, button: Int): Unit = if (cn == cnum) {
    setPressed(button, false)
    controller.released(mapping(button))
  }

  // these receive input events for the left stick, which isn't necessarily what you want.
  // As such, ignore these callbacks from slick and build something more general
  def controllerDownPressed(cn: Int): Unit = ()
  def controllerDownReleased(cn: Int): Unit = ()
  def controllerLeftPressed(cn: Int): Unit = ()
  def controllerLeftReleased(cn: Int): Unit = ()
  def controllerRightPressed(cn: Int): Unit = ()
  def controllerRightReleased(cn: Int): Unit = ()
  def controllerUpPressed(cn: Int): Unit = ()
  def controllerUpReleased(cn: Int): Unit = ()

  protected def addListener(input: Input) = input.addControllerListener(this)
  protected def removeListener(input: Input) = input.removeControllerListener(this)

  /** returns the axis value for the left or right stick */
  def getAxisValue(axis: Int): (Float, Float) = {
    if (axis == LEFT_AXIS) (input.getAxisValue(cnum, AXIS_X), input.getAxisValue(cnum, AXIS_Y))
    else (input.getAxisValue(cnum, RIGHT_AXIS_X), input.getAxisValue(cnum, RIGHT_AXIS_Y))
  }
}

class SlickKeyboardController(protected var input: Input, val sbg: StateBasedGame)(implicit val state: Int, protected val mapping: ControllerInput.KeyboardMapping) extends SlickController with KeyListener with MouseListener {
  import ControllerInput._
  input.addKeyListener(this)
  input.addMouseListener(this)

  def keyPressed(key: Int, c: Char): Unit = {
    setPressed(key, true)
    controller.pressed(mapping(key))
  }
  def keyReleased(key: Int, c: Char): Unit = {
    setPressed(key, false)
    controller.released(mapping(key))
  }
  def mousePressed(button: Int, x: Int, y: Int) = {
    if (button == Input.MOUSE_LEFT_BUTTON) controller.pressed(mapping(MOUSE_LEFT))
    if (button == Input.MOUSE_RIGHT_BUTTON) controller.pressed(mapping(MOUSE_RIGHT))
    if (button == Input.MOUSE_MIDDLE_BUTTON) controller.pressed(mapping(MOUSE_RIGHT))
  }
  def mouseReleased(button: Int, x: Int, y: Int) = {
    if (button == Input.MOUSE_LEFT_BUTTON) controller.released(mapping(MOUSE_LEFT))
    if (button == Input.MOUSE_RIGHT_BUTTON) controller.released(mapping(MOUSE_RIGHT))
    if (button == Input.MOUSE_MIDDLE_BUTTON) controller.released(mapping(MOUSE_RIGHT))
  }

  // not useful for my purposes, ignore them
  def mouseClicked(button: Int, x: Int, y: Int, clickCount: Int) = {}
  def mouseWheelMoved(change: Int) = {}
  // mouse moved/dragged events could be useful, but ultimately it's better if all
  // axes behave the same way, and slick does not have events for the right stick, so
  // at the very least that has to be done on a polling basis. So for consistency, mouse
  // position is also done by polling
  def mouseMoved(oldx: Int, oldy: Int, newx: Int, newy: Int) = {}
  def mouseDragged(oldx: Int, oldy: Int, newx: Int, newy: Int) = {}

  protected def addListener(input: Input): Unit = {
    input.addKeyListener(this)
    input.addMouseListener(this)
  }
  protected def removeListener(input: Input): Unit = {
    input.removeKeyListener(this)
    input.removeMouseListener(this)
  }

  def getAxisValue(axis: Int): (Float, Float) = (input.getMouseX.toFloat, input.getMouseY.toFloat)
}

/** a single controller - provides a much nicer interface to work with */
trait Controller {
  import ControllerInput._
  /** Dispatch action type to the correct pressed function */
  def pressed(act: ActionType): Unit = act match {
    case Action1 => button1Pressed()
    case Action2 => button2Pressed()
    case Action3 => button3Pressed()
    case Action4 => button4Pressed()
    case Action5 => button5Pressed()
    case Action6 => button6Pressed()
    case Action7 => button7Pressed()
    case Action8 => button8Pressed()
    case Action9 => button9Pressed()
    case Action10 => button10Pressed()
    case Action11 => button11Pressed()
    case Action12 => button12Pressed()
    case Axis1(x, y) => axis1(x, y)
    case Axis2(x, y) => axis2(x, y)
    case Axis3(x, y) => axis3(x, y)
    case Pause => pausePressed()
    case Back => backPressed()
    case _ => ()
  }
  /** Dispatch action type to the correct released function */
  def released(act: ActionType): Unit = act match {
    case Action1 => button1Released()
    case Action2 => button2Released()
    case Action3 => button3Released()
    case Action4 => button4Released()
    case Action5 => button5Released()
    case Action6 => button6Released()
    case Action7 => button7Released()
    case Action8 => button8Released()
    case Action9 => button9Released()
    case Action10 => button10Released()
    case Action11 => button11Released()
    case Action12 => button12Released()
    case Axis1(x, y) => axis1(x, y)
    case Axis2(x, y) => axis2(x, y)
    case Axis3(x, y) => axis3(x, y)
    case _ => ()
  }

  /** called once per frame */
  def update(delta: Int): Unit = ()

  def axis1(x: Float, y: Float): Unit = ()
  def axis2(x: Float, y: Float): Unit = ()
  def axis3(x: Float, y: Float): Unit = ()

  def button1Pressed(): Unit = {}
  def button2Pressed(): Unit = {}
  def button3Pressed(): Unit = {}
  def button4Pressed(): Unit = {}
  def button5Pressed(): Unit = {}
  def button6Pressed(): Unit = {}
  def button7Pressed(): Unit = {}
  def button8Pressed(): Unit = {}
  def button9Pressed(): Unit = {}
  def button10Pressed(): Unit = {}
  def button11Pressed(): Unit = {}
  def button12Pressed(): Unit = {}

  def button1Released(): Unit = {}
  def button2Released(): Unit = {}
  def button3Released(): Unit = {}
  def button4Released(): Unit = {}
  def button5Released(): Unit = {}
  def button6Released(): Unit = {}
  def button7Released(): Unit = {}
  def button8Released(): Unit = {}
  def button9Released(): Unit = {}
  def button10Released(): Unit = {}
  def button11Released(): Unit = {}
  def button12Released(): Unit = {}

  // for pause and back, typically only the press matters
  def pausePressed(): Unit = {}
  def backPressed(): Unit = {}
}
