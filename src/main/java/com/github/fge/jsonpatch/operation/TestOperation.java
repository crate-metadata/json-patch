/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonpatch.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.operation.PathValueOperation;
import com.google.common.base.Equivalence;

/**
 * JSON Patch {@code test} operation
 *
 * <p>The two arguments for this operation are the pointer containing the value
 * to test ({@code path}) and the value to test equality against ({@code
 * value}).</p>
 *
 * <p>It is an error if no value exists at the given path and there is an expected value to test equality against.</p>
 *
 * <p>Also note that equality as defined by JSON Patch is exactly the same as it
 * is defined by JSON Schema itself. As such, this operation reuses {@link
 * JsonNumEquals} for testing equality.</p>
 */
public final class TestOperation
    extends PathValueOperation
{
    public static final String OPERATION_NAME = "test";

    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNumEquals.getInstance();

    @JsonCreator
    public TestOperation(@JsonProperty("path") final JsonPointer path,
        @JsonProperty("value") final JsonNode value)
    {
        super(OPERATION_NAME, path, value);
    }

    @Override
    public JsonNode apply(final JsonNode node)
        throws JsonPatchException
    {
        final JsonNode tested = path.path(node);
        if (tested.isMissingNode() && !value.isNull())
            throw new JsonPatchException(BUNDLE.getMessage(
                "jsonPatch.noSuchPath"));
        if (!(tested.isMissingNode() && value.isNull()) && !EQUIVALENCE.equivalent(tested, value))
            throw new JsonPatchException(BUNDLE.getMessage(
                "jsonPatch.valueTestFailure"));
        return node.deepCopy();
    }
}
