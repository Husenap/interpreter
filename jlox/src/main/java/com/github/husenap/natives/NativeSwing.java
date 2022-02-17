package com.github.husenap.natives;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.husenap.Environment;
import com.github.husenap.LoxCallable;

public class NativeSwing extends Natives {
    public NativeSwing(Environment environment) {
        super(environment);
        jframe();
        jbutton();
    }

    private void jframe() {
        define("__swing_jframe_create", 1, (i, args) -> {
            JFrame frame = new JFrame((String) args.get(0));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            return frame;
        });
        define("__swing_jframe_show", 1, (i, args) -> {
            var frame = (JFrame) args.get(0);
            frame.pack();
            frame.setVisible(true);
            return null;
        });
        define("__swing_jframe_add", 2, (i, args) -> {
            var frame = (JFrame) args.get(0);
            frame.getContentPane().add((Component) args.get(1));
            return null;
        });
    }

    private void jbutton() {
        define("__swing_jbutton_create", 1, (i, args) -> {
            var button = new JButton((String) args.get(0));
            return button;
        });
        define("__swing_jbutton_add_action_listener", 2, (i, args) -> {
            var button = (JButton) args.get(0);
            button.addActionListener(e -> {
                ((LoxCallable) args.get(1)).call(i, new ArrayList<>());
            });
            return null;
        });
    }
}
