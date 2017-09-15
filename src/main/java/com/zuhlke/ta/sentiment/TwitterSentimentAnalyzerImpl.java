package com.zuhlke.ta.sentiment;

import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.pipeline.*;
import com.zuhlke.ta.sentiment.pipeline.impl.*;
import com.zuhlke.ta.sentiment.utils.SentenceDetector;

import java.io.IOException;
import java.net.URISyntaxException;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;


/**
 * Calculates the sentiment polarity and intensity
 * of a document. It is an implementation of the
 * work in
 * <p>
 * Maite Taboada et al. Lexicon-Based Methos for Sentiment
 * Analysis. Compuational Linguistics 37, 267-307, 2011.
 */
public class TwitterSentimentAnalyzerImpl implements SentimentAnalyzer {
    private static final int MAX_NGRAM = 4;

    private final SentenceDetector sentenceDetector;
    private final ScoreCalculator calculator = new ScoreCalculatorImpl();
    private WordTokenizer tokenizer;
    private POSTokenizer posTokenizer = new TwitterStanPOSTokenizerImp();
    private SentimentWordFinder wordFinder;
    private NGramFilter ngramFilter;

    private IrrealisFinder irrealisFinder;
    private NegativesFinder negativesFinder;
    private IntensifiersFinder intensifiersFinder;// max trimgrams

    private TwitterSentimentAnalyzerImpl(SentenceDetector sentenceDetector, WordTokenizerImpl tokenizer, SentimentWordFinderImpl wordFinder, NGramFilterImpl ngramFilter, IrrealisFinderImpl irrealisFinder, NegativesFinderImpl negativesFinder, IntensifiersFinderImpl intensifiersFinder) {
        this.sentenceDetector = sentenceDetector;
        this.tokenizer = tokenizer;
        this.wordFinder = wordFinder;
        this.ngramFilter = ngramFilter;
        this.irrealisFinder = irrealisFinder;
        this.negativesFinder = negativesFinder;
        this.intensifiersFinder = intensifiersFinder;
    }

    public static TwitterSentimentAnalyzerImpl create(SentenceDetector sentenceDetector) throws IOException, URISyntaxException {
        return new TwitterSentimentAnalyzerImpl(sentenceDetector, new WordTokenizerImpl(), new SentimentWordFinderImpl(), new NGramFilterImpl(MAX_NGRAM), new IrrealisFinderImpl(), new NegativesFinderImpl(), new IntensifiersFinderImpl());
    }

    public double getSentiment(String text) {
        return stream(sentenceDetector.sentencesFrom(text))
                .map(this::tokensFrom)
                .map(wordFinder::find)
                .map(ngramFilter::filterNgrams)
                .map(irrealisFinder::find)
                .map(intensifiersFinder::find)
                .map(negativesFinder::find)
                .mapToDouble(calculator::calculate)
                .sum();
    }

    private String[] tokensFrom(String sentence) {
        return posTokenizer.tokenize(tokenizer.tokenize(sentence).stream().collect(joining(" ")));
    }

}
