#TradingApp

Trading app consists of multiple modules which are required for building any low latency system

market data service

Refer to test class - OrderDepthStoreTest for understanding the code structure.

OrderDepthStore is the code handling logic for building up the order book and aggregating books based on same price.

It provides method to the clients to fetch the data efficiently and quickly with O(n) efficiency.

Spring Boot application has been built to show cases how file upload can be used for building such a case.

Objective: build and serve consolidated market book for all US equities

Description:
US equities can be traded on multiple exchanges.
You can subscribe to market data feed from each exchange to track the book for a security.
We want to build a consolidated book that merges the per-exchange books.

There are two types of market feeds

1.       Top of the book. Each message from the exchange has
SYMBOL, BEST_BID_PRICE, BEST_BID_SIZE, BEST_OFFER_PRICE, BEST_OFFER_SIZE

2.       Order based book. Messages can be

a.       NEW_ORDER: SYMBOL, LIMIT_PRICE, SIDE (BUY/SELL), QUANTITY, ORDER_ID

b.       CANCEL_ORDER: ORDER_ID

c.       MODIFY_ORDER: ORDER_ID, NEW_QUANTITY (modifications allow changing the quantity only)

Here we consume the market data feed (top-of-the-book style and order-based-book style) from multiple exchanges,
and build a consolidated book per symbol such that

Level 0: Bid Size, Bid Price, Offer Price, Offer Size
Level 1: Bid Size, Bid Price, Offer Price, Offer Size
…
Such that Level 0 is the BEST (highest Bid price, lowest offer price), Level 1 is the next best, and so on.
The Size at each level is the sum of the sizes at all the exchanges at that price.
Serve the top 5 levels of the consolidated book.

There are many readers –
all simultaneously requiring the top 5 levels of the consolidated book by symbol.
