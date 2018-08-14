package com.github.conceptacid

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import java.util.logging.Logger

class AppVerticle : AbstractVerticle() {
    var timerID = 0L

    companion object {
        var log = Logger.getLogger("AppVerticle");
    }

    override fun start(startFuture: Future<Void>?) {
        val id = config().getString("ID")
        log.info(" SPAWNED app verticle \"${id}\"")

        vertx.eventBus().consumer<Any>("mynamspace.app.${id}") { message ->
            val cmd = message.body()
            log.info(" - app verticle \"${id}\" handled message ${cmd}")
        }

        timerID = vertx.setPeriodic(1000) {
            log.info(" - app verticle \"${id}\" is alive")
        }
    }
}