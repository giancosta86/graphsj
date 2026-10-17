package info.gianlucacosta.graphsj.windows.main

import java.io._
import java.net.URL
import java.nio.file.Files
import java.util.regex.Pattern
import javafx.beans.Observable
import javafx.stage.Stage

import javax.json.{Json, JsonObject}
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.converters.ConversionException
import com.thoughtworks.xstream.io.xml.StaxDriver
import info.gianlucacosta.eighthbridge.fx.GraphCanvas
import info.gianlucacosta.eighthbridge.fx.graph.{VisualGraph}
import info.gianlucacosta.graphsj.{App, ScenarioRepository, windows}
import info.gianlucacosta.helios.apps.AppInfo
import info.gianlucacosta.helios.desktop.DesktopUtils
import info.gianlucacosta.helios.fx.dialogs.{Alerts, BusyDialog, InputDialogs}
import info.gianlucacosta.helios.fx.workspace.Workspace

import scala.collection.JavaConversions._
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.stage.FileChooser


private object GraphWorkspace {
  private val PredefinedScenariosJarFileNameRegex =
    Pattern.compile(raw"graphsj-scenarios-\d+(?:[\d\.]+)\.jar")
}


private class GraphWorkspace
(
  appInfo: AppInfo,
  stage: Stage,
  initialScenarioRepository: ScenarioRepository,
  documentFileChooser: FileChooser
) extends Workspace(stage, documentFileChooser) {
  private val scenarioRepositoryLock =
    new Object

  private var _scenarioRepository: ScenarioRepository = _
  private var xstream: XStream = _


  def scenarioRepository: ScenarioRepository = {
    scenarioRepositoryLock.synchronized {
      _scenarioRepository
    }
  }

  def scenarioRepository_=(newValue: ScenarioRepository): Unit = {
    scenarioRepositoryLock.synchronized {
      _scenarioRepository =
        newValue

      xstream =
        new XStream(new StaxDriver) {
          setClassLoader(newValue.scenariosClassLoader)
        }
    }
  }


  scenarioRepository =
    initialScenarioRepository


  val scenarioProperty =
    ObjectProperty[Option[VisualScenario]](None)

  def scenario: Option[VisualScenario] =
    scenarioProperty.get

  private def scenario_=(newValue: Option[VisualScenario]): Unit =
    scenarioProperty.set(newValue)

  scenarioProperty.addListener((observable: Observable) => {
    scenarioProperty().foreach(scenario => {
      val stylesheets =
        MainWindowController.AppStylesheet.toExternalForm :: scenario.stylesheets

      stage.scene().getStylesheets.setAll(stylesheets: _*)
    })
  })


  val designCanvasProperty =
    ObjectProperty[Option[GraphCanvas]](None)

  def designCanvas: Option[GraphCanvas] =
    designCanvasProperty.get

  def designCanvas_=(newValue: Option[GraphCanvas]): Unit = {
    designCanvasProperty.set(newValue)
  }


  override protected def doNew(): Boolean = {
    if (scenarioRepository.scenarioFactories.isEmpty) {
      val installPredefinedScenariosButton =
        new ButtonType("Install predefined scenarios")


      val showScenariosDirectoryButton =
        new ButtonType("Show scenarios directory")


      val noScenariosAlert = new Alert(AlertType.Confirmation) {
        initOwner(stage)

        headerText =
          "No scenarios installed"

        contentText = (
          s"${appInfo.name} requires at least one scenario.\n\n"
            + s"${appInfo.name} can automatically install the predefined scenario pack; alternatively, "
            + "you can download one or more .jar files providing scenarios and copy them "
            + "to the scenarios directory."
          )


        buttonTypes = List(
          installPredefinedScenariosButton,
          showScenariosDirectoryButton,
          ButtonType.Cancel
        )

        Alerts.fix(this)
      }

      val noScenariosResolutionOption =
        noScenariosAlert.showAndWait()

      noScenariosResolutionOption match {
        case Some(`installPredefinedScenariosButton`) =>
          installPredefinedScenarios()


        case Some(`showScenariosDirectoryButton`) =>
          showScenariosDirectory()

        case _ =>
        //Just do nothing
      }

      return false
    }


    val scenarioFactoryOption =
      InputDialogs.askForItem(
        "Scenario:",
        scenarioRepository.scenarioFactories,
        header = "New scenario..."
      )

    scenarioFactoryOption.exists(scenarioFactory => {
      val newScenarioOption =
        scenarioFactory.createScenario

      newScenarioOption.exists(newScenario => {
        var visualScenario: VisualScenario =
          newScenario.asInstanceOf[VisualScenario]

        val newDesignCanvas =
          new GraphCanvas(
            visualScenario.createDesignController(),
            visualScenario.createDesignGraph()
              .asInstanceOf[VisualGraph]
          )

        scenario =
          Some(visualScenario)

        designCanvas =
          Some(newDesignCanvas)

        true
      })
    })
  }


  override protected def doOpen(sourceFile: File): Boolean = {
    try {
      val sourceReader =
        new InputStreamReader(
          new FileInputStream(sourceFile),
          "UTF-8"
        )

      try {
        val loadedObject =
          scenarioRepositoryLock.synchronized {
            xstream.fromXML(sourceReader)
          }

        val document =
          loadedObject.asInstanceOf[GraphDocument]

        scenario =
          Some(document.scenario)

        designCanvas =
          Some(new GraphCanvas(
            document.scenario.createDesignController(),
            document.designGraph
          ))
      } finally {
        sourceReader.close()
      }

    } catch {
      case ex: ConversionException =>
        val lastColonIndex =
          ex.getShortMessage.lastIndexOf(":")

        if (lastColonIndex > -1) {
          ex.printStackTrace(System.err)

          val scenarioName =
            ex.getShortMessage.substring(0, lastColonIndex).trim

          Alerts.showWarning(
            s"The requested scenario cannot be loaded:\n\n'${scenarioName}'\n\nPlease, ensure that the related JAR file is available in the scenarios directory."
          )

          showScenariosDirectory()
        } else {
          Alerts.showException(ex)
        }
    }

    true
  }


  override protected def doSave(targetFile: File): Boolean = {
    val document =
      GraphDocument(
        scenario.get,
        designCanvas.get.graph()
      )

    val targetWriter =
      new OutputStreamWriter(
        new FileOutputStream(targetFile),
        "UTF-8"
      )

    try {
      scenarioRepositoryLock.synchronized {
        xstream.toXML(document, targetWriter)
      }
    } finally {
      targetWriter.close()
    }

    true
  }


  def installPredefinedScenarios(): Unit = {
    App.ensureScenariosDirectory()

    try {
      val existingScenariosJarFile =
        App.ScenariosDirectory
          .listFiles()
          .find(file =>
            GraphWorkspace
              .PredefinedScenariosJarFileNameRegex
              .matcher(file.getName)
              .matches()
          )


      if (existingScenariosJarFile.nonEmpty) {
        val reinstallOption =
          InputDialogs.askYesNoCancel(
            "The predefined scenarios are already installed.\n\nDo you wish to reinstall them?",
            s"${appInfo.name} - Scenarios"
          )

        if (!reinstallOption.contains(true)) {
          return
        }


        require(
          existingScenariosJarFile.get.delete(),
          s"Cannot delete the predefined scenarios file:\n'${existingScenariosJarFile.get.getAbsolutePath}'"
        )
      }

      new BusyDialog(
        stage,
        "Installing scenarios..."
      ) {
        run {
          downloadPredefinedScenariosJar()
        }
      }
    } catch {
      case ex: Exception =>
        Alerts.showException(ex, alertType = AlertType.Warning)
    }
  }


  private def downloadPredefinedScenariosJar(): Unit = {
    val scenariosApiJsonReader =
      Json.createReader(App.ScenariosApiUrl.openStream())


    try {
      val scenariosApiJson =
        scenariosApiJsonReader.readObject()

      val assets =
        scenariosApiJson.getJsonArray("assets")

      assets.foreach(asset => {
        val assetObject =
          asset.asInstanceOf[JsonObject]

        val assetName =
          assetObject.getString("name")

        val isScenariosJarAsset =
          GraphWorkspace
            .PredefinedScenariosJarFileNameRegex
            .matcher(assetName)
            .matches()

        if (isScenariosJarAsset) {
          val targetFile =
            new File(App.ScenariosDirectory, assetName)

          val sourceUrl =
            new URL(assetObject.getString("browser_download_url"))

          val sourceStream =
            sourceUrl.openStream()


          try {
            Files.copy(sourceStream, targetFile.toPath)
          } finally {
            sourceStream.close()
          }

          Platform.runLater {
            Alerts.showInfo("The predefined scenarios have been installed.", s"${appInfo.name} - Scenarios")
          }
        }
      })
    } finally {
      scenariosApiJsonReader.close()
    }
  }


  def showScenariosDirectory(): Unit = {
    try {
      App.ensureScenariosDirectory()
      DesktopUtils.openFile(App.ScenariosDirectory)
    } catch {
      case ex: Exception =>
        Alerts.showException(ex, alertType = AlertType.Warning)
    }
  }
}