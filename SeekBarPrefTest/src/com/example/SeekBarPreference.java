package com.example;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.seekbarpreftest.R;

public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {
    
    private static final String ANDROIDNS="http://schemas.android.com/apk/res/android";
    private static final String MYNS="myns";
    private static final int DEFAULT_VALUE = 50;
    
    private int mMaxValue = 100;
    private int mMinValue = 0;
    private int mCurrentValue;
    private String mUnitsText = "";

    private SeekBar mSeekBar;
    private TextView mStatusText;
    private TextView mUnitsRight;

    public SeekBarPreference(Context context) {
        super(context);
    }
    
    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPreference(context, attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPreference(context, attrs);
    }

    private void initPreference(Context context, AttributeSet attrs) {
        setLayoutResource(R.layout.pref_seekbar);
        setValuesFromXml(attrs);
    }
    
    private void setValuesFromXml(AttributeSet attrs) {
        mMaxValue = attrs.getAttributeIntValue(ANDROIDNS, "max", 100);
        mMinValue = attrs.getAttributeIntValue(MYNS, "min", 0);
        mUnitsText = getAttributeStringValue(attrs, MYNS, "unitsRight", "");
    }
    
    private String getAttributeStringValue(AttributeSet attrs, String namespace, String name, String defaultValue) {
        String value = attrs.getAttributeValue(namespace, name);
        if(value == null)
            value = defaultValue;
        
        return value;
    }
    
    @Override
    public void onBindView(View view) {
        super.onBindView(view);

        mSeekBar = (SeekBar)view.findViewById(R.id.seekbar);
        mStatusText = (TextView)view.findViewById(R.id.seekbar_value);
        mUnitsRight = (TextView)view.findViewById(R.id.seekbar_unit);

        mSeekBar.setMax(100);
        mSeekBar.setProgress(mCurrentValue + mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
        
        mStatusText.setText(String.valueOf(mCurrentValue));
        mStatusText.setMinimumWidth(30);
        mUnitsRight.setText(mUnitsText);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int newValue = progress + mMinValue;
        
        if(newValue > mMaxValue) {
            newValue = mMaxValue;
        } else if(newValue < mMinValue) {
            newValue = mMinValue;
        }
        
        // change rejected, revert to the previous value
        if(!callChangeListener(newValue)){
            seekBar.setProgress(mCurrentValue); 
            return; 
        }
        
        // change accepted, store it
        mCurrentValue = newValue;

        if(mStatusText!=null) {
            mStatusText.setText(String.valueOf(mCurrentValue));
        }
        
        persistInt(newValue);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        notifyChanged();
    }

    @Override 
    protected Object onGetDefaultValue(TypedArray ta, int index){
        int defaultValue = ta.getInt(index, DEFAULT_VALUE);
        return defaultValue;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if(restoreValue) {
            mCurrentValue = getPersistedInt(mCurrentValue);
        } else {
            int temp = 0;
            try {
                temp = (Integer)defaultValue;
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            
            persistInt(temp);
            mCurrentValue = temp;
        }
    }
}

