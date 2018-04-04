package projects.noloan.app.filter.bayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import projects.noloan.app.filter.SpamFilterAlgorithm;
import projects.noloan.app.filter.bayes.Protos.*;

public class BayesFilter implements SpamFilterAlgorithm {
  private HashMap<String, WordInfo> words = new HashMap<>();
  private BayesianProbabilityCalculationStrategy strategy;
  private int totalSpamCount = 0;
  private int totalHamCount = 0;

  interface BayesianProbabilityCalculationStrategy {
    double probability(List<Protos.WordInfo> message);
  }

  static class SimpleProbabilityStrategy implements BayesianProbabilityCalculationStrategy {

    @Override
    public double probability(List<WordInfo> message) {
      double positive_probabilities_product = 1.0f;
      double negative_probabilities_product = 1.0f;
      for (int i = 0; i < message.size(); i++) {
        WordInfo wordInfo = message.get(i);
        positive_probabilities_product *= wordInfo.getSpamProbability();
        negative_probabilities_product *= (1.0f - wordInfo.getSpamProbability());
      }
      double probabilities_products_sum = positive_probabilities_product + negative_probabilities_product;
      return positive_probabilities_product / probabilities_products_sum;
    }
  }

  static class LogProbabilityStrategy implements BayesianProbabilityCalculationStrategy {

    @Override
    public double probability(List<WordInfo> message) {
      double nu = message
              .stream()
              .mapToDouble(word ->
                      Math.log(1 - word.getSpamProbability()) -
                              Math.log(word.getSpamProbability())).sum();
      return 1/(1 + Math.exp(nu));
    }
  }

  public boolean lineIsSpam(String line) {
    return calculateBayesianProbability(makeWordList(line));
  }

  public BayesFilter(List<String> spam, List<String> ham){
    train(spam, ham);
  }

  public BayesFilter(WordInfoList wordInfoList, BayesianProbabilityCalculationStrategy strategy) {
    totalHamCount = wordInfoList.getTotalHamCount();
    totalSpamCount = wordInfoList.getTotalSpamCount();
    for (WordInfo wordInfo: wordInfoList.getMessageList()) {
      words.put(wordInfo.getWord(), wordInfo);
    }
    this.strategy = strategy;
  }

  public BayesFilter(WordInfoList wordInfoList) {
    this(wordInfoList, new SimpleProbabilityStrategy());
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

  /**
   * Given wordInfo with spam and ham counts,
   * calculate spam/ham rate and spam probability
   */
  private WordInfo wordInfoWithProbability(WordInfo wordInfo) {
    float spamRate, hamRate, spamProbability = 0;

    spamRate = wordInfo.getSpamCount() / (float) totalSpamCount;
    hamRate = wordInfo.getHamCount() / (float) totalHamCount;
    spamProbability = spamRate / (spamRate + hamRate);

    if (spamProbability < 0.01f) {
      spamProbability = 0.01f;
    } else if (spamProbability > 0.99f) {
      spamProbability = 0.99f;
    }

    return wordInfo
            .toBuilder()
            .setSpamRate(spamRate)
            .setHamRate(hamRate)
            .setSpamProbability(spamProbability)
            .build();
  }

  private boolean calculateBayesianProbability(ArrayList<WordInfo> sms) {
    return strategy.probability(sms) > 0.9f;
  }
}
