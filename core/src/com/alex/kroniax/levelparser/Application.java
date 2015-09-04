package com.alex.kroniax.levelparser;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class Application extends ApplicationAdapter {

    private Stage mStage;

    private FileChooser mFileChooser;
    private String mFilePath;

    private VisTextField mFileLabel;

    @Override
    public void create() {
        VisUI.load();

        mStage = new Stage();
        Gdx.input.setInputProcessor(mStage);

        // somewhere during app loading (look below at "Favorites storage" for
        // explanation)
        FileChooser.setFavoritesPrefsName("com.your.package.here");

        // chooser creation
        mFileChooser = new FileChooser(Mode.OPEN);
        mFileChooser.setSize(500, 500);
        mFileChooser.setSelectionMode(SelectionMode.FILES);
        mFileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(FileHandle file) {
                mFilePath = new String(file.path());
                mFileLabel.setText(mFilePath);
            }
        });

        VisTextButton bt = new VisTextButton("Open tmx file");

        // button listener

        bt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // displaying chooser with fade in animation
                mStage.addActor(mFileChooser.fadeIn());
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.add(bt).width(300).height(40);
        table.row();

        // mFileLabel = new VisTextField("Press \"Open tmx file\" to select a
        // file");
        mFileLabel = new VisTextField("/home/alex/level2.tmx");
        mFilePath = "/home/alex/level2.tmx";
        table.add(mFileLabel).width(480).padTop(25);
        table.row();

        bt = new VisTextButton("Generate");
        bt.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                Parser parser = new Parser(mFilePath);
                StringBuilder error = new StringBuilder();
                if (parser.parse(error)) {
                    DialogUtils.showOKDialog(mStage, "Generating result",
                            "The file has been successfully converted!\nThank you for contributing to Kroniax I hope you like the experience!");
                } else {
                    System.out.println("Error = " + error);
                    DialogUtils.showErrorDialog(mStage, error.toString());
                }
            }
        });
        table.add(bt).width(300).height(40).padTop(50);
        table.row();

        VisLabel info = new VisLabel(
                "The level file will be generated into the same folder with the same\nname but with the .kroniax extension");
        table.add(info).width(480);

        table.pack();
        mStage.addActor(table);
    }

    @Override
    public void render() {
        mStage.act(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mStage.draw();
    }

    public void dispose() {
        mStage.dispose();
        VisUI.dispose();
    }
}
