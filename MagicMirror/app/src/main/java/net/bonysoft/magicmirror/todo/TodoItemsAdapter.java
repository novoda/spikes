package net.bonysoft.magicmirror.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.Arrays;
import java.util.List;

import net.bonysoft.magicmirror.R;

public class TodoItemsAdapter extends ArrayAdapter<CharSequence> {

    private final List<CharSequence> todoItems;

    public TodoItemsAdapter(Context context, int textViewResourceId, CharSequence[] todoItems) {
        super(context, textViewResourceId, todoItems);
        this.todoItems = Arrays.asList(todoItems);
    }

    private static class ViewHolder {
        CheckBox todoItemCheck;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.todo_list_item, parent, false);

            holder = new ViewHolder();
            holder.todoItemCheck = (CheckBox) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CharSequence item = todoItems.get(position);
        holder.todoItemCheck.setText(item);
        holder.todoItemCheck.setChecked(checkItemAtPosition(position));

        return convertView;
    }

    private boolean checkItemAtPosition(int position) {
        return position % 2 == 0;
    }

}
