package screen.com.example.mysoftinputpan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;


import java.util.List;

import screen.com.example.mysoftinputpan.R;

import static screen.com.example.mysoftinputpan.View.KeyBoardViewUtil.KEY_CODE_ABC2NUM;
import static screen.com.example.mysoftinputpan.View.KeyBoardViewUtil.KEY_CODE_ABC_ENTER;
import static screen.com.example.mysoftinputpan.View.KeyBoardViewUtil.KEY_CODE_ABC_SPACE;
import static screen.com.example.mysoftinputpan.View.KeyBoardViewUtil.KEY_CODE_NUM2ABC;
import static screen.com.example.mysoftinputpan.View.KeyBoardViewUtil.KEY_CODE_NUM_AFFIRM;
import static screen.com.example.mysoftinputpan.View.KeyBoardViewUtil.KEY_CODE_NUM_BLANK;
import static screen.com.example.mysoftinputpan.View.KeyBoardViewUtil.KEY_CODE_NUM_POINT;

/**
 * Created by Arrow on 2017/7/26.
 */

public class KeyBoardView extends KeyboardView {

    private Context mContext;

    private static Keyboard mKeyBoard;

    public KeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public KeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    /**
     * 重新画某些按键
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mKeyBoard = KeyBoardViewUtil.getKeyBoardType();
        List<Keyboard.Key> keys = mKeyBoard.getKeys();

        for (Keyboard.Key key : keys) {
            //键盘的处理
            if (mKeyBoard.equals(KeyBoardViewUtil.numKeyboard)) {
                drawNumSpecialKey(key, canvas);
            } else if (mKeyBoard.equals(KeyBoardViewUtil.abcKeyboard)) {
                drawABCSpecialKey(key, canvas);
            }
        }
    }

    /**
     * 数字键盘
     * <p>
     *
     * @param key
     * @param canvas
     */
    private void drawNumSpecialKey(Keyboard.Key key, Canvas canvas) {
        // 左、右下角的按键
        if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
            if (KeyBoardViewUtil.getInputType() == KeyBoardViewUtil.KEY_OF_NUM_TYPE_ABC) {
                drawKeyBackground(R.drawable.select_keyboard_key_num_special_delete, canvas, key);
            } else {
                drawKeyBackground(R.drawable.select_keyboard_key_num_delete, canvas, key);
            }
        }
        if (KeyBoardViewUtil.getInputType() == KeyBoardViewUtil.KEY_OF_ABC
                || KeyBoardViewUtil.getInputType() == KeyBoardViewUtil.KEY_OF_NUM_TYPE_ABC) {
            if (key.codes[0] == -3 && key.label == null) {
                drawKeyBackground(R.drawable.select_keyboard_key_pull, canvas, key);
                drawText(canvas, key);
            }
        }
        if (key.codes[0] == KEY_CODE_NUM2ABC
                || key.codes[0] == KEY_CODE_NUM_POINT) {
            drawKeyBackground(R.drawable.select_keyboard_key, canvas, key);
            drawText(canvas, key);
        }
        if (key.codes[0] == KEY_CODE_NUM_AFFIRM) {
            drawKeyBackground(R.drawable.select_keyboard_key_affirm, canvas, key);
            drawText(canvas, key);
        }
        if (key.codes[0] == KEY_CODE_NUM_BLANK) {
            drawKeyBackground(R.color.color_dddddd, canvas, key);
            drawText(canvas, key);
        }
    }

    //字母键盘特殊处理背景
    private void drawABCSpecialKey(Keyboard.Key key, Canvas canvas) {
        if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
            drawKeyBackground(R.drawable.select_keyboard_key_delete, canvas, key);
            drawText(canvas, key);
        }
        if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
            drawKeyBackground(R.drawable.select_keyboard_key_shift, canvas, key);
            drawText(canvas, key);
        }
        if (key.codes[0] == KEY_CODE_ABC2NUM || key.codes[0] == KEY_CODE_ABC_ENTER) {
            drawKeyBackground(R.drawable.select_keyboard_special_key_abc, canvas, key);
            drawText(canvas, key);
        }
        if (key.codes[0] == KEY_CODE_ABC_SPACE) {
            drawKeyBackground(R.drawable.select_keyboard_key_space, canvas, key);
        }
    }

    private void drawKeyBackground(int drawableId, Canvas canvas, Keyboard.Key key) {
        Drawable npd = (Drawable) mContext.getResources().getDrawable(
                drawableId);
        int[] drawableState = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            npd.setState(drawableState);
        }
        npd.setBounds(key.x, key.y, key.x + key.width, key.y
                + key.height);
        npd.draw(canvas);
    }

    private void drawText(Canvas canvas, Keyboard.Key key) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        if (key.codes[0] == KEY_CODE_NUM_POINT) {
            paint.setTextSize(70);
        } else {
            paint.setTextSize(40);
        }
        paint.setAntiAlias(true);
        if (key.codes[0] == KEY_CODE_NUM_AFFIRM) {
            paint.setColor(getResources().getColor(R.color.color_ffffff));
        } else {
            paint.setColor(Color.BLACK);
        }
        if (mKeyBoard.equals(KeyBoardViewUtil.numKeyboard)) {
            if (key.label != null) {
                paint.getTextBounds(key.label.toString(), 0, key.label.toString()
                        .length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                        (key.y + key.height / 2) + bounds.height() / 2, paint);
            } else if (key.codes[0] == -3) {
                key.icon.setBounds(key.x + 9 * key.width / 20, key.y + 3
                        * key.height / 8, key.x + 11 * key.width / 20, key.y + 5
                        * key.height / 8);
                key.icon.draw(canvas);
            } else if (key.codes[0] == -5) {
                key.icon.setBounds(key.x + (int) (0.4 * key.width), key.y + (int) (0.328
                        * key.height), key.x + (int) (0.6 * key.width), key.y + (int) (0.672
                        * key.height));
                key.icon.draw(canvas);
            }
        } else if (mKeyBoard.equals(KeyBoardViewUtil.abcKeyboard)) {
            if (key.label != null) {
                paint.setColor(mContext.getResources().getColor(R.color.color_333333));
                paint.setTextSize(30);
                paint.getTextBounds(key.label.toString(), 0, key.label.toString()
                        .length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                        (key.y + key.height / 2) + bounds.height() / 2.5f, paint);
            }
        }
    }

}
