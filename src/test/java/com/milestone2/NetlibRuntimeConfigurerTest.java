package com.milestone2;

import com.milestone2.analysis.NetlibRuntimeConfigurer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetlibRuntimeConfigurerTest {
    @Test
    void configurePureJavaPinsAllNetlibImplementationsToF2j() {
        NetlibRuntimeConfigurer.configurePureJava();

        assertEquals("com.github.fommil.netlib.F2jBLAS", System.getProperty("com.github.fommil.netlib.BLAS"));
        assertEquals("com.github.fommil.netlib.F2jLAPACK", System.getProperty("com.github.fommil.netlib.LAPACK"));
        assertEquals("com.github.fommil.netlib.F2jARPACK", System.getProperty("com.github.fommil.netlib.ARPACK"));
    }
}
