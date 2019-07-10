package com.paazl.exception;

import static com.paazl.common.Constants.ERROR_MESSAGE_ORDER_AMOUNT;

public class OrderAmountException extends Exception {
    public OrderAmountException() {
        super(ERROR_MESSAGE_ORDER_AMOUNT);
    }
}