package com.example.cliff.fallouthackinghelper;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private final int ROWS = 12;

    // cw: WHOOPS! MASTER PASSWORDS CAN BE AS LONG AS 12 CHARS!
    // That is a must-fix, as the current layout is only good through Advanced hacking.

    private final int COLS = 12;

    private ToggleButton currentButton;
    private GridLayout grid;
    private int gridIndex;
    private int gridCount;
    private boolean inEditText = false;
    private char[][] charGrid = new char[ROWS][COLS];
    private Vector<EditText> likeVec = new Vector<>();
    private int[] likenessByRow = new int[ROWS];

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setContentView(R.layout.activity_main);

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

        // Force return key to remove focus from the EditText and return it to the Activity.
        final TextView.OnEditorActionListener editAct = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int row = getRC(v).first;

                v.clearFocus();
                // Add code to implement likeness check.
                if (v.getText().equals("0")) {
                    thisAct.handleZeroLikeness();
                } else {
                    thisAct.handleLikeness();
                }
                likenessByRow[row] = Integer.valueOf(v.getText().toString());

                return true;
            }
        };

        grid = (GridLayout) findViewById(R.id.letterGrid);
        gridCount = grid.getChildCount();
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

        ToggleButton kb = (ToggleButton) findViewById(R.id.keyButton);

        /*

            cw: Another method for soft keyboard access.

        kb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE
                );
                if (isChecked) {
                    inputMethodManager.showSoftInput(
                            currentButton, InputMethodManager.SHOW_FORCED
                    );
                    windowToken = currentButton.getWindowToken();
                } else {
                    inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
                }
                buttonView.setChecked(isChecked);
            }
        });
        */

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

    public void handleZeroLikeness() {
        Pair<Integer, Integer> rc = getRC();

        // cw: Please note that the layout is 1-based because of laziness
        int textRow = rc.first - 1;
        for (int r = 0; r < ROWS; r++) {
            if (r == textRow) continue;
            for (int c = 0; c < COLS; c++) {
                if (charGrid[r][c] == charGrid[textRow][c]) {
                    muteRow(r);
                    break;
                }
            }
        }
    }

    public void handleLikeness() {

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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (inEditText) return super.onKeyUp(keyCode, event);

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
                char c = Character.toUpperCase(event.getDisplayLabel());
                currentButton.setTextOn(String.valueOf(c));
                currentButton.setTextOff(String.valueOf(c));

                Pair<Integer, Integer> rc = getRC();
                charGrid[rc.first][rc.second] = c;
                selectNextButton();

                return true;

            case KeyEvent.KEYCODE_ENTER:
                selectNextRow();
                return true;

            case KeyEvent.KEYCODE_DEL:
                selectPrevButton();
                return true;

            // cw: Should probably add actions for arrow keys. Maybe just horizontal ones?

            default:
                return super.onKeyUp(keyCode, event);
        }
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
        // We check to insure that we wrap around properly.
        v = grid.getChildAt((gridIndex + 2) % gridCount);
        if (v instanceof ToggleButton) {
            currentButton.setChecked(false);
            currentButton = (ToggleButton)v;
            currentButton.setChecked(true);
        }

    }

}
