/*
 * Copyright (C) 2011 John Currier
 * Copyright (C) 2017 Daniel Watt
 * Copyright (C) 2018 Nils Petzaell
 *
 * This file is a part of the SchemaSpy project (http://schemaspy.org).
 *
 * SchemaSpy is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * SchemaSpy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.schemaspy.view;

import org.schemaspy.model.Routine;
import org.schemaspy.util.Markdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * The page that lists all of the routines (stored procedures and functions)
 * in the schema.
 *
 * @author John Currier
 * @author Daniel Watt
 * @author Nils Petzaell
 */
public class HtmlRoutinesPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MustacheCompiler mustacheCompiler;

    public HtmlRoutinesPage(MustacheCompiler mustacheCompiler) {
        this.mustacheCompiler = mustacheCompiler;
    }

    public void write(Collection<Routine> routines, Map<String, Object> parametersMap, Writer writer) {

        PageData pageData = new PageData.Builder()
                .templateName("routines.html")
                .scriptName("routines.js")
                .addAllToScope(parametersMap)
                .addToScope("routines", routines)
                .addToScope("md2html", (Function<String,String>) md -> Markdown.toHtml(md, mustacheCompiler.getRootPath(0)))
                .getPageData();

        try {
            mustacheCompiler.write(pageData, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to write routines page", e);
        }
    }

}
