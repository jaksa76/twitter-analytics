package com.zuhlke.ta.sentiment.pipeline.impl;

import com.zuhlke.ta.sentiment.pipeline.WordTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.util.AttributeImpl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class WordTokenizerImpl implements WordTokenizer {
	private final String URL_PATTERN = "^(https?|ftp|file)://.*";
	private final String EMAIL_PATTERN = "([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)";
	private final String NUMBERS_PATTERN = "[0-9]*";


	public List<String> tokenize(String sentence) {
		try {
			try (Tokenizer tokenizer = new UAX29URLEmailTokenizer(new StringReader(sentence))) {
				tokenizer.reset();
                final List<String> words = new ArrayList<>();
                while (movedToNextToken(tokenizer)) {
                    final Iterator<AttributeImpl> it = tokenizer.getAttributeImplsIterator();
                    if (it.hasNext()) {
                        final AttributeImpl a = it.next();
                        if (a instanceof CharTermAttributeImpl) {
                            final String word = a.toString();
                            if (!isARecognisedPattern(word)) {
                                words.add(word);
                            }
                        }
                    }
                }
                return words;
            }
		} catch (IOException e) {
			throw new FatalError("tokenizer", e);
		}
	}

	private boolean movedToNextToken(Tokenizer tokenizer)  {
		try {
			return tokenizer.incrementToken();
		} catch (IOException e) {
			throw new FatalError("next token", e);
		}
	}

	private boolean isARecognisedPattern(String word) {
		return word.matches(EMAIL_PATTERN) || word.matches(NUMBERS_PATTERN) || word.matches(URL_PATTERN);
	}

}
