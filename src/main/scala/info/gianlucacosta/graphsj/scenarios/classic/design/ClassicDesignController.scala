package info.gianlucacosta.graphsj.scenarios.classic.design

import info.gianlucacosta.eighthbridge.fx.VisualVertex
import info.gianlucacosta.eighthbridge.fx.controllers.bundles.StructuralEditing
import info.gianlucacosta.graphsj.scenarios.classic.ClassicControllerBase
import info.gianlucacosta.graphsj.scenarios.prim.PrimDesignEdge
import scalafx.geometry.Point2D

trait ClassicDesignController
  extends ClassicControllerBase
  with StructuralEditing {

  type DesignVertex <:
    ClassicDesignVertex

  val designVertexClass: Class[_ <: DesignVertex]

  type DesignLink <:
    ClassicDesignLink

  val designLinkClass: Class[_ <: DesignLink]

  override def addVertex(center: Point2D): Unit = {
    val newVertex: DesignVertex =
      designVertexClass
        .newInstance()
        .setCenter(center)

    graph =
      graph.addVertex(newVertex)
  }

  override def addLink(sourceVertex: VisualVertex, targetVertex: VisualVertex): Unit = {
    val newLink =
      designLinkClass.newInstance()

    graph =
      graph.addLink(
        sourceVertex,
        targetVertex,
        newLink
      )
  }
}