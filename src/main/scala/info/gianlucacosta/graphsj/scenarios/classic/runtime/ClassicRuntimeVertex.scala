package info.gianlucacosta.graphsj.scenarios.classic.runtime

import info.gianlucacosta.eighthbridge.core.Facet
import info.gianlucacosta.eighthbridge.core.facets.{HasLabel, HasLabelSettable, HasName, HasNameSettable, HasSelected, HasSelectedSettable, HasStyleClasses, HasStyleClassesSettable}
import info.gianlucacosta.eighthbridge.fx.VisualVertex
import info.gianlucacosta.eighthbridge.fx.facets.{HasCenter, HasCenterSettable}
import info.gianlucacosta.graphsj.scenarios.classic.ClassicAlgorithm
import info.gianlucacosta.graphsj.scenarios.classic.design.ClassicDesignVertex

abstract class ClassicRuntimeVertex(algorithm: ClassicAlgorithm)
  extends VisualVertex
    with HasCenterSettable
    with HasSelectedSettable
    with HasNameSettable
    with HasLabel
    with HasStyleClasses {

  override def styleClasses: Set[String] = {
    if (!algorithm.conclusionReached && algorithm.partialSolutionVertexes.contains(this.asInstanceOf[algorithm.V]))
      Set("partialSolution")
    else if (algorithm.conclusionReached && algorithm.conclusionVertexes.contains(this.asInstanceOf[algorithm.V]))
      Set("conclusion")
    else
      Set()
  }

  def copyFacetsFrom(designVertex: ClassicDesignVertex): this.type = {
    var result: this.type =
      this

    Facet.as[HasCenterSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasCenter](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setCenter(facetedDesignVertex.center).asInstanceOf[this.type]
      })
    )

    Facet.as[HasNameSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasName](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setName(facetedDesignVertex.name).asInstanceOf[this.type]
      })
    )

    Facet.as[HasLabelSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasLabel](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setLabel(facetedDesignVertex.label).asInstanceOf[this.type]
      })
    )

    Facet.as[HasSelectedSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasSelected](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setSelected(facetedDesignVertex.selected).asInstanceOf[this.type]
      })
    )

    Facet.as[HasStyleClassesSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasStyleClasses](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setStyleClasses(facetedDesignVertex.styleClasses).asInstanceOf[this.type]
      })
    )

    result
  }
}

