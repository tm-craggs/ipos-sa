package api.service;

import io.javalin.Javalin;

public class Server {

    public static void start() {
        OrderService orderService = new OrderService();

        // create javalin server
        Javalin app = Javalin.create(config -> {

            // show connection confirmation at root
            config.routes.get("/", ctx -> ctx.result("IPOS-SA API has been reached"));

            // implement order tracking. Query takes in orderId, Order status is returned
            config.routes.get("/track", ctx -> {

                // get parameter from URL
                String idParam = ctx.queryParam("id");

                // check if parameter is not null or empty
                if (idParam == null || idParam.isEmpty()) {
                    ctx.status(400);
                    return;
                }

                // convert orderId to int
                int id = Integer.parseInt(idParam);

                // check if orderId exists in orderService
                orderService.getOrder(id).ifPresentOrElse(
                        // if none exist, return 404
                        ctx::json,
                        () -> ctx.status(404)
                );
            });

            // TODO: implement catalogue viewing and order placing


        });

        // start server
        app.start(3001);
        System.out.println("Server started on port 3001");
    }

}
