package api.service;

import api.model.CommercialApplication;
import api.model.ItemRequest;
import api.model.OrderRequest;
import cat.CatalogueItem;
import db.DatabaseManager;
import io.javalin.Javalin;
import ord.OrderItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Server {

    public static void start() {
        // create javalin server
        Javalin app = Javalin.create(config -> {

            // show connection confirmation at root
            config.routes.get("/", ctx -> ctx.result("IPOS-SA API has been reached"));

            // implement catalogue viewing
            config.routes.get("/cat", ctx -> {
                ctx.json(DatabaseManager.getCatalogueItems());
            });

            // implement order tracking. Query takes in orderId, Order status is returned
            config.routes.get("/track", ctx -> {
                String idParam = ctx.queryParam("id");

                if (idParam == null || idParam.isEmpty()) {
                    ctx.status(400).result("No order ID provided");
                    return;
                }

                DatabaseManager.getOrder(Integer.parseInt(idParam)).ifPresentOrElse(
                        order -> {
                            List<OrderItem> items = DatabaseManager.getOrderItems(String.valueOf(order.getOrderId()));
                            ctx.json(Map.of(
                                    "orderId",        order.getOrderId(),
                                    "merchantId",     order.getMerchantId(),
                                    "orderDate",      order.getOrderDate(),
                                    "orderValue",     order.getOrderValue(),
                                    "dispatchDate",   order.getDispatchDate(),
                                    "deliveredDate",  order.getDeliveredDate(),
                                    "paymentStatus",  order.getPaymentStatus(),
                                    "items",          items
                            ));
                        },
                        () -> ctx.status(404).result("Order not found")
                );
            });

            config.routes.post("/order", ctx -> {
                OrderRequest req = ctx.bodyAsClass(OrderRequest.class);

                if (req.getMerchantId() == null || req.getMerchantId().isEmpty()) {
                    ctx.status(400).result("No merchant ID in request, please double check");
                    return;
                }
                if (req.getItems() == null || req.getItems().isEmpty()) {
                    ctx.status(400).result("No items in request, please double check");
                    return;
                }

                List<OrderItem> orderItems = new ArrayList<>();
                double orderTotal = 0;

                for (ItemRequest item : req.getItems()) {
                    Optional<CatalogueItem> catalogueItem = DatabaseManager.getCatalogueItem(item.getItemId());

                    if (catalogueItem.isEmpty()) {
                        ctx.status(404).result("Item ID: " + item.getItemId() + " not found");
                        return;
                    }

                    CatalogueItem cat = catalogueItem.get();

                    if (cat.getAvailability() < item.getQuantity()) {
                        ctx.status(409).result("Insufficient stock for item: " + item.getItemId());
                        return;
                    }

                    double unitCost = cat.getPackageCost();
                    double amount = unitCost * item.getQuantity();
                    orderTotal += amount;
                    orderItems.add(new OrderItem(item.getItemId(), cat.getDescription(), item.getQuantity(), unitCost, amount));
                }

                String orderDate = LocalDate.now().toString();
                int orderId = DatabaseManager.submitOrder(req.getMerchantId(), orderDate, orderTotal, orderItems);

                if (orderId == -1) {
                    ctx.status(500).result("Failed to submit order, please try again");
                    return;
                }

                ctx.status(201).json(Map.of(
                        "orderId", orderId,
                        "merchantId", req.getMerchantId(),
                        "orderDate", orderDate,
                        "total", orderTotal,
                        "status", "Pending"
                ));

            });

            config.routes.post("/apply", ctx -> {

                CommercialApplication[] applications = ctx.bodyAsClass(CommercialApplication[].class);

                if (applications == null || applications.length == 0) {
                    ctx.status(400).result("No applications provided");
                    return;
                }

                boolean success = true;
                for (CommercialApplication application : applications) {
                    if (!DatabaseManager.saveApplication(application)) {
                        success = false;
                        break;
                    }
                }

                if (success) {
                    ctx.status(201).result("Applications submitted successfully");
                } else {
                    ctx.status(500).result("Error saving applications, please try again");
                }

            });

        });

        // start server
        app.start(3001);
        System.out.println("Server started on port 3001");
    }

}
