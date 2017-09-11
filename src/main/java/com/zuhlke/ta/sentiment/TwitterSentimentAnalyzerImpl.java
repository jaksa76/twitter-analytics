package com.zuhlke.ta.sentiment;

import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.model.WeightedWord;
import com.zuhlke.ta.sentiment.pipeline.*;
import com.zuhlke.ta.sentiment.pipeline.impl.*;
import com.zuhlke.ta.sentiment.utils.SentenceDetector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;


/**
 * Calculates the sentiment polarity and intensity
 * of a document. It is an implementation of the
 * work in
 * <p>
 * Maite Taboada et al. Lexicon-Based Methos for Sentiment
 * Analysis. Compuational Linguistics 37, 267-307, 2011.
 *
 * @author hadoop
 */
public class TwitterSentimentAnalyzerImpl implements SentimentAnalyzer {

    private SentenceDetector sentenceDetector;
    private ScoreCalculator calculator;
    private WordTokenizer tokenizer;
    private POSTokenizer posTokenizer;
    private SentimentWordFinder wordFinder;
    private NGramFilter ngramFilter;

    private IrrealisFinder irrealisFinder;
    private NegativesFinder negativesFinder;
    private IntensifiersFinder intensifiersFinder;

    public TwitterSentimentAnalyzerImpl() throws IOException, URISyntaxException {
        int maxNgram = 4; // max trimgrams

        sentenceDetector = SentenceDetector.getInstance();
        calculator = new ScoreCalculatorImpl();
        tokenizer = new WordTokenizerImpl();
        posTokenizer = new TwitterStanPOSTokenizerImp();
        wordFinder = new SentimentWordFinderImpl();
        ngramFilter = new NGramFilterImpl(maxNgram);
        irrealisFinder = new IrrealisFinderImpl();
        negativesFinder = new NegativesFinderImpl();
        intensifiersFinder = new IntensifiersFinderImpl();
    }

    String[] getTokens(String sentence) {
        return posTokenizer.tokenize(stream(tokenizer.tokenize(sentence)).collect(joining(" ")));
    }

    List<WeightedWord> getSentimentWords(String[] tokens) {
        return wordFinder.find(tokens);
    }

    List<WeightedWord> getNgramFilteredWords(List<WeightedWord> words) {
        return ngramFilter.filterNgrams(words);
    }

    @SuppressWarnings("Duplicates")
    public double getSentiment(String text) {
        return Arrays.stream(sentenceDetector.getSentences(text))
                .map(this::getTokens)
                .map(this::getSentimentWords)
                .map(this::getNgramFilteredWords)
                .map(words -> irrealisFinder.find(words))
                .map(words -> intensifiersFinder.find(words))
                .map(words -> negativesFinder.find(words))
                .mapToDouble(words -> calculator.calculate(words))
                .sum();
    }
}
