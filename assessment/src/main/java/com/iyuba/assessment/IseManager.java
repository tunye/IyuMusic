package com.iyuba.assessment;

import android.content.Context;
import android.text.SpannableStringBuilder;

import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechEvaluator;
import com.iflytek.ise.result.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

//进行语音对比打分
public class IseManager {
    public static final String AMR_SUFFIX = ".amr";
    private static final String PCM_SUFFIX = ".pcm";
    private static IseManager instance;
    private SpeechEvaluator mSpeechEvaluator;
    private String fileName;
    private String sentence;

    private IseManager(Context context) {
        // 创建评测对象
        mSpeechEvaluator = SpeechEvaluator.createEvaluator(context, null);
    }

    public static IseManager getInstance(Context context) {
        if (instance == null) {
            instance = new IseManager(context);
        }
        return instance;
    }

    private void setParams() {
        // 设置评测语种
        mSpeechEvaluator.setParameter(SpeechConstant.LANGUAGE, "en_us");
        // 设置评测题型
        mSpeechEvaluator.setParameter(SpeechConstant.ISE_CATEGORY, "read_sentence");
        // 设置试题编码类型
        mSpeechEvaluator.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mSpeechEvaluator.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        // 设置前、后端点超时
        mSpeechEvaluator.setParameter(SpeechConstant.VAD_BOS, "3000");
        mSpeechEvaluator.setParameter(SpeechConstant.VAD_EOS, "1500");
        // 设置录音超时，设置成-1 则无超时限制
        mSpeechEvaluator.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "-1");
        // 设置结果等级，不同等级对应不同的详细程度
        mSpeechEvaluator.setParameter(SpeechConstant.RESULT_LEVEL, "complete");
        mSpeechEvaluator.setParameter(SpeechConstant.ISE_AUDIO_PATH, fileName + PCM_SUFFIX);
    }

    public void startEvaluate(String sen, String filePath, EvaluatorListener mEvaluatorListener) {
        if (mSpeechEvaluator != null) {
            fileName = filePath;
            setParams();
            mSpeechEvaluator.startEvaluating(sen, null, mEvaluatorListener);
            this.sentence = sen;
        }
    }

    public void stopEvaluate() {
        if (mSpeechEvaluator.isEvaluating()) {
            mSpeechEvaluator.stopEvaluating();
        }
    }

    public void cancelEvaluate() {
        mSpeechEvaluator.cancel();
    }

    public boolean isEvaluating() {
        return mSpeechEvaluator.isEvaluating();
    }

    public void releaseResource() {
        mSpeechEvaluator.destroy();
    }

    public void transformPcmToAmr() {
        File amrFile = new File(fileName + AMR_SUFFIX);
        File pcmFile = new File(fileName + PCM_SUFFIX);
        fileCopy(pcmFile, amrFile);
        try {
            WavWriter myWavWriter = new WavWriter(amrFile, 16000);
            myWavWriter.writeHeader();
            myWavWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pcmFile.delete();
    }

    public SpannableStringBuilder getSenStyle(Result resultEva) {
        return ResultParse.getSenResult(resultEva, sentence);
    }

    private void fileCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fo != null) {
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
