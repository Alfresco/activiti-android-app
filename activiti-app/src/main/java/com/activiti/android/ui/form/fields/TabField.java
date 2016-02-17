package com.activiti.android.ui.form.fields;

import java.lang.ref.WeakReference;

import android.support.design.widget.TabLayout;
import android.view.ViewGroup;

import com.activiti.client.api.model.editor.form.FormTabRepresentation;

/**
 * Created by jpascal on 17/02/2016.
 */
public class TabField
{
    protected WeakReference<ViewGroup> hookView;

    protected WeakReference<TabLayout.Tab> tab;

    protected String id;

    protected FormTabRepresentation data;

    protected int originalIndex;

    protected int currentIndex;

    public TabField(TabLayout.Tab tab, FormTabRepresentation data, int originalIndex, ViewGroup hookView)
    {
        this.tab = new WeakReference<>(tab);
        this.id = data.getId();
        this.data = data;
        this.originalIndex = originalIndex;
        this.hookView = new WeakReference<>(hookView);
    }

    public ViewGroup getHookView()
    {
        return hookView.get();
    }

    public TabLayout.Tab getTab()
    {
        return tab.get();
    }

    public String getId()
    {
        return id;
    }

    public FormTabRepresentation getData()
    {
        return data;
    }

    public int getOriginalIndex()
    {
        return originalIndex;
    }

    public int getCurrentIndex()
    {
        return currentIndex;
    }
}
