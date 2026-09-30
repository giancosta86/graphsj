package info.gianlucacosta.graphsj

import java.io.File
import java.lang.reflect.Modifier

import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

import scala.collection.JavaConversions._
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader

class ScenarioRepository(baseDirectory: File) {
  private val jarFiles: Array[File] =
    if (baseDirectory.isDirectory)
      baseDirectory
        .listFiles()
        .filter(_.getName.toLowerCase.endsWith(".jar"))
    else
      Array[File]()


  val scenariosClassLoader = new URLClassLoader(
    jarFiles.map(_.toURI.toURL),
    getClass.getClassLoader
  )


  val scenarioFactories =
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
            .getSubTypesOf(classOf[ScenarioFactory[_, _, _]])
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
}
