package com.zuhlke.ta.sentiment;

import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.model.WeightedWord;
import com.zuhlke.ta.sentiment.pipeline.*;
import com.zuhlke.ta.sentiment.pipeline.impl.*;
import com.zuhlke.ta.sentiment.utils.SentenceDetector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

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
    private WordTokenizer tokenizer;
    private POSTokenizer posTokenizer = new TwitterStanPOSTokenizerImp();
    private SentimentWordFinder wordFinder;
    private NGramFilter ngramFilter;

    private Enhancer irrealis;
    private Enhancer negatives;
    private Enhancer intensifiers;// max trimgrams

    private TwitterSentimentAnalyzerImpl(SentenceDetector sentenceDetector, WordTokenizer tokenizer, SentimentWordFinderImpl wordFinder, NGramFilterImpl ngramFilter, IrrealisEnhancer irrealis, NegativesEnhancer negatives, Enhancer intensifiers) {
        this.sentenceDetector = sentenceDetector;
        this.tokenizer = tokenizer;
        this.wordFinder = wordFinder;
        this.ngramFilter = ngramFilter;
        this.irrealis = irrealis;
        this.negatives = negatives;
        this.intensifiers = intensifiers;
    }

    public static TwitterSentimentAnalyzerImpl create(SentenceDetector sentenceDetector, SentimentWordFinderImpl wordFinder) throws IOException, URISyntaxException {
        return new TwitterSentimentAnalyzerImpl(sentenceDetector, new WordTokenizerImpl(), wordFinder, new NGramFilterImpl(MAX_NGRAM), new IrrealisEnhancer(), NegativesEnhancer.negativesFinder(), new IntensifiersEnhancer());
    }

    public double getSentiment(String text) {
        return stream(sentenceDetector.sentencesFrom(text))
                .map(sentence -> posTokenizer.tokenize(tokenizer
                        .tokenize(sentence)
                        .stream()
                        .collect(joining(" "))))
                .map(wordFinder::find)
                .map(ngramFilter::filterNgrams)
                .map(irrealis::enhance)
                .map(intensifiers::enhance)
                .map(negatives::enhance)
                .flatMap(Collection::stream)
                .mapToDouble(WeightedWord::score)
                .sum();
    }

}
