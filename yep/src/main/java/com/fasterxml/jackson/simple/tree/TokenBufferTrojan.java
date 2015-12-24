package com.fasterxml.jackson.simple.tree;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;

/**
 * Created by mariotaku on 15/12/24.
 */
public class TokenBufferTrojan {

    public static JsonGenerator createTokenBuffer(final ObjectCodec codec, final boolean hasNativeIds) {
        return new TokenBuffer(codec, hasNativeIds);
    }

    public static JsonParser asParser(final JsonGenerator generator) {
        return ((TokenBuffer) generator).asParser();
    }
}
