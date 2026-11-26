package info.gianlucacosta.graphsj

import info.gianlucacosta.graphsj.scenarios.prim.PrimScenarioFactory
import info.gianlucacosta.graphsj.sdk.ScenarioFactory

import java.io.File

class ScenarioRepository(baseDirectory: File) {
  //TODO!
  /*private val jarFiles: List[File] =
    if (baseDirectory.isDirectory)
      baseDirectory
        .listFiles()
        .filter(
          _.getName.toLowerCase.endsWith(".jar")
        )
        .toList
    else
      Nil

  val scenariosClassLoader = new URLClassLoader(
    jarFiles.map(_.toURI.toURL),
    getClass.getClassLoader
  )

  val scenarioFactories: List[ScenarioFactory] =
    jarFiles
      .flatMap(jarFile =>
        try {
          val configuration =
            new ConfigurationBuilder()
              .addClassLoader(scenariosClassLoader)
              .setUrls(jarFile.toURI.toURL)

          val reflections =
            new Reflections(configuration)

          reflections
            .getSubTypesOf(classOf[ScenarioFactory])
            .filter(scenarioFactoryClass =>
              !Modifier.isAbstract(scenarioFactoryClass.getModifiers) &&
                !scenarioFactoryClass.isInterface
            )
            .map(scenarioFactoryClass =>
              scenarioFactoryClass.newInstance()
            )
        } catch {
          case ex: Exception =>
            ex.printStackTrace(System.err)
            List()
        }
      )
      .sortBy(_.scenarioName)
   */

  val scenariosClassLoader: ClassLoader =
    getClass.getClassLoader

  val scenarioFactories: List[ScenarioFactory] =
    List(
      new PrimScenarioFactory
    )
}
