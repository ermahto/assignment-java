package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/** Covers LegacyStoreManagerGateway file IO path (otherwise always mocked in Quarkus tests). */
class LegacyStoreManagerGatewayTest {

  @Test
  void createAndUpdateWriteTempFiles() {
    LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();
    Store store = new Store();
    store.name = "LegacyCov_" + System.currentTimeMillis();
    store.quantityProductsInStock = 2;

    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
    assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
  }
}