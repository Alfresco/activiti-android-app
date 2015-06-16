/*
 *  Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco Activiti Mobile for Android.
 *
 * Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.activiti.android.platform.integration.alfresco;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jpascal on 26/03/2015.
 */
public class NodeRefUtils
{
    public static final int IDENTIFIER_LENGTH = 36;

    public static final String URI_FILLER = "://";

    private static final Pattern NODEREF_PATTERN = Pattern.compile(".+://.+/.+");

    private static final Pattern IDENTIFIER_PATTERN = Pattern
            .compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");

    private static final Pattern IDENTIFIER_VERSION_PATTERN = Pattern
            .compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12};.+");

    public static final String PROTOCOL_WORKSPACE = "workspace";

    public static final String IDENTIFIER_SPACESSTORE = "SpacesStore";

    public static final String STORE_REF_WORKSPACE_SPACESSTORE = PROTOCOL_WORKSPACE + URI_FILLER
            + IDENTIFIER_SPACESSTORE;

    private NodeRefUtils()
    {

    }

    /**
     * Determine if passed string conforms to the pattern of a node reference
     *
     * @param nodeRef the node reference as a string
     * @return true => it matches the pattern of a node reference
     */
    public static boolean isNodeRef(String nodeRef)
    {
        Matcher matcher = NODEREF_PATTERN.matcher(nodeRef);
        return matcher.matches();
    }

    public static String getStoreRef(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int lastForwardSlash = nodeRef.lastIndexOf('/');
            return nodeRef.substring(0, lastForwardSlash);
        }
        return null;
    }

    public static String getStoreProtocol(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int dividerPatternPosition = nodeRef.indexOf(URI_FILLER);
            return nodeRef.substring(0, dividerPatternPosition);
        }
        return null;
    }

    public static String getStoreIdentifier(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int dividerPatternPosition = nodeRef.indexOf(URI_FILLER);
            int lastForwardSlash = nodeRef.lastIndexOf('/');
            return nodeRef.substring(dividerPatternPosition + 3, lastForwardSlash);
        }
        return null;
    }

    /**
     * Returns the identifier of a nodeRef(extract the version number if added
     * by cmis)
     *
     * @param nodeRef
     * @return
     */
    public static String getNodeIdentifier(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int lastForwardSlash = nodeRef.lastIndexOf('/');
            int versionNumber = (nodeRef.lastIndexOf(';') == -1) ? nodeRef.length() : nodeRef.lastIndexOf(';');
            return nodeRef.substring(lastForwardSlash + 1, versionNumber);
        }
        else if (isIdentifier(nodeRef) || isVersionIdentifier(nodeRef)) { return nodeRef; }
        return null;
    }

    public static String createNodeRefByIdentifier(String identifier)
    {
        return STORE_REF_WORKSPACE_SPACESSTORE + "/" + identifier;
    }

    public static String getCleanIdentifier(String nodeRef)
    {
        int versionNumber = (nodeRef.lastIndexOf(';') == -1) ? nodeRef.length() : nodeRef.lastIndexOf(';');
        return nodeRef.substring(0, versionNumber);
    }

    public static boolean isIdentifier(String id)
    {
        Matcher matcher = IDENTIFIER_PATTERN.matcher(id);
        return matcher.matches();
    }

    public static boolean isVersionIdentifier(String id)
    {
        Matcher matcher = IDENTIFIER_VERSION_PATTERN.matcher(id);
        return matcher.matches();
    }

    public static String getVersionIdentifier(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int lastForwardSlash = nodeRef.lastIndexOf('/');
            return nodeRef.substring(lastForwardSlash + 1, nodeRef.length());
        }
        else if (isVersionIdentifier(nodeRef))
        {
            return nodeRef;
        }
        else if (isIdentifier(nodeRef)) { return nodeRef; }
        return null;
    }
}
