package com.mds;

import com.mds.core.OrderBook;
import com.mds.core.OrderDepthStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderBookRestController {
    OrderDepthStore depthStore = OrderDepthStore.getInstance();

    @GetMapping
    public Collection<OrderBook> list() {
        return depthStore.getOrderBooks();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody OrderBook orderBook) {
        depthStore.processOrder(orderBook);
    }
}
