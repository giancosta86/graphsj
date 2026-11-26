package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.fx.{Styles, VisualGraph}
import info.gianlucacosta.graphsj.sdk.Scenario
import info.gianlucacosta.helios.desktop.DesktopUtils
import info.gianlucacosta.helios.fx.dialogs.InputDialogs

object ClassicScenario {
  private val WebsiteUrl =
    "https://github.com/giancosta86/graphsj-scenarios"

  private val Styles =
    getClass.getResource("ClassicStyles.css")
}

abstract class ClassicScenario extends Scenario {
  protected var verbose: Boolean =
    true //TODO! false?

  override def stylesheets: List[String] =
    List(
      Styles.resourceUrl,
      ClassicScenario.Styles
    )
      .map(_.toExternalForm)

  override def showHelp(): Unit = {
    DesktopUtils.openBrowser(ClassicScenario.WebsiteUrl)
  }

  //TODO! What if I wanted to adjust GraphCanvas after setting the options?
  override def showSettings(designGraph: VisualGraph): Option[VisualGraph] = {
    InputDialogs
      .askYesNoCancel(
        message=
          "Make the algorithm verbose?",
        header=
          "Algorithm settings"
      )
      .foreach(requestedVerbose =>
        verbose =
          requestedVerbose
      )

    None //TODO! When returning Some(designGraph), the graph canvas shrinks!
  }

  override def runStepsBeforePausing: Int =
    0
}
