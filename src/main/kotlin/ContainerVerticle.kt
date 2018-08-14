package com.github.conceptacid

import io.vertx.core.*
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.util.logging.Logger
import io.vertx.ext.web.handler.BodyHandler

class ContainerVerticle : AbstractVerticle() {

    companion object {
        var log = Logger.getLogger("ContainerVerticle");
    }

    override fun start(startFuture: Future<Void>?) {

        val router = createRouter()
        val port = config().getInteger("http.port", 8080)

        vertx.eventBus().consumer<Any>("mynamspace.container.spawn") { message ->
            val appVerticleID = message.body()
            log.info(" - HANDLE SPAWN message \"${appVerticleID}\"")
            val appVerticleConfig = JsonObject().put("ID", appVerticleID)

            vertx.deployVerticle(AppVerticle::class.java.name,
                    DeploymentOptions()
                            .setConfig(appVerticleConfig)
                            .setInstances(1)
                            .setHa(true))
        }

        vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(port) { result ->
                    if (result.succeeded()) {
                        log.info("Listening on port $port")
                        startFuture?.complete()
                    } else {
                        startFuture?.fail(result.cause())
                    }
                }
    }

    private fun createRouter(): Router {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/").handler(handlerRoot)
        return router
    }

    val handlerRoot = Handler<RoutingContext> { routingContext ->
        val cmd = routingContext.bodyAsString
        val tokens = cmd.split(" ")
        if (tokens[0] == "spawn") {
            vertx.eventBus().send("mynamspace.container.spawn", tokens[1])  // round-robin
            routingContext.response().end("Successfully handled command ${cmd}\n")
        } else if (tokens[0] == "send") {
            vertx.eventBus().send("mynamspace.app.${tokens[1]}", tokens[2])
            routingContext.response().end("success\n")
        } else {
            routingContext.response().end("ERROR: Unknown command ${cmd}\n")
        }
    }
}