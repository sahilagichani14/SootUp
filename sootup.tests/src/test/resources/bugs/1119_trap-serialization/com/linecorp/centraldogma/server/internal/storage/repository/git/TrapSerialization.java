package com.linecorp.centraldogma.server.internal.storage.repository.git;

import java.util.*;
import java.io.*;

public class TrapSerialization {

    public Integer processWithExplicitCasting(String var2, String var4) throws Exception{
        Object var19;
        try {
            try {
                var19 = 10;
                throw new Exception();
            } catch (Exception e) {
                var19 = 20;
                throw new Exception(e);
            } finally {
                var19 = 30;
            }
        } catch (Exception ex) {
            var19 = 40;
        }
        return (Integer) var19;
    }

}