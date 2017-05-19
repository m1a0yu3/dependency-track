/*
 * This file is part of Dependency-Track.
 *
 * Dependency-Track is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Dependency-Track is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Dependency-Track. If not, see http://www.gnu.org/licenses/.
 */
package org.owasp.dependencytrack.search;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.owasp.dependencytrack.model.Component;
import java.io.IOException;

public final class ComponentIndexer extends IndexManager implements ObjectIndexer<Component> {

    private static final Logger LOGGER = Logger.getLogger(ComponentIndexer.class);

    protected ComponentIndexer() {
        super(IndexType.COMPONENT);
    }

    @Override
    public String[] getSearchFields() {
        return IndexConstants.COMPONENT_SEARCH_FIELDS;
    }

    /**
     * Adds a Component object to a Lucene index.
     *
     * @param component A persisted Component object.
     */
    public void add(Component component) {
        final Document doc = new Document();
        addField(doc, IndexConstants.COMPONENT_UUID, component.getUuid(), Field.Store.YES, false);
        addField(doc, IndexConstants.COMPONENT_NAME, component.getName(), Field.Store.YES, true);
        addField(doc, IndexConstants.COMPONENT_GROUP, component.getGroup(), Field.Store.YES, true);
        addField(doc, IndexConstants.COMPONENT_VERSION, component.getVersion(), Field.Store.YES, false);
        addField(doc, IndexConstants.COMPONENT_SHA1, component.getSha1(), Field.Store.YES, true);
        addField(doc, IndexConstants.COMPONENT_DESCRIPTION, component.getDescription(), Field.Store.YES, true);

        try {
            getIndexWriter().addDocument(doc);
            getIndexWriter().commit();
            close();
        } catch (IOException e) {
            LOGGER.error("Error adding object to index");
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Updates a Component object in the Lucene index.
     *
     * @param component A persisted Component object.
     */
    public synchronized void update(Component component) {
        final Document doc = getDocument(IndexConstants.COMPONENT_UUID, component.getUuid());
        if (doc == null) {
            LOGGER.warn("Could not find object in index. Adding.");
            add(component);
            return;
        }

        updateField(doc, IndexConstants.COMPONENT_UUID, component.getUuid());
        updateField(doc, IndexConstants.COMPONENT_NAME, component.getName());
        updateField(doc, IndexConstants.COMPONENT_GROUP, component.getGroup());
        updateField(doc, IndexConstants.COMPONENT_VERSION, component.getVersion());
        updateField(doc, IndexConstants.COMPONENT_SHA1, component.getSha1());
        updateField(doc, IndexConstants.COMPONENT_DESCRIPTION, component.getDescription());

        try {
            getIndexWriter().updateDocument(new Term(IndexConstants.COMPONENT_UUID, component.getUuid()), doc);
            getIndexWriter().commit();
            close();
        } catch (IOException e) {
            LOGGER.error("Error updating object in index");
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Deletes a Component object from the Lucene index.
     *
     * @param component A persisted Component object.
     */
    public synchronized void remove(Component component) {
        try {
            getIndexWriter().deleteDocuments(new Term(IndexConstants.COMPONENT_UUID, component.getUuid()));
            getIndexWriter().commit();
            close();
        } catch (IOException e) {
            LOGGER.error("Error removing object from index");
            LOGGER.error(e.getMessage());
        }
    }

}
