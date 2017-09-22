package com.zuhlke.ta.sentiment.pipeline.impl;

import com.zuhlke.ta.sentiment.pipeline.WordTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.util.AttributeImpl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer.TOKEN_TYPES;

public class WordTokenizerImpl implements WordTokenizer {
    private final String EMAIL_PATTERN = "([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)";

    public List<String> tokenize(String sentence) {
        final List<String> words = new ArrayList<>();
        try (UAX29URLEmailTokenizer tokenizer = new UAX29URLEmailTokenizer(new StringReader(sentence))) {
            tokenizer.reset();
            while (tokenizer.incrementToken()) {
                Iterator<AttributeImpl> attributeIterator = tokenizer.getAttributeImplsIterator();
                PackedTokenAttributeImpl attr = (PackedTokenAttributeImpl) attributeIterator.next();
                if (attr != null && isARecognisedPattern(attr)) {
                    final String word = attr.toString();
                    words.add(word);
                }
            }
            return words;
        } catch (IOException e) {
            throw new FatalError(String.format("Cannot tokenize sentence: %s", sentence), e);
        }
    }

    private boolean isARecognisedPattern(PackedTokenAttributeImpl attribute) {
        return attribute.type().equals(TOKEN_TYPES[UAX29URLEmailTokenizer.ALPHANUM]);
    }

}
