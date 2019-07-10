package com.paazl.common;

public class Constants {
    public static final String ERROR_MESSAGE_SERVER_IS_UNAVAILABLE = "Server is unavailable";
    public static final String ERROR_MESSAGE_ORDER_AMOUNT = "Amount of sheeps must be greater then 0!";
    public static final String SUCCESS_MESSAGE_STATUS = "Balance: %d, number of sheep healthy and dead: [%d, %d]";

    public static final String BASE_URL = "http://localhost:8080/rest/shepherdmanager";
    public static final String BALANCE_ENDPOINT = "/balance";
    public static final String STATUS_ENDPOINT = "/status";
    public static final String ORDER_ENDPOINT = "/order";
}