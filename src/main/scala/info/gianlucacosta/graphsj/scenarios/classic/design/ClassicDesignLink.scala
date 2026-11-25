package info.gianlucacosta.graphsj.scenarios.classic.design

import info.gianlucacosta.eighthbridge.core.facets.HasSelectedSettable
import info.gianlucacosta.eighthbridge.fx.VisualLink
import info.gianlucacosta.eighthbridge.fx.facets.{HasHandlesSettable, HasOptionalCustomLabelCenterSettable}

trait ClassicDesignLink
  extends VisualLink
  with HasSelectedSettable
  with HasHandlesSettable
  with HasOptionalCustomLabelCenterSettable
