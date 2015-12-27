#!/usr/bin/perl

die "Sole argument must be numeric!\n" if !defined $ARGV[0] || $ARGV[0] =~ /\D/;

my($a) = (ord('a') - 1);
for (my $row = 1; $row <= $ARGV[0]; $row++) {
    my($r) = chr($a + $row);

    print <<MADLIB;
            <ToggleButton
				android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="45dp"
                android:textOn="A"
				android:textOff="A"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}1"
                android:layout_row="$row"
                android:layout_column="0"\ />

            <ToggleButton
				android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="B"
				android:textOff="B"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}2"
                android:layout_row="$row"
                android:layout_column="1" />

            <ToggleButton
				android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="C"
				android:textOff="C"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}3"
                android:layout_row="$row"
                android:layout_column="2" />

            <ToggleButton
				android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="D"
				android:textOff="D"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}4"
                android:layout_row="$row"
                android:layout_column="3" />

            <ToggleButton
				android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="E"
				android:textOff="E"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}5"
                android:layout_row="$row"
                android:layout_column="4" />

            <ToggleButton
				android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="F"
				android:textOff="F"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}6"
                android:layout_row="$row"
                android:layout_column="5" />

            <ToggleButton
				android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="G"
				android:textOff="G"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}7"
                android:layout_row="$row"
                android:layout_column="6" />

            <ToggleButton
				android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="H"
				android:textOff="H"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}8"
                android:layout_row="$row"
                android:layout_column="7" />

            <ToggleButton
                android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="I"
                android:textOff="I"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}9"
                android:layout_row="$row"
                android:layout_column="8" />

            <ToggleButton
                android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="J"
                android:textOff="J"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}A"
                android:layout_row="$row"
                android:layout_column="9" />

            <ToggleButton
                android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="K"
                android:textOff="K"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}B"
                android:layout_row="$row"
                android:layout_column="10" />

            <ToggleButton
                android:background="\@drawable/button_bkg"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textOn="L"
                android:textOff="L"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/button_${r}C"
                android:layout_row="$row"
                android:layout_column="11" />

            <Space
                android:layout_width="35dp"
                android:layout_height="20px"
                android:layout_row="$row"
                android:layout_column="12" />

            <EditText
                android:digits="0123456789"
                android:singleLine="true"
                android:background="#cccccc"
                android:layout_width="35dp"
                android:layout_height="wrap_content"
                android:textAppearance="?android:attr/textAppearanceMedium"
                android:textOn="9"
				android:textOff="9"
                android:textSize="22dp"
                android:textStyle="bold"
                android:id="\@+id/likeText_${r}9"
                android:layout_row="$row"
                android:layout_column="13" />

MADLIB

}