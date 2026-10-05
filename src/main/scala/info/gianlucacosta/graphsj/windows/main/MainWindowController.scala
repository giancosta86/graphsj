package info.gianlucacosta.graphsj.windows.main

import java.io._
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.concurrent.Callable
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

import javax.imageio.ImageIO
import info.gianlucacosta.eighthbridge.fx.canvas.{GraphCanvas, VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.graphsj._
import info.gianlucacosta.helios.apps.AppInfo
import info.gianlucacosta.helios.desktop.DesktopUtils
import info.gianlucacosta.helios.fx.dialogs.about.AboutBox
import info.gianlucacosta.helios.fx.Includes._
import info.gianlucacosta.helios.fx.dialogs.{Alerts, InputDialogs}
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.SnapshotParameters
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.stage.FileChooser


object MainWindowController {
  val AppStylesheet = getClass.getResource("MainWindow.css")
}


class MainWindowController[V <: VisualVertex, L <: VisualLink, G <: VisualGraph[V, L]] {
  private var aboutBox: AboutBox = _

  private var stage: Stage = _
  private var appInfo: AppInfo = _
  private var workspace: GraphWorkspace[V, L, G] = _

  def scenarioRepository: ScenarioRepository =
    workspace.scenarioRepository


  def scenarioRepository_=(newValue: ScenarioRepository): Unit =
    workspace.scenarioRepository = newValue


  def setup(stage: Stage, appInfo: AppInfo, scenarioRepository: ScenarioRepository): Unit = {
    this.stage =
      stage

    this.appInfo =
      appInfo


    Platform.runLater {
      aboutBox = new AboutBox(appInfo)
    }


    val scenarioFileChooser: FileChooser = {
      val fileChooser =
        new FileChooser

      fileChooser.extensionFilters.setAll(
        new FileChooser.ExtensionFilter("Scenario", s"*${App.DefaultExtension}")
      )

      fileChooser.title =
        appInfo.name

      fileChooser
    }

    workspace =
      new GraphWorkspace[V, L, G](appInfo, stage, scenarioRepository, scenarioFileChooser) {
        bindEvents()

        designCanvasProperty.addListener((observable: Observable) => {
          workspace.designCanvas.foreach(designCanvas => {
            designCanvas.graphProperty.addListener((graphObservable: Observable) => {
              workspace.setModified()
            })

            graphCanvasPane.content =
              designCanvas

            designCanvas.requestFocus()

            designCanvas.addEventHandler(MouseEvent.ANY, new EventHandler[MouseEvent] {
              override def handle(event: MouseEvent): Unit = {
                designCanvas.requestFocus()

                event.consume()
              }
            })
          })

          ()
        })
      }


    stage.title <== Bindings.createStringBinding(new Callable[String] {
      override def call(): String = {
        String.format("%s%s%s",
          appInfo.name,

          workspace.documentFileOption.get.map(
            file => " - " + file.getName
          ).getOrElse(
            ""
          ),

          if (workspace.modified.get) " *" else ""
        )
      }
    },

      workspace.documentFileOption,
      workspace.modified
    )

    setupMenusAndToolbar()
  }


  private val runState =
    ObjectProperty[RunState](NotRunning)

  private val running =
    BooleanProperty(false)

  running <==
    runState =!= NotRunning


  running.addListener((o: javafx.beans.value.ObservableValue[_ <: java.lang.Boolean], oldValue: java.lang.Boolean, newValue: java.lang.Boolean) => {
    if (newValue != oldValue) {
      if (running()) {
        consoleArea.clear()

        algorithm =
          workspace.scenario.get.createAlgorithm()

        stepIndex =
          0

        runtimeCanvasProperty() =
          Some(new GraphCanvas(
            workspace.scenario.get.createRuntimeController(),
            designGraph)
          )

        graphCanvasPane.content =
          runtimeCanvas.get
      } else {
        graphCanvasPane.content =
          workspace.designCanvas.get
      }
    }
  })


  private val runtimeCanvasProperty =
    ObjectProperty[Option[GraphCanvas[V, L, G]]](None)


  def runtimeCanvas: Option[GraphCanvas[V, L, G]] =
    runtimeCanvasProperty.get


  def runtimeCanvas(newValue: Option[GraphCanvas[V, L, G]]): Unit = {
    runtimeCanvasProperty.set(newValue)
  }


  runtimeCanvasProperty.addListener((observable: Observable) => {
    runtimeCanvas.foreach(runtimeCanvas => {
      runtimeCanvas.requestFocus()

      runtimeCanvas.addEventHandler(MouseEvent.ANY, new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = {
          runtimeCanvas.requestFocus()

          event.consume()
        }
      })
    })

    ()
  })


  private var algorithm: Algorithm[V, L, G] = _
  private var stepIndex: Int = _


  private def designGraph: G =
    workspace.designCanvas.get.graph

  private def designGraph_=(newGraph: G): Unit =
    workspace.designCanvas.get.graph = newGraph


  private def runtimeGraph: G =
    runtimeCanvasProperty().get.graph

  private def runtimeGraph_=(newGraph: G): Unit =
    runtimeCanvasProperty().get.graph = newGraph


  private lazy val outputConsole: OutputConsole = new OutputConsole {
    override def write(value: Any): Unit = {
      consoleArea.appendText(value.toString)
    }

    override def writeln(value: Any): Unit = {
      consoleArea.appendText(value.toString + "\n")
    }

    override def writeln(): Unit = {
      consoleArea.appendText("\n")
    }
  }


  private lazy val consoleFileChooser: FileChooser = {
    val fileChooser =
      new FileChooser

    fileChooser.extensionFilters.setAll(
      new FileChooser.ExtensionFilter("Text file", "*.txt"),
      new FileChooser.ExtensionFilter("Any file", "*.*")
    )

    fileChooser.title =
      "Save console output..."

    fileChooser
  }

  private lazy val exportAsImageFileChooser: FileChooser = {
    val fileChooser =
      new FileChooser

    fileChooser.extensionFilters.setAll(
      new FileChooser.ExtensionFilter("PNG image", "*.png")
    )

    fileChooser.title =
      "Export as image..."

    fileChooser
  }


  private def setupMenusAndToolbar() {
    val scenarioProperty =
      workspace.scenarioProperty


    newMenuItem.disable <==
      running

    bindButton(newButton, newMenuItem)


    openMenuItem.disable <==
      running

    bindButton(openButton, openMenuItem)


    saveMenuItem.disable <==
      (scenarioProperty === None) || running || (!workspace.modified)

    bindButton(saveButton, saveMenuItem)


    saveAsMenuItem.disable <==
      (scenarioProperty === None) || running

    bindButton(saveAsButton, saveAsMenuItem)


    exportAsImageMenuItem.disable <==
      (scenarioProperty === None)


    fullRunMenuItem.disable <==
      (scenarioProperty === None) || (runState === InFullRun) || (runState === Finished)

    bindButton(fullRunButton, fullRunMenuItem)


    runStepMenuItem.disable <==
      (scenarioProperty === None) || (runState === InFullRun) || (runState === Finished)

    bindButton(runStepButton, runStepMenuItem)


    stopRunMenuItem.disable <==
      (scenarioProperty === None) || (runState === NotRunning) || (runState === InFullRun)

    bindButton(stopRunButton, stopRunMenuItem)


    editMenu.disable <==
      (scenarioProperty === None) || running


    scenarioMenu.disable <==
      (scenarioProperty === None) || running


    runMenu.disable <==
      (scenarioProperty === None)


    bindButton(helpButton, helpMenuItem)


    bindButton(aboutButton, aboutMenuItem)
  }


  private def bindButton(button: Button, menuItem: MenuItem) {
    val buttonId =
      button.id()

    val actionName =
      buttonId.substring(0, buttonId.lastIndexOf("Button"))

    val expectedMenuItemId =
      actionName + "MenuItem"

    if (menuItem.id() != expectedMenuItemId) {
      throw new IllegalArgumentException(
        s"'${menuItem.id()}' should be named '${expectedMenuItemId}' instead"
      )
    }


    val actionImage =
      new Image(getClass.getResourceAsStream(
        s"actionIcons/${actionName}.png"
      ))

    menuItem.graphic =
      new ImageView(actionImage)

    button.graphic =
      new ImageView(actionImage)


    button.disable <==
      menuItem.disable

    button.onAction <==
      menuItem.onAction


    button.tooltip = new scalafx.scene.control.Tooltip() {
      text <==
        menuItem.text
    }
  }

  /*
   * ACTIONS
   */
  def newScenario(): Unit = {
    if (workspace.newDocument()) {
      workspace.setModified()
    }
  }


  def openScenario() {
    workspace.openDocument()
  }


  def saveScenario(): Unit = {
    workspace.saveDocument()
  }


  def saveScenarioAs(): Unit = {
    workspace.saveAsDocument()
  }


  def exportCurrentGraphAsImage(): Unit = {
    val imageFile =
      exportAsImageFileChooser.smartSave(stage)

    if (imageFile == null) {
      return
    }

    try {
      val image =
        if (running())
          runtimeCanvas.get.snapshot(
            new SnapshotParameters(),
            null
          )
        else
          workspace.designCanvas.get.snapshot(
            new SnapshotParameters(),
            null
          )

      ImageIO.write(
        SwingFXUtils.fromFXImage(image, null),
        "png",
        imageFile
      )

      Alerts.showInfo("Graph exported successfully.")
    } catch {
      case ex: Exception =>
        ex.printStackTrace(System.err)

        Platform.runLater {
          Alerts.showException(ex, alertType = AlertType.Warning)
        }
    }
  }


  def exitProgram(): Unit = {
    workspace.closeStage()
  }


  def selectAll(): Unit = {
    designGraph =
      designGraph.selectAll
  }


  def saveConsoleAs(): Unit = {
    val chosenFile =
      consoleFileChooser.smartSave(stage)

    if (chosenFile == null) {
      return
    }

    try {
      Files.write(
        chosenFile.toPath,
        consoleArea.getText().getBytes(Charset.forName("utf-8"))
      )
    } catch {
      case ex: IOException =>
        Alerts.showException(ex, alertType = AlertType.Warning)
    }
  }


  def clearConsole(): Unit = {
    consoleArea.clear()
  }


  def showScenarioName(): Unit = {
    Alerts.showInfo(
      workspace.scenario.get.name,
      "Scenario name"
    )
  }

  def showScenarioHelp(): Unit = {
    workspace.scenario.get.showHelp()
  }


  def showScenarioSettings(): Unit = {
    workspace.scenario.get.showSettings(designGraph).foreach(newGraph =>
      workspace.designCanvas = Some(
        new GraphCanvas[V, L, G](
          workspace.scenario.get.createDesignController(),
          newGraph
        )
      )
    )
  }


  def run(): Unit = {
    runState() =
      InFullRun

    var executedSteps =
      0

    val runStepsBeforePausing =
      workspace.scenario.get.runStepsBeforePausing


    while (internalRunStep()) {
      executedSteps += 1

      if (runStepsBeforePausing > 0 && executedSteps % runStepsBeforePausing == 0) {
        InputDialogs.askYesNoCancel(s"The algorithm has already performed ${executedSteps} steps.\n\nDo you wish to continue?") match {
          case Some(true) =>
          //Just do nothing

          case _ =>
            runState() =
              Finished

            return
        }
      }
    }
  }


  def runStep(): Unit = {
    runState() =
      InStepRun

    internalRunStep()
  }


  private def internalRunStep(): Boolean = {
    try {
      val (newGraph, canProceed) =
        algorithm.runStep(stepIndex, runtimeGraph, outputConsole)

      require(newGraph != null)

      runtimeGraph =
        newGraph

      if (canProceed) {
        stepIndex +=
          1

        true
      } else {
        runState() =
          Finished

        false
      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace(System.err)

        Alerts.showException(ex, alertType = AlertType.Warning)

        runState() =
          NotRunning

        false
    }
  }


  def stopRun(): Unit = {
    runState() =
      NotRunning
  }


  def installPredefinedScenarios(): Unit = {
    workspace.installPredefinedScenarios()
  }

  def showScenariosDirectory(): Unit = {
    workspace.showScenariosDirectory()
  }


  def showHelp(): Unit = {
    DesktopUtils.openBrowser(appInfo.website)
  }


  def showAboutBox(): Unit = {
    aboutBox.showAndWait()
  }


  @FXML
  var graphCanvasPane: javafx.scene.control.ScrollPane = _


  @FXML
  var consoleArea: javafx.scene.control.TextArea = _


  @FXML
  var newMenuItem: javafx.scene.control.MenuItem = _

  @FXML
  var openMenuItem: javafx.scene.control.MenuItem = _

  @FXML
  var saveMenuItem: javafx.scene.control.MenuItem = _

  @FXML
  var saveAsMenuItem: javafx.scene.control.MenuItem = _


  @FXML
  var exportAsImageMenuItem: javafx.scene.control.MenuItem = _


  @FXML
  var editMenu: javafx.scene.control.Menu = _


  @FXML
  var scenarioMenu: javafx.scene.control.Menu = _


  @FXML
  var runMenu: javafx.scene.control.Menu = _

  @FXML
  var fullRunMenuItem: javafx.scene.control.MenuItem = _

  @FXML
  var runStepMenuItem: javafx.scene.control.MenuItem = _

  @FXML
  var stopRunMenuItem: javafx.scene.control.MenuItem = _


  @FXML
  var installPredefinedScenariosMenuItem: javafx.scene.control.MenuItem = _


  @FXML
  var helpMenuItem: javafx.scene.control.MenuItem = _

  @FXML
  var aboutMenuItem: javafx.scene.control.MenuItem = _


  @FXML
  var newButton: javafx.scene.control.Button = _

  @FXML
  var openButton: javafx.scene.control.Button = _

  @FXML
  var saveButton: javafx.scene.control.Button = _

  @FXML
  var saveAsButton: javafx.scene.control.Button = _


  @FXML
  var fullRunButton: javafx.scene.control.Button = _

  @FXML
  var runStepButton: javafx.scene.control.Button = _

  @FXML
  var stopRunButton: javafx.scene.control.Button = _


  @FXML
  var helpButton: javafx.scene.control.Button = _

  @FXML
  var aboutButton: javafx.scene.control.Button = _
}