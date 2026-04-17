package com.milestone2.analysis;

/**
 * Forces netlib-java to use the pure Java F2j implementations.
 */
public final class NetlibRuntimeConfigurer {
    private static final String BLAS_PROPERTY = "com.github.fommil.netlib.BLAS";
    private static final String LAPACK_PROPERTY = "com.github.fommil.netlib.LAPACK";
    private static final String ARPACK_PROPERTY = "com.github.fommil.netlib.ARPACK";

    private static final String F2J_BLAS = "com.github.fommil.netlib.F2jBLAS";
    private static final String F2J_LAPACK = "com.github.fommil.netlib.F2jLAPACK";
    private static final String F2J_ARPACK = "com.github.fommil.netlib.F2jARPACK";

    private NetlibRuntimeConfigurer() {
    }

    /**
     * Pins BLAS, LAPACK and ARPACK resolution to the pure-Java F2J implementations.
     */
    public static void configurePureJava() {
        System.setProperty(BLAS_PROPERTY, F2J_BLAS);
        System.setProperty(LAPACK_PROPERTY, F2J_LAPACK);
        System.setProperty(ARPACK_PROPERTY, F2J_ARPACK);
    }
}
