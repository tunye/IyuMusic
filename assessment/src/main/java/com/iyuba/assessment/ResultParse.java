package com.iyuba.assessment;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.iflytek.ise.result.Result;
import com.iflytek.ise.result.entity.Sentence;
import com.iflytek.ise.result.entity.Word;

/**
 * 语音测评使用,标识文字是否正确
 */
public class ResultParse {
    private static final String TAG = ResultParse.class.getSimpleName();

    private static int parseIndex;
    private static SpannableStringBuilder spannable;
    private static String sen;

    public static SpannableStringBuilder getSenResult(Result result, String s) {
        sen = s;
        spannable = new SpannableStringBuilder(sen);
        parseIndex = 0;

        for (Sentence sentence : result.sentences) {
            if (!sentence.content.equals("sil")) {
                for (Word word : sentence.words) {
                    if (word.total_score != 0) {
                        setWord(word);
                    }
                }
            }
        }

        return spannable;
    }

    public static void setWord(Word word) {
        String wordStr = word.content;
        if (word.total_score < 3 || word.total_score > 4) {
            int start = -1;
            int end = -1;
            int i = parseIndex;
            for (int j = 0; i < sen.length() && j < wordStr.length(); i++) {
                if (start == -1) {
                    if (sen.charAt(i) == wordStr.charAt(j)
                            || sen.charAt(i) + 32 == wordStr.charAt(j)) {
                        start = i;
                        j++;
                    }
                } else {
                    if (sen.charAt(i) == wordStr.charAt(j)) {
                        j++;
                    } else {
                        return;
                    }
                }
            }

            if (start != -1) {
                end = i;
                parseIndex = i;
                if (word.total_score < 3) {
                    setRed(start, end);
                } else if (word.total_score > 4) {
                    setGreen(start, end);
                }
            }
        } else {
            parseIndex += wordStr.length() + 1;
        }
        while (parseIndex < sen.length() && !isAlphabeta(sen.charAt(parseIndex))) {
            parseIndex++;
        }
    }

    private static boolean isAlphabeta(char c) {
        boolean result = false;
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
            result = true;
        return result;
    }

    public static void setRed(int start, int end) {
        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    public static void setGreen(int start, int end) {
        spannable.setSpan(new ForegroundColorSpan(0xff079500), start, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    }

}
