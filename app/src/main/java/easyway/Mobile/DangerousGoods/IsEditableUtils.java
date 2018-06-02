package easyway.Mobile.DangerousGoods;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.lang.reflect.Field;

import easyway.Mobile.util.BindView;

/**
 * Created by boy on 2017/8/14.
 */

public class IsEditableUtils {

    public static boolean isEditable = false;

    public static void edibleStateChanged(boolean isEditable, Activity act) {

        if (act instanceof Activity) {
            View rootView = act.getWindow().getDecorView();
            Field[] fields = act.getClass().getDeclaredFields();
            int count = fields.length;
            for (int i = 0; i < count; i++) {
                Field field = fields[i];
                BindView bindView = field.getAnnotation(BindView.class);
                if (bindView != null) {
                    int viewId = bindView.id();
                    field.setAccessible(true);
                    View view = rootView.findViewById(viewId);
                    if (view instanceof EditText) {
                        if (isEditable) {
                            view.setFocusableInTouchMode(isEditable);
                            view.requestFocus();
                        } else {
                            view.setFocusableInTouchMode(false);
                            view.clearFocus();
                        }
                    } else if (view instanceof Button) {
                        if (isEditable) {
                            view.setEnabled(isEditable);
                            view.setAlpha(0.88f);
                        } else {
                            view.setEnabled(isEditable);
                            view.setAlpha(1);
                        }
                    } else if (view instanceof Spinner) {
                        if (isEditable) {
                            view.setEnabled(true);
                        } else {
                            view.setEnabled(false);
                        }
                    } else if (view instanceof RadioGroup) {
                        int childCount = ((RadioGroup) view).getChildCount();
                        if (isEditable) {
                            for (int j = 0; j < childCount; j++) {
                                ((RadioGroup) view).getChildAt(j).setEnabled(true);
                            }
                        } else {
                            for (int j = 0; j < childCount; j++) {
                                ((RadioGroup) view).getChildAt(j).setEnabled(false);
                            }
                        }
                    }
                    try {
                        field.set(act, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
