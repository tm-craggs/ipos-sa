package api.service;

import api.model.Order;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OrderService {


    //TODO: Until ORD is done, the API will return placeholder data
    final private List<Order> orderList;
    public OrderService() {

        orderList = new ArrayList<>();

        Order order1 = new Order (1, 1, "Processing");
        Order order2 = new Order (2, 2, "Dispatched");
        Order order3 = new Order (3, 3, "Out for delivery");
        Order order4 = new Order (4, 4, "Processing");
        Order order5 = new Order (5, 5, "Out for delivery");

        orderList.addAll(Arrays.asList(order1, order2, order3, order4, order5));

    }

    public Optional<Order> getOrder(Integer id){
        Optional<Order> optional = Optional.empty();
        for (Order order : orderList) {
            if (order.getId() == id) {
                optional = Optional.of(order);
                return optional;
            }
        }
        return optional;
    }

}