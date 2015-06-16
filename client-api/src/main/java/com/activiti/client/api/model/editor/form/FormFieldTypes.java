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

package com.activiti.client.api.model.editor.form;

/**
 * @author Joram Barrez
 */
public interface FormFieldTypes
{

    String SINGLE_LINE_TEXT = "text";

    String MULTI_LINE_TEXT = "multi-line-text";

    String INTEGER = "integer";

    String DATE = "date";

    String BOOLEAN = "boolean";

    String RADIO_BUTTONS = "radio-buttons";

    String DROPDOWN = "dropdown";

    String TYPEAHEAD = "typeahead";

    String UPLOAD = "upload";

    String GROUP = "group";

    String READONLY = "readonly";

    String READONLY_TEXT = "readonly-text";

    String PEOPLE = "people";

    String FUNCTIONAL_GROUP = "functional-group";

    String DYNAMIC_TABLE = "dynamic-table";

    String HYPERLINK = "hyperlink";

    String CONTAINER = "container";

    String AMOUNT = "amount";
}
