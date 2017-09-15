package com.zuhlke.ta.sentiment;

import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.model.WeightedWord;
import com.zuhlke.ta.sentiment.pipeline.*;
import com.zuhlke.ta.sentiment.pipeline.impl.*;
import com.zuhlke.ta.sentiment.utils.SentenceDetector;

import java.io.IOException;
import java.net.URISyntaxException;
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

    public static TwitterSentimentAnalyzerImpl create() {
        try {
            return new TwitterSentimentAnalyzerImpl(SentenceDetector.getInstance(), new WordTokenizerImpl(), new SentimentWordFinderImpl(), new NGramFilterImpl(MAX_NGRAM), new IrrealisFinderImpl(), new NegativesFinderImpl(), new IntensifiersFinderImpl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not load WordNet");
        }
    }

    public double getSentiment(String text) {
        String[] sentences = sentenceDetector.getSentences(text);
        float result = 0;
        for (String sentence : sentences) {
            List<WeightedWord> input = getSentimentWords(getTokens(sentence));
            input = getNgramFilteredWords(input);
            input = irrealisFinder.find(input);
            input = intensifiersFinder.find(input);
            //System.out.println(input);
            input = negativesFinder.find(input);
            result += calculator.calculate(input);
        }
        return result;
    }

    private String[] getTokens(String sentence) {
        return posTokenizer.tokenize(stream(tokenizer.tokenize(sentence)).collect(joining(" ")));
    }

    private List<WeightedWord> getSentimentWords(String[] tokens) {
        return wordFinder.find(tokens);
    }

    private List<WeightedWord> getNgramFilteredWords(List<WeightedWord> words) {
        return ngramFilter.filterNgrams(words);
    }
}
