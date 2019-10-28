package com.mds.core;

import com.mds.core.core.OrderBook;
import com.mds.core.core.TopLevelnstrument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

@SpringBootApplication(scanBasePackages = {"com.mds.core"})
public class MainController {

    private static HashMap<String, TopLevelnstrument> topLevelInstrumentMap = new HashMap();
    private static HashMap<String, OrderBook> orderBookMap = new HashMap();
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    public static void main(String[] args) {
        SpringApplication.run(MainController.class, args);

        try {
            loadTopInstrument();
            loadOrderBookExchange();

            log.info("instrument {}, orders {} ", topLevelInstrumentMap, orderBookMap);
        } catch (Exception ex) {
            System.out.println("Failure loading file " + ex.getMessage());
        }
    }


    /**
     * This method adds up all top level instruments from the file
     */
    static void loadTopInstrument() throws IOException {
        URL topInstrumentURL = MainController.class.getClassLoader().getResource("top_instrument_book.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(topInstrumentURL.openStream()))) {
            reader.lines().skip(1).forEach(
                    line -> {
                        String[] data = line.split(",");
                        int pos = 0;
                        TopLevelnstrument topLevelnstrument = new TopLevelnstrument();
                        topLevelnstrument.setSymbol(data[pos++]);
                        topLevelnstrument.setBestBidPrice(Double.parseDouble(data[pos++]));
                        topLevelnstrument.setBestBidQty(Double.parseDouble(data[pos++]));
                        topLevelnstrument.setBestAskPrice(Double.parseDouble(data[pos++]));
                        topLevelnstrument.setBestAskQty(Double.parseDouble(data[pos++]));
                        topLevelInstrumentMap.put(topLevelnstrument.getSymbol(), topLevelnstrument);
                    }
            );
        }
    }

    /**
     * This method adds up all top level instruments from the file
     */
    static void loadOrderBookExchange() throws IOException{
        URL orderBookURL = MainController.class.getClassLoader().getResource("order_book_exchanges.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(orderBookURL.openStream()))) {
            reader.lines().skip(1).forEach(
                    line -> {
                        String[] data = line.split(",");
                        int pos = 0;
                        OrderBook orderBook = new OrderBook();
                        orderBook.setType(data[pos++]);
                        orderBook.setOrder_id(data[pos++]);
                        orderBook.setQuantity(StringUtils.isEmpty(data[pos])?0:
                                Double.parseDouble(data[pos++]));

                        if(data.length > pos) {
                            orderBook.setSymbol(data[pos++]);
                            orderBook.setSide(data[pos++]);
                            orderBook.setLimitPrice(StringUtils.isEmpty(data[pos]) ? 0 :
                                    Double.parseDouble(data[pos++]));
                        }

                        if (orderBookMap.containsKey(orderBook.getOrder_id())) {
                            if ("CANCEL_ORDER".equalsIgnoreCase(orderBook.getType())) {
                                orderBookMap.remove(orderBook.getOrder_id());
                            } else { //amend
                                OrderBook newOrderBook = orderBookMap.get(orderBook.getOrder_id());
                                newOrderBook.setQuantity(orderBook.getQuantity());
                                orderBookMap.put(newOrderBook.getOrder_id(), newOrderBook);
                            }

                        } else {
                            orderBookMap.put(orderBook.getOrder_id(), orderBook);
                        }
                    }
            );
        }
    }
}

