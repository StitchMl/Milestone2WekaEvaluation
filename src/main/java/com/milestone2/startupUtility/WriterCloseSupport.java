package com.milestone2.startupUtility;

import java.io.IOException;

/**
 * Shared close-accumulation helper for output-bundle classes.
 *
 * <p>{@link OutputWriters} and
 * {@link com.milestone2.whatif.WhatIfOutputs} both manage multiple
 * {@link AutoCloseable} resources and must propagate every close failure
 * rather than swallowing them. This utility centralises that logic so the
 * two classes do not duplicate it.
 */
public final class WriterCloseSupport {

 private WriterCloseSupport() {
 }

 /**
 * Closes one resource while accumulating failures for deferred propagation.
 *
 * <p>If {@code closeable} is {@code null} the method is a no-op.
 * If closing throws an {@link IOException} it is returned directly
 * (or added as a suppressed exception when {@code failure} is already
 * set). Any other exception type is wrapped in a new {@link IOException}
 * using {@code message} as the detail.
 *
 * @param closeable resource to close, possibly {@code null}
 * @param failure previously captured failure, if any
 * @param message context message used when wrapping a non-IOException
 * @return updated failure accumulator
 */
 public static IOException closeQuietly(AutoCloseable closeable,
 IOException failure,
 String message) {
 if (closeable == null) {
 return failure;
 }
 try {
 closeable.close();
 return failure;
 } catch (Exception exception) {
 if (failure == null && exception instanceof IOException) {
 return (IOException) exception;
 }
 if (failure == null) {
 return new IOException(message, exception);
 }
 failure.addSuppressed(exception);
 return failure;
 }
 }
}
