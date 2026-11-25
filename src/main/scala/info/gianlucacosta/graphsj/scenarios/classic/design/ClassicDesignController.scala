package info.gianlucacosta.graphsj.scenarios.classic.design

import info.gianlucacosta.eighthbridge.core.facets.HasLabel
import info.gianlucacosta.eighthbridge.fx.{GraphCanvasController, LinkNode, VertexNode, VisualLink, VisualVertex}
import info.gianlucacosta.eighthbridge.fx.controllers.bundles.StructuralEditing
import info.gianlucacosta.eighthbridge.fx.facets.HasOptionalCustomLabelCenter
import info.gianlucacosta.eighthbridge.fx.linknodes.labeled.LabeledLinkNode
import info.gianlucacosta.eighthbridge.fx.vertexnodes.labeled.LabeledVertexNode
import info.gianlucacosta.graphsj.scenarios.classic.ClassicControllerBase

trait ClassicDesignController
  extends ClassicControllerBase
  with StructuralEditing