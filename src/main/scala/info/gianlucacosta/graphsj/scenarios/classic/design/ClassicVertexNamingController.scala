package info.gianlucacosta.graphsj.scenarios.classic.design

import info.gianlucacosta.eighthbridge.core.facets.HasNameSettable
import info.gianlucacosta.eighthbridge.fx.controllers.creation.CreateVertexWithName
import scalafx.geometry.Point2D

trait ClassicVertexNamingController
  extends ClassicDesignController
    with CreateVertexWithName
  {
    override type DesignVertex <:
      ClassicDesignVertex with HasNameSettable

    override protected def createNamedVertex(center: Point2D, vertexName: String): DesignVertex =
      designVertexClass
        .newInstance()
        .setCenter(center)
        .setName(vertexName)
}
