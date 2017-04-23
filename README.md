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
* Provides access to [fee](https://btc-e.com/api/2/btc_usd/fee) api
* Public v2 API: [ticker](https://btc-e.com/api/2/btc_usd/ticker), [trades](https://btc-e.com/api/2/btc_usd/trades), [depth](https://btc-e.com/api/2/btc_usd/depth)
* Private API
  - getInfo
  - OrderList (some attributes doesn't work on btc-e side)
  - TransHistory (some attributes doesn't work on btc-e side)
  - TradeHistory (some attributes doesn't work on btc-e side)
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
* Logging support
