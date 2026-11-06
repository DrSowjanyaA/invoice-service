package com.bits.invoice;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class InvoiceCalculatorTest {

    InvoiceCalculator calc = new InvoiceCalculator();

    @Test
    public void discountApplied() {
        assertEquals(900.0, calc.netAmount(1000, 10), 0.001);
    }

    @Test
    public void gstAdded() {
        assertEquals(1062.0, calc.total(1000, 10, 18), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDiscount() {
        calc.netAmount(1000, 120);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeDiscount() {
        calc.netAmount(1000, -5);
    }
}
