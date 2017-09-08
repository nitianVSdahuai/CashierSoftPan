package screen.com.example.mysoftinputpan.View;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.List;

import screen.com.example.mysoftinputpan.R;
import screen.com.example.mysoftinputpan.SIZE;

/**
 * Created by Arrow on 2017/7/26.
 */

public class KeyBoardViewUtil {

    private final String TAG = KeyBoardViewUtil.this.getClass().getSimpleName();

    private Context mContext;
    private int widthPixels;
    private Activity mActivity;
    private KeyBoardView keyboardView;

    public static Keyboard abcKeyboard;// 字母键盘
    public static Keyboard numKeyboard;// 数字键盘
    public static Keyboard keyboard;//提供给keyboardView 进行画

    private View keyBoardLayout;
    private View view_line;
    private RelativeLayout TopLayout;

    private LinearLayout rootView;

    OnItemClickListener itemClick;
    KeyBoardStateChangeListener keyBoardStateChangeListener;

    public boolean isupper = false;// 是否大写
    public boolean isShow = false;

    public static final int KEYBOARD_SHOW = 1;
    public static final int KEYBOARD_HIDE = 2;

    /**
     * *左右下角keyCode对应说明
     * code:555555 -> NUM2ABC转换(数字键盘)
     * code:666666 -> ABC2NUM转换(英文字母键盘)
     * code:777777 -> 回车换行
     * code:333333 -> 右下角 确认
     * code:0 -> 左下角 空白
     * code:46 -> 左下角 点
     * code:32 -> 空格
     * code:-1 -> 切换大小写
     * code:-5 -> 删除
     */
    public static final int KEY_CODE_NUM_BLANK = 0;
    public static final int KEY_CODE_NUM_POINT = 46;
    public static final int KEY_CODE_NUM_AFFIRM = 333333;
    public static final int KEY_CODE_NUM2ABC = 555555;

    public static final int KEY_CODE_ABC_SPACE = 32;
    public static final int KEY_CODE_ABC2NUM = 666666;
    public static final int KEY_CODE_ABC_ENTER = 777777;


    // 开始输入的键盘状态设置
    @InputType
    public static int inputType = 1;// 默认
    public static final int KEY_OF_NUM_TYPE_BLANK = 1;//数字  左下角空白,右下角删除
    public static final int KEY_OF_NUM_TYPE_POINT = 2;//数字  左下角小数点,右下角删除
    public static final int KEY_OF_NUM_TYPE_ABC = 3;//数字  左下角ABC,右下角删除(可进行转换)
    public static final int KEY_OF_NUM_TYPE_DELETE = 4;//数字  左下角删除,右下角确定
    public static final int KEY_OF_ABC = 5;// 一般的abc(可进行转换)

