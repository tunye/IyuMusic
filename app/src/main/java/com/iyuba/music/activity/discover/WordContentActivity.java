package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.word.ExampleSentenceOp;
import com.iyuba.music.entity.word.PersonalWordOp;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.entity.word.WordSetOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.request.discoverrequest.DictRequest;
import com.iyuba.music.request.discoverrequest.DictUpdateRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.Utils;
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
    public int getLayoutId() {
        return R.layout.word;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        appointWord = getIntent().getStringExtra("word");
        source = getIntent().getStringExtra("source");
        wordSetOp = new WordSetOp();
        player = new SimplePlayer(context);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        key = findViewById(R.id.word_key);
        pron = findViewById(R.id.word_pron);
        def = findViewById(R.id.word_def);
        example = findViewById(R.id.example);
        speaker = findViewById(R.id.word_speaker);
        wordCollect = findViewById(R.id.word_collect);
        toolbarOper = findViewById(R.id.toolbar_oper);
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.word_searching));
    }

    @Override
    public void setListener() {
        super.setListener();
        speaker.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                playSound();
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, WordSetActivity.class));
            }
        });
        wordCollect.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    synchroCollect();
                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            synchroCollect();
                        }
                    });
                }
            }
        });
    }

    private void synchroCollect() {
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
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.word_title);
        enableToolbarOper(R.string.word_set);
        currentWord = wordSetOp.findDataByKey(appointWord);
        if (currentWord != null) {
            currentWord.setSentences(new ExampleSentenceOp().findData(appointWord));
            if (ConfigManager.getInstance().isWordAutoPlay()) {
                if (TextUtils.isEmpty(currentWord.getPronMP3())) {
                    playSound = true;
                } else {
                    playSound();
                }
            }
            setContent();
            saveDB();
        } else {
            if (ConfigManager.getInstance().isWordAutoPlay()) {
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
        if (ConfigManager.getInstance().isWordAutoAdd()) {
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

    public void onActivityResumed() {
        fromHtml = false;
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
        RequestClient.requestAsync(new DictRequest(wordkey), new SimpleRequestCallBack<Word>() {
            @Override
            public void onSuccess(Word word) {
                currentWord = word;
                finish.success(null);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                finish.fail(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private void saveDB() {
        if ("wordlist".equals(source)) {
            new PersonalWordOp().updateWord(appointWord, currentWord.getExampleSentence());
        }
        wordSetOp.updateWord(appointWord);
    }

    private void synchroYun(final String type) {
        final String userid = AccountManager.getInstance().getUserId();
        RequestClient.requestAsync(new DictUpdateRequest(userid, type, currentWord.getWord()), new SimpleRequestCallBack<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                if (type.equals("insert")) {
                    new PersonalWordOp().insertWord(currentWord.getWord(), userid);
                    CustomToast.getInstance().showToast(R.string.word_add);
                } else {
                    new PersonalWordOp().deleteWord(currentWord.getWord(), userid);
                    CustomToast.getInstance().showToast(R.string.word_delete);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
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