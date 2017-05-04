[![Build Status](https://travis-ci.org/iisador/jBTCE.svg?branch=master)](https://travis-ci.org/iisador/jBTCE)
[![Coverage Status](https://coveralls.io/repos/github/iisador/jBTCE/badge.svg?branch=master)](https://coveralls.io/github/iisador/jBTCE?branch=master)

jBTCE
=====
---
NOTE: Uses java version >= 1.8

---
Brief description
--------------------

Trade api wrapper to www.btc-e.com.
Provides full access to public and private API.

Features
=========
* Public v2 API: [ticker](https://btc-e.com/api/2/btc_usd/ticker), [trades](https://btc-e.com/api/2/btc_usd/trades), [depth](https://btc-e.com/api/2/btc_usd/depth), [fee](https://btc-e.com/api/2/btc_usd/fee)
* Public v3 API: [ticker](https://btc-e.com/api/3/ticker/btc_usd-btc-rur), [trades](https://btc-e.com/api/3/trades/btc_usd-btc-rur), [depth](https://btc-e.com/api/3/depth/btc_usd-btc-rur), [fee](https://btc-e.com/api/3/fee/btc_usd-btc-rur)
* Private API
  - getInfo
  - OrderList (some attributes doesn't work on btc-e side)
  - TransHistory (some attributes doesn't work on btc-e side)
  - TradeHistory (some attributes doesn't work on btc-e side)
  - CancelOrder
  - Trade

How-To
======
use Public v2 api:
```java
// Create public api using default connector
PublicApi api = new PublicApi();
Tick tick = api.getTick(BTC_USD);
System.out.println(tick);
```

use Public v3 api:
```java
// Create public api using default connector
PublicApiV3 api = new PublicApiV3();
        Map<Pair, Tick> ticks = api.getTicks(BTC_USD, BTC_RUR);
        ticks.entrySet().stream()
                .map(e -> String.format("%s: %s", e.getKey(), e.getValue()))
                .forEach(System.out::println);
```
or Private api:
```java
String key = "...";
String secret = "...";

// Create private api using default connector
PrivateApi api = new PrivateApi(key, secret);
UserInfo info = api.getUserInfo();
System.out.println(info);
```

Next release goals:
* Logging support
