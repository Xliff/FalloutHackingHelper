package com.example.cliff.fallouthackinghelper;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private final int ROWS = 12;
    private final int COLS = 12;

    private ToggleButton currentButton;
    private GridLayout grid;
    private int gridIndex;
    private int gridCount;
    private boolean inEditText = false;
    private char[][] charGrid = new char[ROWS][COLS];
    private Vector<EditText> likeVec = new Vector<>();
    private int[] likenessByRow = new int[ROWS];
    private SparseIntArray keySounds = new SparseIntArray();
    private SoundPool mShortPlayer= null;
    private Random rng = new Random();

    public void setEditFocus(boolean hasFocus) {
        inEditText = hasFocus;
    }

    public void hideCurrentButton() {
        currentButton.setChecked(false);
    }

    public void showCurrentButton() {
        currentButton.setChecked(true);
    }

    public char getChar(int r, int c) {
        return charGrid[r][c];
    }

    public Pair<Integer, Integer> getRC() {
        return getRC(gridIndex);
    }

    public Pair<Integer, Integer> getRC(int gi) {
        int row = gi / COLS;
        int col = gi - (row * COLS);

        return new Pair<>(row, col);
    }

    public Pair<Integer, Integer> getRC(View v) {
        return getRC(grid.indexOfChild(v));
    }

    // cw: Clear grid action. Doesn't depend on onCreate() so we define it here for cleanliness.
    final View.OnClickListener clearAct = new View.OnClickListener() {
        @Override
        public void onClick(View vd) {
            for (int i = 0; i < grid.getChildCount(); i++) {
                View v = grid.getChildAt(i);

                if (v instanceof ToggleButton) {
                    ToggleButton b = (ToggleButton) v;

                    b.setTextOn("");
                    b.setTextOff("");
                    b.setText("");
                }
            }

            // cw: Manually clear the array rather than redefine a new one.
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    charGrid[r][c] = '\0';
                }
                // Also clear helper structure values.
                likenessByRow[r] = 0;
                likeVec.get(r).setText("");
            }

            // Reset to beginning.
            currentButton.setChecked(false);
            gridIndex = 0;
            currentButton = (ToggleButton)grid.getChildAt(gridIndex);
            currentButton.setChecked(true);
        }
    };

    // cw: Toggle button action. Doesn't depend on onCreate() so we define it here for cleanliness.
    final View.OnClickListener buttonClick = new View.OnClickListener() {
        public void onClick(View v) {
            // Unhighlight previous button.
            currentButton.setChecked(false);

            // Set current button to the one clicked.
            currentButton = (ToggleButton) v;
            currentButton.setSelected(true);
            currentButton.invalidate();
            currentButton.requestFocus();

            // Update index variable.
            gridIndex = grid.indexOfChild(v);
        }
    };

    protected int getRandomKeySoundId() {
        int keyIDs[] = {
            R.raw.keyboard_1,
            R.raw.keyboard_2,
            R.raw.keyboard_3
        };

        return keyIDs[rng.nextInt(keyIDs.length)];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // cw: Deprecated in API-21, but we are shooting for API-19.
        // Please note, we only expect to use 1 sound at a time, but for those who type fast,
        // we allocate 2 extra streams, just in case.
        mShortPlayer = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

        keySounds.put(R.raw.keyboard_1, mShortPlayer.load(this, R.raw.keyboard_1, 1));
        keySounds.put(R.raw.keyboard_2, mShortPlayer.load(this, R.raw.keyboard_2, 1));
        keySounds.put(R.raw.keyboard_3, mShortPlayer.load(this, R.raw.keyboard_3, 1));

        grid = (GridLayout) findViewById(R.id.letterGrid);
        gridCount = grid.getChildCount();

        final MainActivity thisAct = this;
        final View.OnFocusChangeListener editFocus = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                thisAct.setEditFocus(hasFocus);
                if (hasFocus) {
                    thisAct.hideCurrentButton();
                    ((EditText) v).setText("");
                } else {
                    thisAct.showCurrentButton();
                }
            }
        };

        final TextView.OnEditorActionListener editAct = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // cw: Force return key to remove focus from the EditText and return it to the
                // Activity.
                v.clearFocus();

                // Likeness check.
                int row = getRC(v).first;
                if (v.getText().equals("0")) {
                    thisAct.handleZeroLikeness();
                } else {
                    thisAct.handleLikeness();
                }
                likenessByRow[row] = Integer.valueOf(v.getText().toString());

                return true;
            }
        };

        // cw: Initialization actions that need to be performed after UI layout is complete.
        // cw: But do they?!? -- May need to test this. Would clean the code up significantly if
        // I didn't need [vto].
        ViewTreeObserver vto = grid.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                grid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                for (int i = 0; i < grid.getChildCount(); i++) {
                    View v = grid.getChildAt(i);

                    if (v instanceof ToggleButton) {
                        ToggleButton b = (ToggleButton) v;

                        b.setTextOn("");
                        b.setTextOff("");
                        b.setText("");
                        b.setFocusableInTouchMode(true);
                        b.setOnClickListener(buttonClick);
                    } else if (v instanceof EditText) {
                        EditText e = (EditText) v;

                        e.setText("");
                        e.setOnFocusChangeListener(editFocus);
                        e.setOnEditorActionListener(editAct);

                    }
                }
            }
        });

        Button cb = (Button)findViewById(R.id.clearButton);
        cb.setOnClickListener(clearAct);

        ToggleButton kb = (ToggleButton) findViewById(R.id.keyButton);
        kb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );
                imm.toggleSoftInput(0, 0);
            }
        });

        gridIndex = 0;
        currentButton = (ToggleButton)grid.getChildAt(gridIndex);
        currentButton.setChecked(true);

        // cw: Helper struct for likeness computations.
        for (int gi = 0; gi < gridCount; gi++) {
            View v = grid.getChildAt(gi);
            if (!(v instanceof EditText)) continue;
            likeVec.add((EditText)v);
        }
    }

    @Override
    protected void onDestroy() {
        mShortPlayer.release();
        keySounds.clear();

        super.onDestroy();
    }

    public void handleZeroLikeness() {
        Pair<Integer, Integer> rc = getRC();

        // cw: Please note that the layout is 1-based because of laziness
        int textRow = rc.first - 1;
        for (int r = 0; r < ROWS; r++) {
            if (r == textRow) continue;
            for (int c = 0; c < COLS; c++) {
                if (charGrid[r][c] == charGrid[textRow][c]) {
                    EditText et = likeVec.get(r);
                    if (et.getText().toString().equals("")) {
                        et.setText("0");
                    }
                    muteRow(r);

                    break;
                }
            }
        }
    }

    public void handleLikeness() {
        Pair<Integer, Integer> rc = getRC();

        // cw: Please note that the layout is 1-based because of laziness
        int textRow = rc.first - 1;
        int newLikeness = Integer.valueOf(likeVec.get(textRow).getText().toString());


        for (int r = 0; r < ROWS; r++) {
            if (r == textRow) continue;

            // cw: Will have to check previous rows of likeness > 0 to see if there are common
            // characters.

            // May need a password var that tracks all rows with common letters so that we can
            // properly highlight candidates.
        }
    }

    public void muteRow(int row) {
        int gIndex = ((row + 1) * COLS) - 1;

        View v;
        do {
            v = grid.getChildAt(gIndex++);
            if (v instanceof ToggleButton) {
                // cw: Probably better to use a different selector and a new set of drawables
                // instead if programatically setting the color.
                //
                // This will do for now, though.
                v.setBackgroundColor(Color.parseColor("#333333"));
            }
        } while (v instanceof ToggleButton);
    }

    // cw: We'll need a highlightRow() [for most probable options and a restoreRow() [to
    // de-emphasize rows that may have been previously highlighted].

    public void restoreRow(int row) {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (inEditText) return super.onKeyUp(keyCode, event);

        boolean handled = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_B:
            case KeyEvent.KEYCODE_C:
            case KeyEvent.KEYCODE_D:
            case KeyEvent.KEYCODE_E:
            case KeyEvent.KEYCODE_F:
            case KeyEvent.KEYCODE_G:
            case KeyEvent.KEYCODE_H:
            case KeyEvent.KEYCODE_I:
            case KeyEvent.KEYCODE_J:
            case KeyEvent.KEYCODE_K:
            case KeyEvent.KEYCODE_L:
            case KeyEvent.KEYCODE_M:
            case KeyEvent.KEYCODE_N:
            case KeyEvent.KEYCODE_O:
            case KeyEvent.KEYCODE_P:
            case KeyEvent.KEYCODE_Q:
            case KeyEvent.KEYCODE_R:
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_T:
            case KeyEvent.KEYCODE_U:
            case KeyEvent.KEYCODE_V:
            case KeyEvent.KEYCODE_W:
            case KeyEvent.KEYCODE_X:
            case KeyEvent.KEYCODE_Y:
            case KeyEvent.KEYCODE_Z:
            case KeyEvent.KEYCODE_SPACE:
                Character c = (keyCode == KeyEvent.KEYCODE_SPACE) ?
                     null : Character.toUpperCase(event.getDisplayLabel());
                currentButton.setTextOn((c == null) ? "" : String.valueOf(c));
                currentButton.setTextOff((c == null ) ? "" : String.valueOf(c));

                Pair<Integer, Integer> rc = getRC();
                charGrid[rc.first][rc.second] = (c == null) ? '\0' : c;
                selectNextButton();
                handled = true;
                break;

            case KeyEvent.KEYCODE_ENTER:
                selectNextRow();
                handled = true;
                break;

            case KeyEvent.KEYCODE_DEL:
                selectPrevButton();
                handled = true;
                break;

            case KeyEvent.KEYCODE_SOFT_LEFT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                selectPrevButton();
                handled = true;
                break;

            case KeyEvent.KEYCODE_SOFT_RIGHT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                selectNextButton();
                handled = true;
                break;

            default:
                return super.onKeyUp(keyCode, event);
        }

        if (handled) {
            int keySoundId = keySounds.get(getRandomKeySoundId());

            mShortPlayer.play(keySoundId, 0.99f, 0.99f, 0, 0, 1);
        }

        return handled;
    }


    // The next two methods beg for optimization, but will need to be tested, first.
    private void selectNextButton() {
        View v;

        do {
            v = grid.getChildAt(++gridIndex % gridCount);
        } while (!(v instanceof ToggleButton));

        // Deselect currently selected button.
        currentButton.setChecked(false);

        // Set new current button.
        currentButton = (ToggleButton)v;
        // Highlight button
        currentButton.setChecked(true);
    }

    private void selectPrevButton() {
        View v;

        do {
            v = grid.getChildAt(--gridIndex);
            if (gridIndex < 0) {
                gridIndex = gridCount;
            }
        } while (!(v instanceof ToggleButton));

        // Deselect currently selected button.
        currentButton.setChecked(false);

        // Set new current button.
        currentButton = (ToggleButton)v;
        // Highlight button.
        currentButton.setChecked(true);
    }

    private void selectNextRow() {
        View v;

        do {
            v = grid.getChildAt(++gridIndex % gridCount);
        } while (v instanceof ToggleButton);

        // We must be at a space, so next row starts 2 children after.
        // Also check to insure that we wrap around properly.
        v = grid.getChildAt((gridIndex + 2) % gridCount);
        if (v instanceof ToggleButton) {
            currentButton.setChecked(false);
            currentButton = (ToggleButton)v;
            currentButton.setChecked(true);
        }
    }

}
