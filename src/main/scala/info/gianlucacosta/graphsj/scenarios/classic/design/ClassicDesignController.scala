package info.gianlucacosta.graphsj.scenarios.classic.design

import info.gianlucacosta.eighthbridge.fx.VisualVertex
import info.gianlucacosta.eighthbridge.fx.controllers.bundles.StructuralEditing
import info.gianlucacosta.graphsj.scenarios.classic.ClassicControllerBase
import info.gianlucacosta.graphsj.scenarios.prim.PrimDesignEdge

trait ClassicDesignController
  extends ClassicControllerBase
  with StructuralEditing {

  type DesignVertex <:
    ClassicDesignVertex

  val designVertexClass: Class[_ <: DesignVertex]

  type DesignLink <:
    ClassicDesignLink

  val designLinkClass: Class[_ <: DesignLink]

  override def addLink(sourceVertex: VisualVertex, targetVertex: VisualVertex): Unit = {
    graph =
      graph.addLink(
        sourceVertex,
        targetVertex,
        designLinkClass.newInstance()
      )
  }
}