    @IntDef({KEY_OF_NUM_TYPE_BLANK, KEY_OF_NUM_TYPE_POINT, KEY_OF_NUM_TYPE_DELETE, KEY_OF_NUM_TYPE_ABC, KEY_OF_ABC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface InputType {

    }

    private EditText et;
    private Handler showHandler;
    private TextView keyboard_tips_tv;
    private static final float TIPS_MARGIN_W = 0.0407f;

    //rootView 页面根布局
    public KeyBoardViewUtil(Context ctx, LinearLayout rootView) {
        this.mContext = ctx;
        this.mActivity = (Activity) mContext;
        this.inputType = KEY_OF_NUM_TYPE_ABC; //默认
        widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        initKeyBoardView(rootView);
        this.rootView = rootView;
    }

    private void initKeyBoardView(LinearLayout rootView) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        keyBoardLayout = inflater.inflate(R.layout.layout_keyboard_input, null);
        keyBoardLayout.setVisibility(View.GONE);
        keyBoardLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.color_ffffff));
        initLayoutHeight((LinearLayout) keyBoardLayout);
        rootView.addView(keyBoardLayout);
        if (keyBoardLayout != null && keyBoardLayout.getVisibility() == View.VISIBLE) {
            Log.d("KeyboardUtil", "visible");
        }
    }

    private void initLayoutHeight(LinearLayout layoutView) {
        LinearLayout.LayoutParams keyboard_layoutlLayoutParams = (LinearLayout.LayoutParams) layoutView
                .getLayoutParams();
        if (keyboard_layoutlLayoutParams == null) {
            int height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * SIZE.KEYBOARY_H);
            layoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height));
        } else {
            keyboard_layoutlLayoutParams.height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * SIZE.KEYBOARY_H);
        }
        view_line = (View) layoutView.findViewById(R.id.view_line);
        if (KeyBoardViewUtil.getInputType() == KeyBoardViewUtil.KEY_OF_ABC
                || KeyBoardViewUtil.getInputType() == KeyBoardViewUtil.KEY_OF_NUM_TYPE_ABC) {
            keyboard_tips_tv = (TextView) layoutView.findViewById(R.id.keyboard_tips_tv);
            setMargins(keyboard_tips_tv, (int) (widthPixels * TIPS_MARGIN_W), 0, 0, 0);
            keyboard_tips_tv.setVisibility(View.VISIBLE);
            TextView keyboard_view_finish = (TextView) layoutView.findViewById(R.id.keyboard_view_finish);
            setMargins(keyboard_view_finish, 0, 0, (int) (widthPixels * TIPS_MARGIN_W), 0);
            keyboard_view_finish.setOnClickListener(new finishListener());
            TopLayout = (RelativeLayout) layoutView.findViewById(R.id.keyboard_view_top);
            TopLayout.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams TopLayoutParams = (LinearLayout.LayoutParams) TopLayout
                    .getLayoutParams();
            if (TopLayoutParams == null) {
                int height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * SIZE.KEYBOARY_T_H);
                TopLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height));
            } else {
                TopLayoutParams.height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * SIZE.KEYBOARY_T_H);
            }
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                    .getLayoutParams();
            layoutParams.setMargins(left, top, right, bottom);
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view
                    .getLayoutParams();
            layoutParams.setMargins(left, top, right, bottom);
        }
    }

    //设置一些不需要使用这个键盘的edittext,解决切换问题
    public void setOtherEdittext(EditText... edittexts) {
        for (TextView textView : edittexts) {
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        //防止没有隐藏键盘的情况出现
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideKeyboardLayout();
                            }
                        }, 300);
                        et = (EditText) v;
                        hideKeyboardLayout();
                    }
                    return false;
                }
            });
        }
    }

    //隐藏所有布局
    public void hideAllKeyBoard() {
        hideSystemKeyBoard();
        hideKeyboardLayout();
    }

    private void hideSystemKeyBoard() {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(keyBoardLayout.getWindowToken(), 0);
    }

    //隐藏KeyBoard
    private void hideKeyboardLayout() {
        if (getKeyboardState() == true) {
            if (keyBoardLayout != null)
                keyBoardLayout.setVisibility(View.GONE);
            if (keyBoardStateChangeListener != null)
                keyBoardStateChangeListener.KeyBoardStateChange(KEYBOARD_HIDE, et);
            isShow = false;
            hideKeyboard();
            et = null;
        }
    }

    private void hideKeyboard() {
        isShow = false;
        if (keyboardView != null) {
            int visibility = keyboardView.getVisibility();
            if (visibility == View.VISIBLE) {
                keyboardView.setVisibility(View.INVISIBLE);
            }
        }
        if (keyBoardLayout != null) {
            keyBoardLayout.setVisibility(View.GONE);
        }
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }

        @Override
        public void onText(CharSequence text) {
            if (et == null) {
                return;
            }
            Editable editable = et.getText();
            int start = et.getSelectionStart();
            int end = et.getSelectionEnd();
            String temp = editable.subSequence(0, start) + text.toString() + editable.subSequence(start, editable.length());
            et.setText(temp);
            Editable etext = et.getText();
            Selection.setSelection(etext, start + 1);
        }

        @Override
        public void onRelease(int primaryCode) {
            if (inputType != KeyBoardViewUtil.KEY_OF_NUM_TYPE_ABC
                    && (primaryCode == Keyboard.KEYCODE_SHIFT)) {
                keyboardView.setPreviewEnabled(true);
            }
        }

        @Override
        public void onPress(int primaryCode) {
            //数字键盘无预览
            if (inputType == KeyBoardViewUtil.KEY_OF_NUM_TYPE_BLANK ||
                    inputType == KeyBoardViewUtil.KEY_OF_NUM_TYPE_POINT ||
                    inputType == KeyBoardViewUtil.KEY_OF_NUM_TYPE_ABC ||
                    inputType == KeyBoardViewUtil.KEY_OF_NUM_TYPE_DELETE) {
                keyboardView.setPreviewEnabled(false);
                return;
            }
            //英文键盘某些字符无预览
            if (primaryCode == Keyboard.KEYCODE_SHIFT
                    || primaryCode == Keyboard.KEYCODE_DELETE
                    || primaryCode == KEY_CODE_NUM2ABC
                    || primaryCode == KEY_CODE_ABC2NUM
                    || primaryCode == KEY_CODE_ABC_ENTER
                    || primaryCode == KEY_CODE_ABC_SPACE) {
                keyboardView.setPreviewEnabled(false);
                return;
            }
            keyboardView.setPreviewEnabled(true);
            return;
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = et.getText();
            int start = et.getSelectionStart();
            if (itemClick != null)
                itemClick.Click(primaryCode, et, editable.toString());
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 收起
                hideKeyboardLayout();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                changeKey();
                keyboardView.setKeyboard(abcKeyboard);
            } else if (primaryCode == KEY_CODE_NUM_BLANK) {
                return;
            } else if (primaryCode == KEY_CODE_NUM2ABC) {//转换数字键盘
                isupper = false;
                showKeyBoardLayout(et, KEY_OF_ABC);
            } else if (primaryCode == KEY_CODE_ABC2NUM) {//转换字母键盘
                isupper = false;
                showKeyBoardLayout(et, KEY_OF_NUM_TYPE_ABC);
            } else if (primaryCode == KEY_CODE_ABC_ENTER) {//回车处理
                editable.insert(start, "\n");
            } else if (primaryCode == KEY_CODE_NUM_AFFIRM) {//确认处理
                //暂不处理,上层回调判断
            } else if (primaryCode == KEY_CODE_NUM_POINT) {//点处理
                if (!editable.toString().contains(".")) {
                    editable.insert(start, Character.toString((char) primaryCode));
                } else {
                    return;
                }
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    /**
     * @param editText
     * @param keyBoardType 类型
     */
    public void showKeyBoardLayout(final EditText editText, @InputType int keyBoardType) {
        if (editText.equals(et) && getKeyboardState() == true && this.inputType == keyBoardType) {
            return;
        }
        this.inputType = keyBoardType;
        if (inputType == KEY_OF_ABC || inputType == KEY_OF_NUM_TYPE_ABC) {
            TopLayout.setVisibility(View.VISIBLE);
            view_line.setVisibility(View.GONE);
        } else {
            TopLayout.setVisibility(View.GONE);
            view_line.setVisibility(View.VISIBLE);
        }
        if (setKeyBoardCursorNew(editText)) {
            showHandler = new Handler();
            showHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show(editText);
                }
            }, 400);
        } else {
            //直接显示
            show(editText);
        }
    }

    private void show(EditText editText) {
        this.et = editText;
        if (keyBoardLayout != null) {
            keyBoardLayout.setVisibility(View.VISIBLE);
        }
        showKeyboard();
        if (keyBoardStateChangeListener != null) {
            keyBoardStateChangeListener.KeyBoardStateChange(KEYBOARD_SHOW, editText);
        }
    }

    private void showKeyboard() {
        if (keyboardView != null) {
            keyboardView.setVisibility(View.GONE);
        }
        initInputType();
        isShow = true;
        keyboardView.setVisibility(View.VISIBLE);
    }

    private void initInputType() {
        if (inputType == KEY_OF_NUM_TYPE_BLANK) {
            initKeyBoard(R.id.keyboards_view);
            keyboardView.setPreviewEnabled(false);
            numKeyboard = new Keyboard(mContext, R.xml.symbols_blank);
            setKeyBoard(numKeyboard);
        } else if (inputType == KEY_OF_NUM_TYPE_POINT) {
            initKeyBoard(R.id.keyboards_view);
            keyboardView.setPreviewEnabled(false);
            numKeyboard = new Keyboard(mContext, R.xml.symbols_point);
            setKeyBoard(numKeyboard);
        } else if (inputType == KEY_OF_NUM_TYPE_ABC) {
            initKeyBoard(R.id.keyboards_view);
            keyboardView.setPreviewEnabled(false);
            numKeyboard = new Keyboard(mContext, R.xml.symbols_num2abc);
            setKeyBoard(numKeyboard);
        } else if (inputType == KEY_OF_NUM_TYPE_DELETE) {
            initKeyBoard(R.id.keyboards_view);
            keyboardView.setPreviewEnabled(false);
            numKeyboard = new Keyboard(mContext, R.xml.symbols_num_delete);
            setKeyBoard(numKeyboard);
        } else if (inputType == KEY_OF_ABC) {
            initKeyBoard(R.id.keyboard_view_abc);
            keyboardView.setPreviewEnabled(true);
            abcKeyboard = new Keyboard(mContext, R.xml.symbols_abc);
            setKeyBoard(abcKeyboard);
        }
    }

    private void setKeyBoard(Keyboard newkeyboard) {
        keyboard = newkeyboard;
        keyboardView.setKeyboard(newkeyboard);
    }

    private void initKeyBoard(int keyBoardViewID) {
        mActivity = (Activity) mContext;
        keyboardView = (KeyBoardView) keyBoardLayout.findViewById(keyBoardViewID);
        keyboardView.setEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
        keyboardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        });
    }


    private boolean setKeyBoardCursorNew(EditText edit) {
        this.et = edit;
        boolean flag = false;
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
        if (isOpen) {
            if (imm.hideSoftInputFromWindow(edit.getWindowToken(), 0))
                flag = true;
        }
        if (Build.VERSION.SDK_INT <= 10) {
            edit.setInputType(android.text.InputType.TYPE_NULL);
        } else {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(edit, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keylist = abcKeyboard.getKeys();
        if (isupper) {// 大写切小写
            isupper = false;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {// 小写切大写
            isupper = true;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

    //设置监听事件
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClick = listener;
    }

    public static Keyboard getKeyBoardType() {
        return keyboard;
    }

    public static int getInputType() {
        return inputType;
    }

    public boolean getKeyboardState() {
        return this.isShow;
    }

    private void setInputType(@InputType int type) {
        inputType = type;
    }

    class finishListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            hideKeyboardLayout();
        }
    }

    /**
     * @description:TODO 输入监听
     */
    public interface OnItemClickListener {
        void Click(int onclickType, EditText editText, String content);
    }

    /**
     * 监听键盘变化
     */
    public interface KeyBoardStateChangeListener {
        void KeyBoardStateChange(int state, TextView tv);
    }

    public void setKeyBoardStateChangeListener(KeyBoardStateChangeListener listener) {
        this.keyBoardStateChangeListener = listener;
    }
}
