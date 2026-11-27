package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.eighthbridge.fx.VisualVertex
import info.gianlucacosta.eighthbridge.fx.controllers.creation.CreateVertexWithName
import info.gianlucacosta.eighthbridge.fx.controllers.editing.{EditLinkWeight, EditVertexName}
import info.gianlucacosta.graphsj.scenarios.classic.design.{ClassicDesignController, ClassicDesignLink, ClassicVertexNamingController}
import scalafx.geometry.Point2D

class PrimDesignController
  extends ClassicVertexNamingController
  with EditVertexName
  with EditLinkWeight {
  override type DesignVertex =
    PrimDesignVertex

  override type DesignLink =
    PrimDesignEdge

  override val designVertexClass: Class[_ <: PrimDesignVertex] =
    classOf[PrimDesignVertex]

  override val designLinkClass: Class[_ <: PrimDesignEdge] =
    classOf[PrimDesignEdge]
}
