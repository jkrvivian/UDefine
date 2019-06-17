package com.example.udefine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.udefine.Database.Layouts;
import com.example.udefine.Database.Notes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class widgetManager {
    private LinearLayout parentLinearLayout;
    private Context mainContext;
    private FragmentManager fragmentManager;
    private int margin;

    /* A hash map record View id and title */
    private HashMap<Integer, String> title_id_list;

    /*
     *  widget type:
     *  1. editText (single line)
     *  2. date/time picker
     *  3. tag
     *  4. plainText (multiple line)
     */

    public widgetManager(Context context, LinearLayout parent,
                           FragmentManager fragment) {
        mainContext = context;
        parentLinearLayout = parent;
        fragmentManager = fragment;
        title_id_list = new HashMap<Integer, String>();
        margin = Math.round(16 * mainContext.getResources()
                .getDisplayMetrics().density);
    }

    // widget.generate for one element
    public void generate(int widget_type, String title) {
        switch (widget_type) {
            case 1:
                /* Add editText */
                this.addEditText(title);
                break;
            case 2:
                /* Add DateTime */
                this.addDateTime(title);
                break;
            case 3:
                /* Add Tag */
                this.addTag(title);
                break;
            case 4:
                /* Add PlainText */
                this.addPlainText(title);
                break;
        }
    }

    // widget.generate for multiple element
    public void generate(int componentList[], String[] title) {

        // make sure one element with one title name
        if (componentList.length != title.length)
            return;

        for (int i = 0; i < componentList.length; ++i) {
            switch (componentList[i]) {
                case 1:
                    /* Add editText */
                    this.addEditText(title[i]);
                    break;
                case 2:
                    /* Add DateTime */
                    this.addDateTime(title[i]);
                    break;
                case 3:
                    /* Add Tag */
                    this.addTag(title[i]);
                    break;
                case 4:
                    /* Add PlainText */
                    this.addPlainText(title[i]);
                    break;
            }
        }
    }

    // widget.generate for multiple element with Arraylist<String> type
    public void generate(ArrayList<Integer> componentList, ArrayList<String> title) {

        // make sure one element with one title name
        if (componentList.size() != title.size())
            return;

        for (int i = 0; i < componentList.size(); ++i) {
            switch (componentList.get(i)) {
                case 1:
                    /* Add editText */
                    this.addEditText(title.get(i));
                    break;
                case 2:
                    /* Add DateTime */
                    this.addDateTime(title.get(i));
                    break;
                case 3:
                    /* Add Tag */
                    this.addTag(title.get(i));
                    break;
                case 4:
                    /* Add PlainText */
                    this.addPlainText(title.get(i));
                    break;
            }
        }
    }

    public void removeAllHashmap() { title_id_list.clear(); }
    public void removeLastHashmap() {
        title_id_list.remove(title_id_list.size() - 1);
    }

    public void fillContentToWidget(Notes[] notes) {
        int id, counter = 0;
        Iterator<HashMap.Entry<Integer, String>> iterator =
                title_id_list.entrySet().iterator();

        while (iterator.hasNext() && counter <= notes.length) {
            // get Title and View
            HashMap.Entry<Integer, String> entry = iterator.next();
            id = entry.getKey();
            String title = notes[counter].getTitle();
            String content = notes[counter].getContent();
            View v = parentLinearLayout.findViewById(id);

            // check if the title are same
            if (entry.getValue().equals(title)) {
                if (v instanceof EditText) {
                    // set Title
                    EditText e = (EditText) v;
                    e.setText(content);
                } else if (v instanceof Button) {
                    Button b = (Button) v;
                    if (content != null) {
                        String dateTime[] = content.split(",");
                        // set date
                        b.setText(dateTime[0]);

                        // set time button
                        entry = iterator.next();
                        b = (Button) parentLinearLayout.findViewById(entry.getKey());
                        b.setText(dateTime[1]);
                    } else {
                        b.setText("Date");
                        // set time button
                        entry = iterator.next();
                        b = (Button) parentLinearLayout.findViewById(entry.getKey());
                        b.setText("Time");
                    }
                } else if (v instanceof Spinner) {
                    Spinner s = (Spinner) v;
                    tagListAdapter adapter = (tagListAdapter) s.getAdapter();
                    ArrayList<tagItemStateVO> listState = adapter.getItems();

                    // parse colors
                    String color[] = content.split(",");
                    int color_index = 0;
                    for (int i = 0; i < listState.size() && color_index < color.length; ++i) {
                        tagItemStateVO tmp = listState.get(i);
                        if (tmp.getTitle().equals(color[color_index])) {
                            Log.d("tag test", color[color_index]);
                            listState.get(i).setSelected(true);
                            ++color_index;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                ++counter;
            }
        }
    }

    public ArrayList<String> getNoteTitleTimeTag() {
        int id, counter = 0;
        String content = "";
        ArrayList<String> titleTimeTag = new ArrayList<String>();
        Iterator<HashMap.Entry<Integer, String>> iterator =
                title_id_list.entrySet().iterator();

        while (iterator.hasNext() && counter <= 2) {
            // get Title and View
            HashMap.Entry<Integer, String> entry = iterator.next();
            id = entry.getKey();
            View v = parentLinearLayout.findViewById(id);

            if (v instanceof EditText) {
                // get Title
                EditText e = (EditText) v;
                content = e.getText().toString();
                if (content.length() == 0) {
                    content = null;
                }
            } else if (v instanceof Button) {
                // get date + time
                Button b = (Button) v;
                String tmp = b.getText().toString();

                // if Date and Time is not set, set content to null
                if (tmp.equals("Date")) {
                    continue;
                } else if (tmp.equals("Time")) {
                    content = null;
                } else {
                    content = content + tmp;
                    // date string, continue to get time
                    if (tmp.contains("/")) {
                        content = content + ",";
                        continue;
                    }
                }
                //Log.d("widget", title + ":" + b.getText().toString());
            } else if (v instanceof Spinner) {
                Spinner s = (Spinner) v;
                tagListAdapter adapter = (tagListAdapter) s.getAdapter();
                ArrayList<tagItemStateVO> listState = adapter.getSelectedItems();

                if (listState.size() == 0) {
                    content = null;
                } else {
                    for (int i = 0; i < listState.size(); ++i) {
                        tagItemStateVO tmp = listState.get(i);
                        if (i != 0) {
                            content = content + ',';
                        }
                        content = content + tmp.getTitle();
                    }
                }
            }
            titleTimeTag.add(content);
            content = "";
            ++counter;
        }
        return titleTimeTag;
    }

    public ArrayList<Notes> getNoteContent(int noteID) {
        int id;
        String title, content = "";
        ArrayList<Notes> notes = new ArrayList<Notes>();
        Iterator<HashMap.Entry<Integer, String>> iterator =
                title_id_list.entrySet().iterator();

        while (iterator.hasNext()) {
            // get Title and View
            HashMap.Entry<Integer, String> entry = iterator.next();
            id = entry.getKey();
            title = entry.getValue();
            View v = parentLinearLayout.findViewById(id);

            if (v instanceof EditText) {
                EditText e = (EditText)v;
                content = e.getText().toString();
                if (content.length() == 0) {
                    content = null;
                }
                //Log.d("widget", title + ":" + e.getText().toString());
            } else if (v instanceof Button) {
                Button b = (Button)v;
                String tmp = b.getText().toString();

                // if Date and Time is not set, set content to null
                if (tmp.equals("Date")) {
                    continue;
                } else if (tmp.equals("Time")) {
                    content = null;
                } else {
                    content = content + tmp;
                    // date string, continue to get time
                    if (tmp.contains("/")) {
                        content = content + ",";
                        continue;
                    }
                }
                //Log.d("widget", title + ":" + b.getText().toString());
            } else if (v instanceof Spinner) {
                Spinner s = (Spinner)v;
                tagListAdapter adapter = (tagListAdapter)s.getAdapter();
                ArrayList<tagItemStateVO> listState = adapter.getSelectedItems();

                if (listState.size() == 0) {
                    content = null;
                } else {
                    for (int i = 0; i < listState.size(); ++i) {
                        tagItemStateVO tmp = listState.get(i);
                        if (i != 0) {
                            content = content + ',';
                        }
                        content = content + tmp.getTitle();
                    }
                }
            }

            // Add Notes object to list
            Log.d("widget", title + " " + content);
            Notes n = new Notes(noteID, title, content);
            notes.add(n);
            content = "";
        }
        return notes;
    }

    // get Layout content by Layout ID
    public ArrayList<Layouts> getLayoutContent(int LayoutID) {

        int id, format = 0;
        String title;
        ArrayList<Layouts> layouts = new ArrayList<>();
        Iterator<HashMap.Entry<Integer, String>> iterator =
                title_id_list.entrySet().iterator();
        boolean time_button_repeat = false;

        while (iterator.hasNext()) {
            // get Title and View
            HashMap.Entry<Integer, String> entry = iterator.next();
            id = entry.getKey();
            title = entry.getValue();
            View v = parentLinearLayout.findViewById(id);

            if (v instanceof EditText) {
                EditText e = (EditText)v;

                if((e.getInputType() & InputType.TYPE_TEXT_FLAG_MULTI_LINE) > 0) {
                    // multiple line textview
                    format = 4;
                } else {
                    // single line textview
                    format = 1;
                }
            } else if (v instanceof Button) {
                // date/time picker has two button, this may cause repeat count.
                if (!time_button_repeat) {
                    format = 2;
                    time_button_repeat = !time_button_repeat;
                } else {
                    time_button_repeat = !time_button_repeat;
                    continue;
                }
            } else if (v instanceof Spinner) {
                // tag
                format = 3;
            } else {
                Log.w("widgetManager", "Something wrong in getLayoutContent()",null);
            }

            // Add Layouts object to list
            Layouts n = new Layouts(LayoutID, title, format);
            layouts.add(n);
        }
        return layouts;
    }

    public void addEditText(String title) {
        LinearLayout childLayout = new LinearLayout(mainContext);
        childLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, margin, 0, margin);

        /* element layout setting */
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        /* Title TextView */
        TextView titleTextView = new TextView(mainContext);
        titleTextView.setTextSize(24);
        titleTextView.setText(title);
        titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);

        /* EditText */
        EditText editText = new EditText(mainContext);
        editText.setTextSize(18);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setId(View.generateViewId());

        /* Add element to child layout */
        childLayout.addView(titleTextView, titleLayoutParams);
        childLayout.addView(editText, titleLayoutParams);

        /* Add child layout to parent layout */
        parentLinearLayout.addView(childLayout, layoutParams);

        /* Update hashmap list */
        title_id_list.put(editText.getId(), title);
    }

    public void addDateTime(String title) {
        LinearLayout childLayout = new LinearLayout(mainContext);
        childLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, margin, 0, margin);
        childLayout.setLayoutParams(layoutParams);

        /* Time Date button layout */
        LinearLayout grandChildLayout = new LinearLayout(mainContext);
        grandChildLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams grandLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        grandChildLayout.setLayoutParams(grandLayoutParams);

        /* Title Layout setting */
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        /* Title TextView */
        TextView titleTextView = new TextView(mainContext);
        titleTextView.setTextSize(24);
        titleTextView.setText(title);
        titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);

        /* buttons layout setting */
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.weight = 1;

        /* Date button */
        Button dateButton = new Button(mainContext);
        dateButton.setText(R.string.date_button);
        dateButton.setId(View.generateViewId());
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment(view);
                newFragment.show(fragmentManager,"datePicker");
            }
        });

        /* Time button */
        Button timeButton = new Button(mainContext);
        timeButton.setText(R.string.time_button);
        timeButton.setId(View.generateViewId());
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment(view);
                newFragment.show(fragmentManager,"timePicker");
            }
        });

        /* Add element to child layout */
        childLayout.addView(titleTextView, titleLayoutParams);
        grandChildLayout.addView(dateButton, buttonLayoutParams);
        grandChildLayout.addView(timeButton, buttonLayoutParams);
        childLayout.addView(grandChildLayout);

        /* Add child layout to parent layout */
        parentLinearLayout.addView(childLayout);

        /* Update hashmap list */
        title_id_list.put(dateButton.getId(), title);
        title_id_list.put(timeButton.getId(), title);
    }

    public void addTag(String title) {
        LinearLayout childLayout = new LinearLayout(mainContext);
        childLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, margin, 0, margin);
        childLayout.setLayoutParams(layoutParams);

        /* Title Layout setting */
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        /* Title TextView */
        TextView titleTextView = new TextView(mainContext);
        titleTextView.setTextSize(24);
        titleTextView.setText(title);
        titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);

        /* spinner layout setting */
        LinearLayout.LayoutParams spinnerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        /* spinner */
        Spinner tagSpinner = new Spinner(mainContext, Spinner.MODE_DROPDOWN);
        tagSpinner.setId(View.generateViewId());
        final String[] tag_colors = mainContext.getResources()
                .getStringArray(R.array.tag_color_spinner);

        ArrayList<tagItemStateVO> listVOs = new ArrayList<>();

        for (int i = 0; i < tag_colors.length; i++) {
            tagItemStateVO stateVO = new tagItemStateVO();
            stateVO.setTitle(tag_colors[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }
        tagListAdapter myAdapter = new tagListAdapter(mainContext, 0,
                listVOs);
        tagSpinner.setAdapter(myAdapter);

        /* Add element to child layout */
        childLayout.addView(titleTextView, titleLayoutParams);
        childLayout.addView(tagSpinner, spinnerLayoutParams);

        /* Add child layout to parent layout */
        parentLinearLayout.addView(childLayout);

        /* Update hashmap list */
        title_id_list.put(tagSpinner.getId(), title);
    }

    public void addPlainText(String title) {

        LinearLayout childLayout = new LinearLayout(mainContext);
        childLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, margin, 0, margin);

        /* element layout setting */
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        /* Title TextView */
        TextView titleTextView = new TextView(mainContext);
        titleTextView.setTextSize(24);
        titleTextView.setText(title);
        titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);

        /* EditText */
        EditText editText = new EditText(mainContext);
        editText.setTextSize(18);
        editText.setInputType(InputType.TYPE_CLASS_TEXT |
                              InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setId(View.generateViewId());

        /* Add element to child layout */
        childLayout.addView(titleTextView, titleLayoutParams);
        childLayout.addView(editText, titleLayoutParams);

        /* Add child layout to parent layout */
        parentLinearLayout.addView(childLayout, layoutParams);

        /* Update hashmap list */
        title_id_list.put(editText.getId(), title);
    }
}
