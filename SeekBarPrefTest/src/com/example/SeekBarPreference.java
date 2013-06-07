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
	private int mInterval = 1;
	private boolean mLinear = true;
	private boolean mShowValue = true;
	private String mLeftText = "";
	private String mRightText = "";
	private int mCurrentValue;
	private String mUnitsRight = "";
	private SeekBar mSeekBar;
	
	private TextView mStatusText;

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
		
		mLinear = attrs.getAttributeBooleanValue(MYNS, "linear", true);
		mShowValue = attrs.getAttributeBooleanValue(MYNS, "showValue", true);
		
		mUnitsRight = getAttributeStringValue(attrs, MYNS, "unitsRight", "");
		
		try {
			String newInterval = attrs.getAttributeValue(MYNS, "interval");
			if(newInterval != null)
				mInterval = Integer.parseInt(newInterval);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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

        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(this);
		
        mSeekBar.setMax(100);
        mSeekBar.setProgress(valueToBar(mCurrentValue));
        
        mStatusText = (TextView)view.findViewById(R.id.seekbar_value);
        if(mShowValue) {
            mStatusText.setText(String.valueOf(mCurrentValue));
            mStatusText.setMinimumWidth(30);
        }

        TextView tvLeft = (TextView)view.findViewById(R.id.seekbar_lefttext);
        TextView tvRight = (TextView)view.findViewById(R.id.seekbar_righttext);
        TextView tvUnitsRight = (TextView)view.findViewById(R.id.seekbar_unit);
        
        tvLeft.setText(mLeftText);
        tvRight.setText(mRightText);
        
        if(mShowValue) {
            tvUnitsRight.setText(mUnitsRight);
        } else {
            tvUnitsRight.setText("");
        }
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int newValue = barToValue(progress);// + mMinValue;
		
		if(newValue > mMaxValue) {
			newValue = mMaxValue;
		} else if(newValue < mMinValue) {
			newValue = mMinValue;
		} else if(mInterval != 1 && newValue % mInterval != 0) {
			newValue = Math.round(((float)newValue)/mInterval)*mInterval;
		}
		
		// change rejected, revert to the previous value
		if(!callChangeListener(newValue)){
			seekBar.setProgress(valueToBar(mCurrentValue)); 
			return; 
		}
		
		// change accepted, store it
        mCurrentValue = newValue;

        if(mStatusText!=null && mShowValue) {
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
    
	/**
	 * Convert from bar value [0;100] to real value (mMinValue - mMaxValue).
	 * 
	 * @param b the bar value [0;100]
	 * @return the real value [mMinValue;mMaxValue]
	 */
    private int barToValue(int b) {
        float x = (float)b / 100.0f;
        float y;
        
        if(mLinear) {
            y = x;
        } else {
            y = (float)Math.sin(x*3.1415927f-3.1415927f/2.0f)/2.0f+0.5f;
        }
        
        int i = (int)Math.round((y * (mMaxValue-mMinValue)) + mMinValue);
//        Log.e("XXX","barToValue: b="+b+" i="+i+ " min="+mMinValue+" max="+mMaxValue);
        return i;
    }
    
    /**
     * Convert from real value [mMinValue;mMaxValue] to bar value [0;100].
     * 
     * @param v the real value [mMinValue;mMaxValue]
     * @return the bar value [0;100]
     */
    private int valueToBar(int v) {
        float y = (float)(v-mMinValue)/(float)(mMaxValue-mMinValue);
        float x;
        
        if(mLinear) {
            x = y;
        } else {
            x = ((float)Math.asin(2.0f*y-1.0f)+3.1415927f/2.0f)/3.1415927f;
        }

        int i = (int)Math.round(x*100.0f);
//        Log.e("XXX","valueToBar: v="+v+" i="+i+ " min="+mMinValue+" max="+mMaxValue);
        return i;
    }
}

