[![Build Status](https://travis-ci.org/iisador/jBTCE.svg?branch=master)](https://travis-ci.org/iisador/jBTCE)

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
* Provides access to [fee](https://btc-e.com/api/2/btc_usd/fee) api
* Public v2 API: [ticker](https://btc-e.com/api/2/btc_usd/ticker), [trades](https://btc-e.com/api/2/btc_usd/trades), [depth](https://btc-e.com/api/2/btc_usd/depth)
* Private API
  - getInfo
  - OrderList (without attributes)
  - TransHistory (without attributes)
  - TradeHistory (without attributes)
  - CancelOrder
  - Trade

How-To
======
use Public api:
```java
// Create connector instance first. Used JavaConnector or implement your own
Connector connector = new JavaConnector();

PublicApi api = new PublicApi(connector);
Tick tick = api.getTick(BTC_USD);
System.out.println(tick);
```

or Private api:
```java
String key = "...";
String secret = "...";
Connector connector = new JavaConnector();

// Initialize connector with key and secret to setup mac
connector.init(key, secret);

PrivateApi api = new PrivateApi(connector);
UserInfo info = api.getUserInfo();
System.out.println(info);
```

Next release goals:
* Public V3 support
