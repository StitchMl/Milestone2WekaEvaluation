package com.milestone2;

import com.milestone2.startupUtility.Application;

/**
 * Application entry point.
 */
public class MainApp {
 /**
 * Delegates startup to the analysis application.
 *
 * @param args CLI arguments in {@code --key=value} form
 */
 public static void main(String[] args) {
 new Application().run(args);
 }
}

