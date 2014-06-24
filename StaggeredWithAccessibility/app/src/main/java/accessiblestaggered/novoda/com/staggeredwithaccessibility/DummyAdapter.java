package accessiblestaggered.novoda.com.staggeredwithaccessibility;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DummyAdapter extends ArrayAdapter<Integer> {

    private static final Integer[] COLORS = {
            Color.BLUE,
            Color.BLACK,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.BLUE,
            Color.DKGRAY,
            Color.GREEN,
            Color.GRAY,
            Color.WHITE,
            Color.LTGRAY,
            Color.BLUE,
            Color.BLACK,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.BLUE,
            Color.DKGRAY,
    };

    private final LayoutInflater inflater;

    public DummyAdapter(Context context, LayoutInflater inflater) {
        super(context, 0, COLORS);
        this.inflater = inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_item, null);
            convertView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
        }

        TextView textView = (TextView) convertView.findViewById(R.id.positionView);
        textView.setText(String.valueOf(position));

        View colorView = convertView.findViewById(R.id.colorView);
        colorView.setBackgroundColor(COLORS[position]);

        return convertView;
    }
}
