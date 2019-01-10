package example.stacktrace.htm

import org.numenta.nupic.Parameters
import org.numenta.nupic.algorithms.{Anomaly, SpatialPooler, TemporalMemory}
import org.numenta.nupic.encoders.MultiEncoder
import org.numenta.nupic.network.{Layer, Network, Region}

/**
  * Network wrapper
  *
  * @param p Parameters sent to build the network
  */
case class HtmNetwork(p: Parameters) {

  lazy val network: Network = {
    val newNetwork = Network.create("Test", p).add(region)
    newNetwork
  }

  private lazy val region: Region = {
    val r = Network.createRegion("Cortex").add(layer23)
    val rr = r.close()
    rr
  }

  private lazy val layer23: Layer[_] = {
    val l = Network.createLayer("Layer 2/3", p)
      //   .alterParameter(KEY.AUTO_CLASSIFY, true)
      .add(Anomaly.create())
      .add(new TemporalMemory())
      .add(new SpatialPooler())
    l.add(MultiEncoder.builder().build())
  }

}
