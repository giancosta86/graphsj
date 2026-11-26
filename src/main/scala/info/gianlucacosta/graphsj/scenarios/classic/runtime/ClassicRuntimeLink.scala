package info.gianlucacosta.graphsj.scenarios.classic.runtime

import info.gianlucacosta.eighthbridge.core.Facet
import info.gianlucacosta.eighthbridge.core.directed.{Arc, HasBarbsSettable}
import info.gianlucacosta.eighthbridge.core.facets.{HasLabel, HasLabelSettable, HasName, HasNameSettable, HasSelected, HasSelectedSettable, HasStyleClasses, HasStyleClassesSettable, HasWeightBounded, HasWeightBoundedSettable}
import info.gianlucacosta.eighthbridge.fx.VisualLink
import info.gianlucacosta.eighthbridge.fx.facets.{HasHandles, HasHandlesSettable, HasOptionalCustomLabelCenter, HasOptionalCustomLabelCenterSettable}
import info.gianlucacosta.graphsj.scenarios.classic.ClassicAlgorithm
import info.gianlucacosta.graphsj.scenarios.classic.design.ClassicDesignLink

abstract class ClassicRuntimeLink(algorithm: ClassicAlgorithm)
  extends VisualLink
  with HasSelectedSettable
  with HasHandlesSettable
  with HasStyleClasses
  with HasLabelSettable
  with HasOptionalCustomLabelCenterSettable {
  override def styleClasses: Set[String] = {
    if (!algorithm.conclusionReached && algorithm.partialSolutionLinks.contains(this.asInstanceOf[algorithm.L]))
      Set("partialSolution")
    else if (algorithm.conclusionReached && algorithm.conclusionLinks.contains(this.asInstanceOf[algorithm.L]))
      Set("conclusion")
    else
      Set()
  }

  def copyFacetsFrom(designLink: ClassicDesignLink): this.type = {
    var result: this.type =
      this

    Facet.as[HasNameSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasName](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setName(facetedDesignLink.name).asInstanceOf[this.type]
      })
    )

    Facet.as[HasLabelSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasLabel](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setLabel(facetedDesignLink.label).asInstanceOf[this.type]
      })
    )

    Facet.as[HasSelectedSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasSelected](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setSelected(facetedDesignLink.selected).asInstanceOf[this.type]
      })
    )

    Facet.as[HasStyleClassesSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasStyleClasses](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setStyleClasses(facetedDesignLink.styleClasses).asInstanceOf[this.type]
      })
    )

    Facet.as[HasHandlesSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasHandles](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setHandles(facetedDesignLink.handles).asInstanceOf[this.type]
      })
    )

    Facet.as[HasOptionalCustomLabelCenterSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasOptionalCustomLabelCenter](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setCustomLabelCenter(facetedDesignLink.customLabelCenter).asInstanceOf[this.type]
      })
    )

    Facet.as[HasBarbsSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[Arc](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setBarbs(facetedDesignLink.barbs).asInstanceOf[this.type]
      })
    )

    Facet.as[HasWeightBoundedSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasWeightBounded](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setWeight(
            demand=
              facetedDesignLink.demand,
            weight=
              facetedDesignLink.weight,
            capacity=
              facetedDesignLink.capacity
          ).asInstanceOf[this.type]
      })
    )

    result
  }
}
