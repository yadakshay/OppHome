package com.openhome.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.openhome.R;
import com.openhome.model.Hazards;
import com.openhome.model.SelectedHazards;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SecondLevelAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final List<String> mListDataHeader;
    private final Map<String, List<String>> mListDataChild;
    private List<Hazards> selectedHazardsList;

    public SecondLevelAdapter(Context mContext, List<String> mListDataHeader, Map<String, List<String>> mListDataChild, List<Hazards> selectedHazardsList) {
        this.mContext = mContext;
        this.mListDataHeader = mListDataHeader;
        this.mListDataChild = mListDataChild;
        this.selectedHazardsList = selectedHazardsList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parentViewGrp) {
        final String childText = (String) getChild(groupPosition, childPosition);

        LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.drawer_list_item, parentViewGrp, false);

        CheckBox chk = (CheckBox) convertView.findViewById(R.id.lblListCheckBox);

        String child = (String) getChild(groupPosition, childPosition);
        String parent = (String) getGroup(groupPosition);
        final Hazards hazards = getHazardInView(child, parent);
        if (hazards != null) {
            if (hazards.getDisplay() != 1)
                chk.setChecked(true);
            else
                chk.setChecked(false);
        }
        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (hazards != null)
                    hazards.setDisplay(isChecked ? 0 : 1);
            }
        });

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        txtListChild.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_group_second, parent, false);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        lblListHeader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        //lblListHeader.setTextColor(Color.YELLOW);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private Hazards getHazardInView(String child, String parent) {
        Hazards hazards = null;
        for (Hazards h : selectedHazardsList) {
            Log.d("SSSSSSSSSSSS", "-" + child + "-" + h.getHazardDescription() + "-" + parent + "-" + h.getHazardScenario());
            Log.d("sssss", "" + h.getHazardScenario().equalsIgnoreCase(parent));
            Log.d("sssss", "" + h.getHazardDescription().equalsIgnoreCase(child));
            if (h.getHazardScenario().equalsIgnoreCase(parent) && h.getHazardDescription().equalsIgnoreCase(child)) {
                hazards = h;
                break;
            }
        }
        return hazards;
    }
}
