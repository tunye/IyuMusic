package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.word.ExampleSentenceOp;
import com.iyuba.music.entity.word.PersonalWordOp;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.entity.word.WordSetOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.request.discoverrequest.DictRequest;
import com.iyuba.music.request.discoverrequest.DictUpdateRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.player.SimplePlayer;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.Calendar;


/**
 * Created by 10202 on 2015/12/2.
 */
public class WordContentActivity extends BaseActivity {
    private String appointWord, source;
    private TextView key, pron, def;
    private JustifyTextView example;
    private ImageView speaker;
    private boolean fromHtml;
    private IyubaDialog waitingDialog;
    private Word currentWord;
    private ImageView wordCollect;
    private boolean collected;
    private boolean playSound;
    private WordSetOp wordSetOp;
    private SimplePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        context = this;
        wordSetOp = new WordSetOp();
        appointWord = getIntent().getStringExtra("word");
        source = getIntent().getStringExtra("source");
        playSound = false;
        player = new SimplePlayer(context);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        key = (TextView) findViewById(R.id.word_key);
        pron = (TextView) findViewById(R.id.word_pron);
        def = (TextView) findViewById(R.id.word_def);
        example = (JustifyTextView) findViewById(R.id.example);
        speaker = (ImageView) findViewById(R.id.word_speaker);
        wordCollect = (ImageView) findViewById(R.id.word_collect);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.word_searching));
    }

    @Override
    protected void setListener() {
        super.setListener();
        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, WordSetActivity.class));
            }
        });
        wordCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    if (!collected) {
                        currentWord.setUser(AccountManager.getInstance().getUserId());
                        currentWord.setCreateDate(DateFormat.formatTime(Calendar.getInstance().getTime()));
                        currentWord.setViewCount("1");
                        currentWord.setIsdelete("-1");
                        wordCollect.setBackgroundResource(R.drawable.word_collect);
                        new PersonalWordOp().saveData(currentWord);
                        synchroYun("insert");
                    } else {
                        wordCollect.setBackgroundResource(R.drawable.word_uncollect);
                        new PersonalWordOp().tryToDeleteWord(currentWord.getWord(), AccountManager.getInstance().getUserId());
                        synchroYun("delete");
                    }
                    collected = !collected;
                } else {
                    CustomDialog.showLoginDialog(context);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.word_title);
        toolbarOper.setText(R.string.word_set);
        currentWord = wordSetOp.findDataByKey(appointWord);
        if (currentWord != null) {
            currentWord.setSentences(new ExampleSentenceOp().findData(appointWord));
            if (SettingConfigManager.getInstance().isWordAutoPlay()) {
                if (TextUtils.isEmpty(currentWord.getPronMP3())) {
                    playSound = true;
                } else {
                    playSound();
                }
            }
            setContent();
            saveDB();
        } else {
            if (SettingConfigManager.getInstance().isWordAutoPlay()) {
                playSound = true;
            }
            waitingDialog.show();
        }
        if (AccountManager.getInstance().checkUserLogin()) {
            if (new PersonalWordOp().findDataByName(appointWord, AccountManager.getInstance().getUserId()) != null) {
                collected = true;
                wordCollect.setBackgroundResource(R.drawable.word_collect);
            } else {
                collected = false;
                wordCollect.setBackgroundResource(R.drawable.word_uncollect);
            }
        } else {
            collected = false;
            wordCollect.setBackgroundResource(R.drawable.word_uncollect);
        }
        getNetWord(appointWord, new IOperationResult() {
            @Override
            public void success(Object object) {
                fromHtml = true;
                if (waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                if (playSound) {
                    playSound();
                }
                setContent();
                saveDB();
            }

            @Override
            public void fail(Object object) {
                if (waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                if (currentWord == null) {
                    CustomToast.getInstance().showToast(object.toString());
                }
            }
        });
        if (SettingConfigManager.getInstance().isWordAutoAdd()) {
            if (AccountManager.getInstance().checkUserLogin()) {
                if (!collected) {
                    currentWord.setUser(AccountManager.getInstance().getUserId());
                    currentWord.setCreateDate(DateFormat.formatTime(Calendar.getInstance().getTime()));
                    currentWord.setViewCount("1");
                    currentWord.setIsdelete("-1");
                    wordCollect.setBackgroundResource(R.drawable.word_collect);
                    new PersonalWordOp().saveData(currentWord);
                    synchroYun("insert");
                    collected = true;
                }
            }
        }
    }

    protected void changeUIResumeByPara() {
        fromHtml = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.pause();
        }
        player.stopPlayback();
    }

    private void setContent() {
        if (!TextUtils.isEmpty(currentWord.getWord())) {
            key.setText(currentWord.getWord());
            if (!TextUtils.isEmpty(currentWord.getPron())) {
                if (fromHtml) {
                    pron.setText(Html.fromHtml("[" + currentWord.getPron() + "]"));
                } else {
                    pron.setText(ParameterUrl.decode("[" + currentWord.getPron() + "]"));
                }
            }
            def.setText(currentWord.getDef().replaceAll("\\n", ""));
            if (!TextUtils.isEmpty(currentWord.getExampleSentence())) {
                example.setText(Html.fromHtml(currentWord.getExampleSentence()));
            } else {
                example.setText(R.string.no_word_example);
            }
            if (!TextUtils.isEmpty(currentWord.getPronMP3())) {
                speaker.setVisibility(View.VISIBLE);
            } else {
                speaker.setVisibility(View.GONE);
            }
        } else {
            CustomToast.getInstance().showToast(R.string.word_null);
        }

    }

    private void getNetWord(final String wordkey, final IOperationResult finish) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            DictRequest.exeRequest(DictRequest.generateUrl(wordkey), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    finish.fail(msg);
                }

                @Override
                public void onServerError(String msg) {
                    finish.fail(msg);
                }

                @Override
                public void response(Object object) {
                    currentWord = (Word) object;
                    finish.success(null);
                }
            });
        } else {
            finish.fail(context.getString(R.string.net_speed_slow));
        }
    }

    private void saveDB() {
        if ("wordlist".equals(source)) {
            new PersonalWordOp().updateWord(appointWord, currentWord.getExampleSentence());
        }
        wordSetOp.updateWord(appointWord);
    }

    private void synchroYun(final String type) {
        final String userid = AccountManager.getInstance().getUserId();
        DictUpdateRequest.exeRequest(DictUpdateRequest.generateUrl(userid, type, currentWord.getWord()),
                new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {
                        CustomToast.getInstance().showToast(msg);
                    }

                    @Override
                    public void onServerError(String msg) {
                        CustomToast.getInstance().showToast(msg);
                    }

                    @Override
                    public void response(Object object) {
                        if (type.equals("insert")) {
                            new PersonalWordOp().insertWord(currentWord.getWord(), userid);
                            CustomToast.getInstance().showToast(R.string.word_add);
                        } else {
                            new PersonalWordOp().deleteWord(currentWord.getWord(), userid);
                            CustomToast.getInstance().showToast(R.string.word_delete);
                        }
                    }
                });
    }

    private void playSound() {
        if (!TextUtils.isEmpty(currentWord.getPronMP3())) {
            player.setVideoPath(currentWord.getPronMP3());
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.reset();
                }
            });
        }
    }
}
