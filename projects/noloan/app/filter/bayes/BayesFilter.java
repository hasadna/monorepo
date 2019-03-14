package projects.noloan.app.filter.bayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import projects.noloan.app.filter.bayes.Protos.WordInfo;
import projects.noloan.app.filter.bayes.Protos.WordInfoList;
import projects.noloan.app.filter.bayes.Protos.WordType;

public class BayesFilter {
  private static final float MIN_PROBABILITY_THRESHOLD = 0.01f;
  private static final float MAX_SPAM_THRESHOLD = 0.99f;
  private HashMap<String, WordInfo> words = new HashMap<>();
  private int totalSpamCount = 0;
  private int totalHamCount = 0;

  private double probability(List<WordInfo> message) {
    double positiveProbabilitiesProduct = 1.0f;
    double negativeProbabilitiesProduct = 1.0f;
    for (int i = 0; i < message.size(); i++) {
      WordInfo wordInfo = message.get(i);
      positiveProbabilitiesProduct *= wordInfo.getSpamProbability();
      negativeProbabilitiesProduct *= (1.0f - wordInfo.getSpamProbability());
    }
    double probabilitiesProductsSum = positiveProbabilitiesProduct + negativeProbabilitiesProduct;
    return positiveProbabilitiesProduct / probabilitiesProductsSum;
  }

  public boolean isSpam(String message) {
    return calculateBayesianProbability(makeWordList(message));
  }

  public BayesFilter(List<String> spam, List<String> ham) {
    train(spam, ham);
  }

  public BayesFilter(WordInfoList wordInfoList) {
    totalHamCount = wordInfoList.getTotalHamCount();
    totalSpamCount = wordInfoList.getTotalSpamCount();
    for (WordInfo wordInfo : wordInfoList.getMessageList()) {
      words.put(wordInfo.getWord(), wordInfo);
    }
  }

  private void train(List<String> spam, List<String> ham) {
    for (String spamWord : spam) {
      trainIteration(spamWord, WordType.SPAM);
    }
    for (String hamWord : ham) {
      trainIteration(hamWord, WordType.HAM);
    }

    for (String key : words.keySet()) {
      words.put(key, wordInfoWithProbability(words.get(key)));
    }
  }

  private String normalizeWord(String word) {
    // strip non-word characters
    word = word.replaceAll("\\W", "").toLowerCase();

    if (word.length() < 1 || word.matches("^[0-9]+$")) {
      return null;
    }

    return word;
  }

  private void trainIteration(String string, WordType type) {
    for (String word : string.split(" ")) {
      word = normalizeWord(word);

      if (word == null) {
        return;
      }

      WordInfo.Builder w;
      if (words.containsKey(word)) {
        w = words.get(word).toBuilder();
      } else {
        w = WordInfo.newBuilder();
      }
      if (type == WordType.HAM) {
        w.setHamCount(w.getHamCount() + 1);
        totalHamCount++;
      } else if (type == WordType.SPAM) {
        w.setSpamCount(w.getSpamCount() + 1);
        totalSpamCount++;
      }

      words.put(word, w.build());
    }
  }

  private ArrayList<WordInfo> makeWordList(String sms) {
    ArrayList<WordInfo> wordInfoList = new ArrayList<>();
    // space-separate words
    for (String word : sms.split(" ")) {
      word = normalizeWord(word);

      if (word == null) {
        continue;
      }

      WordInfo w;
      if (words.containsKey(word)) {
        w = words.get(word);
      } else {
        w = WordInfo.getDefaultInstance();
      }
      wordInfoList.add(w);
    }
    return wordInfoList;
  }

  /** Given wordInfo with spam and ham counts, calculate spam/ham rate and spam probability. */
  private WordInfo wordInfoWithProbability(WordInfo wordInfo) {
    float spamRate;
    float hamRate;
    float spamProbability = 0;

    spamRate = wordInfo.getSpamCount() / (float) totalSpamCount;
    hamRate = wordInfo.getHamCount() / (float) totalHamCount;
    spamProbability = spamRate / (spamRate + hamRate);

    if (spamProbability < MIN_PROBABILITY_THRESHOLD) {
      spamProbability = MIN_PROBABILITY_THRESHOLD;
    } else if (spamProbability > MAX_SPAM_THRESHOLD) {
      spamProbability = MAX_SPAM_THRESHOLD;
    }

    return wordInfo
        .toBuilder()
        .setSpamRate(spamRate)
        .setHamRate(hamRate)
        .setSpamProbability(spamProbability)
        .build();
  }

  private boolean calculateBayesianProbability(ArrayList<WordInfo> sms) {
    return probability(sms) > MAX_SPAM_THRESHOLD;
  }
}

