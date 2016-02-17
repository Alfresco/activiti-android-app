/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.android.ui.form;

import java.util.Map;

import com.activiti.client.api.model.editor.form.ConditionOperators;
import com.activiti.client.api.model.editor.form.ConditionRepresentation;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.utils.StringUtils;

public class FormEvaluateExpression
{

    private Map<String, Object> values;

    private Map<String, FormFieldRepresentation> fieldMap;

    public FormEvaluateExpression(Map<String, Object> values, Map<String, FormFieldRepresentation> fieldMap)
    {
        this.values = values;
        this.fieldMap = fieldMap;
    }

    public boolean evaluateExpression(ConditionRepresentation conditionRepresentation)
    {
        Boolean previousCondition = null;
        String previousOperator = null;
        for (ConditionRepresentation currentRule : conditionRepresentation)
        {
            Boolean currentCondition = evaluateRule(currentRule);
            previousCondition = evaluateConditionOperator(currentCondition, previousCondition, previousOperator);
            previousOperator = currentRule.getNextConditionOperator();
        }
        return previousCondition;
    }

    private boolean evaluateRule(ConditionRepresentation conditionRepresentation)
    {
        String leftValue = conditionRepresentation.getLeftFieldValueToCompare(values, fieldMap);
        String operator = conditionRepresentation.getOperator();
        String rightValue = conditionRepresentation.getRigthValueToCompare(values, fieldMap);

        if (StringUtils.isNotEmpty(leftValue) && StringUtils.isNotEmpty(rightValue))
        {
            switch (operator)
            {
                case ConditionOperators.VALUE_EQUALS:
                    if (leftValue.equals(rightValue)) { return true; }
                    break;
                case ConditionOperators.VALUE_NOT_EQUALS:
                    if (!leftValue.equals(rightValue)) { return true; }
                    break;
                case ConditionOperators.VALUE_GREATER:
                    if (Double.valueOf(leftValue) > Double.valueOf(rightValue)) { return true; }
                    break;
                case ConditionOperators.VALUE_GREATER_THEN:
                    if (Double.valueOf(leftValue) >= Double.valueOf(rightValue)) { return true; }
                    break;
                case ConditionOperators.VALUE_LOWER:
                    if (Double.valueOf(leftValue) < Double.valueOf(rightValue)) { return true; }
                    break;
                case ConditionOperators.VALUE_LOWER_OR_EQUALS:
                    if (Double.valueOf(leftValue) <= Double.valueOf(rightValue)) { return true; }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private Boolean evaluateConditionOperator(Boolean conditionA, Boolean conditionB, String operator)
    {
        if (operator != null && conditionB != null)
        {
            if (operator.equals(ConditionOperators.OR)) { return conditionA || conditionB; }
            if (operator.equals(ConditionOperators.OR_NOT)) { return !(conditionA || conditionB); }
            if (operator.equals(ConditionOperators.AND)) { return conditionA && conditionB; }
            if (operator.equals(ConditionOperators.AND_NOT)) { return !(conditionA && conditionB); }
        }
        return conditionA;
    }
}
