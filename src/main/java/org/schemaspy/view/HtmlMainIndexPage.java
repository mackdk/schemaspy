/*
 * Copyright (C) 2004 - 2011 John Currier
 * Copyright (C) 2016, 2017 Rafal Kasa
 * Copyright (C) 2016, 2017 Ismail Simsek
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

import org.schemaspy.Config;
import org.schemaspy.DbAnalyzer;
import org.schemaspy.model.Database;
import org.schemaspy.model.ForeignKeyConstraint;
import org.schemaspy.model.Table;
import org.schemaspy.util.Markdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * The main index that contains all tables and views that were evaluated
 *
 * @author John Currier
 * @author Rafal Kasa
 * @author Ismail Simsek
 * @author Daniel Watt
 * @author Nils Petzaell
 */
public class HtmlMainIndexPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MustacheCompiler mustacheCompiler;
    private final String description;

    public HtmlMainIndexPage(MustacheCompiler mustacheCompiler, String description) {
        this.mustacheCompiler = mustacheCompiler;
        this.description = description;
    }

    public void write(Collection<Table> tables, Map<String, Object> parametersMap, Writer writer) {
        List<MustacheTable> mustacheTables = new ArrayList<>();

        long columnsAmount = 0;

        for(Table table: tables) {
            columnsAmount += table.getColumns().size();
            String comments = Markdown.toHtml(table.getComments(), "");
            MustacheTable mustacheTable = new MustacheTable(table, "");
            mustacheTable.setComments(comments);
            mustacheTables.add(mustacheTable);
        }

        PageData pageData = new PageData.Builder()
                .templateName("main.html")
                .scriptName("main.js")
                .addAllToScope(parametersMap)
                .addToScope("tables", mustacheTables)
                .addToScope("description", Markdown.toHtml(description, ""))
                .depth(0)
                .getPageData();

        try {
            mustacheCompiler.write(pageData, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to write main index page", e);
        }
    }

}
