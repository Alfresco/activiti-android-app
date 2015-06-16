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
public class ConditionRepresentation
{

    // Condition
    private String leftFormFieldId;

    private String operator;

    private Object rightValue;

    private String rightType;

    private String rightFormFieldId;

    // Next condition
    private String nextConditionOperator;

    private ConditionRepresentation nextCondition;

    public String getLeftFormFieldId()
    {
        return leftFormFieldId;
    }

    public ConditionRepresentation setLeftFormFieldId(String leftFormFieldId)
    {
        this.leftFormFieldId = leftFormFieldId;
        return this;
    }

    public String getOperator()
    {
        return operator;
    }

    public ConditionRepresentation setOperator(String operator)
    {
        this.operator = operator;
        return this;
    }

    public Object getRightValue()
    {
        return rightValue;
    }

    public ConditionRepresentation setRightValue(Object value)
    {
        this.rightValue = value;
        return this;
    }

    public String getRightType()
    {
        return rightType;
    }

    public ConditionRepresentation setRightType(String rightType)
    {
        this.rightType = rightType;
        return this;
    }

    public String getRightFormFieldId()
    {
        return rightFormFieldId;
    }

    public ConditionRepresentation setRightFormFieldId(String rightFormFieldId)
    {
        this.rightFormFieldId = rightFormFieldId;
        return this;
    }

    public String getNextConditionOperator()
    {
        return nextConditionOperator;
    }

    public ConditionRepresentation getNextCondition()
    {
        return nextCondition;
    }

    public ConditionRepresentation setNextCondition(String operator, ConditionRepresentation nextCondition)
    {
        this.nextConditionOperator = operator;
        this.nextCondition = nextCondition;
        return this;
    }

}
