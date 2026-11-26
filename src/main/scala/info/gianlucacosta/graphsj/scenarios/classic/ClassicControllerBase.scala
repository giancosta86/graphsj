package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.facets.HasLabel
import info.gianlucacosta.eighthbridge.fx.facets.HasOptionalCustomLabelCenterSettable
import info.gianlucacosta.eighthbridge.fx.linknodes.labeled.LabeledLinkNode
import info.gianlucacosta.eighthbridge.fx.vertexnodes.labeled.LabeledVertexNode
import info.gianlucacosta.eighthbridge.fx.{GraphCanvasController, LinkNode, VertexNode, VisualLink, VisualVertex}

class ClassicControllerBase extends GraphCanvasController {
  override def createVertexNode(vertex: VisualVertex): VertexNode =
    new LabeledVertexNode(graphCanvas, vertex)

  override def createLinkNode(link: VisualLink): LinkNode =
    new LabeledLinkNode(
      graphCanvas,
      link.asInstanceOf[VisualLink with HasLabel with HasOptionalCustomLabelCenterSettable]
    )
}
