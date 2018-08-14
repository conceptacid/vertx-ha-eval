package com.github.conceptacid

import com.hazelcast.config.Config
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import io.vertx.core.VertxOptions
import io.vertx.core.eventbus.EventBusOptions
import java.net.NetworkInterface

fun getAddress(): String {
    return NetworkInterface.getNetworkInterfaces().toList().flatMap {iface ->
        iface.inetAddresses.toList()
                .filter { it.address.size == 4 }
                .filter { !it.isLoopbackAddress }
                .filter { it.address[0] != 10.toByte() }
                .map { it.hostAddress }
    }.first()
}

fun main(args: Array<String>) {
    val hzConfig = Config()
    val mgr = HazelcastClusterManager(hzConfig)  // empty config -> use default
    val hostAddress = getAddress()

    val options = VertxOptions()
            .setClustered(true)
            .setClusterHost(hostAddress)
            .setClusterPort(18001)
            .setClusterManager(mgr)
            //.setQuorumSize(2)
            .setHAEnabled(true)

    val eventBusOptions = EventBusOptions()
    eventBusOptions
            .setClustered(true)
            .setHost(hostAddress)
            .setPort(18002)
    options.setEventBusOptions(eventBusOptions)

    Vertx.clusteredVertx(options) { res ->
        if (res.succeeded()) {
            val vertx = res.result()
            vertx.deployVerticle(ContainerVerticle::class.java.name,
                    DeploymentOptions()
                            .setHa(false)) // ContainerVerticle should not restart
        }
    }
}